(ns app.stream
  (:require [babashka.process :refer [shell]]
            [clojure.string :as string]
            [doric.core :as doric]
            [doric.csv]
            [doric.html]
            [doric.org]
            [doric.raw]
            [fancy.table]
            [tabl.format.k8s]
            [tabl.format.markdown]))

(defn terminal-lines
  []
  (-> (shell {:out :string} "tput lines")
      :out
      string/trim
      (Integer/parseInt)))

(def formatters
  {"fancy" {:render-fn fancy.table/render-table
            :header-size 2
            :trim? false}
   "org" {:render-fn #(->> % (doric/table {:format 'doric.org}) (string/split-lines))
          :header-size 3
          :trim? false}
   "k8s" {:render-fn #(->> % (doric/table {:format 'tabl.format.k8s}) (string/split-lines))
          :header-size 1
          :trim? true}
   "raw" {:render-fn #(->> % (doric/table {:format 'doric.raw}) (string/split-lines))
          :header-size 1
          :trim? true}
   "md" {:render-fn #(->> % (doric/table {:format 'tabl.format.markdown}) (string/split-lines))
         :header-size 2
         :trim? false}
   "csv" {:render-fn #(->> % (doric/table {:format 'doric.csv}) (string/split-lines))
          :header-size 1
          :trim? false}})

(def default-config
  {:sample-window 5
   :terminal-lines-fn terminal-lines})

(def initial-state
  {:prev-header nil
   :data-sample []})

(defn window-overrun?
  "Detect if we have enough lines to fill the window and need to print the
  headers again."
  [data-line-count header-size terminal-lines]
  (<= terminal-lines (+ (inc data-line-count) header-size)))

(defn next-state
  [state formatter config data]
  (let [{:keys [header-size render-fn trim?]} formatter
        {:keys [prev-header data-sample data-line-count]} state
        {:keys [sample-window terminal-lines-fn]} config
        new-sample (take sample-window (cons data data-sample))
        table-lines (render-fn new-sample)
        header (cond->> (take header-size table-lines)
                 trim? (map string/trim))
        data-line (nth table-lines header-size)
        include-headers-because-of-scroll?
        (when terminal-lines-fn
          (window-overrun? (or data-line-count 0) header-size (terminal-lines-fn)))
        include-headers? (or (not= prev-header header)
                             include-headers-because-of-scroll?)]
    {:prev-header header
     :data-sample new-sample
     :data-line-count (if include-headers? 1 (inc data-line-count))
     :lines (cond-> []
              include-headers? (into header)
              :always (conj data-line))}))

(defn- stream-seq*
  [state formatter config data]
  (lazy-seq
    (when data
      (let [{:keys [lines] :as new-state} (next-state state formatter config (first data))]
        (concat lines (stream-seq* new-state formatter config (next data)))))))

(defn stream-seq
  [formatter config data]
  (stream-seq* initial-state formatter config data))
