(ns app.stream
  (:require ;; [babashka.process :refer [$] :as process]
            [clojure.string :as string]
            [doric.core :as doric]
            [doric.csv]
            [doric.html]
            [doric.org]
            [doric.raw]
            [fancy.table]
            [tabl.format.k8s]
            [tabl.format.markdown]))

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

;; TODO: make configurable
(def sample-window 5)

(def initial-state
  {:prev-header nil
   :data-sample []})

(defn next-state
  [state formatter data]
  (let [{:keys [header-size render-fn trim?]} formatter
        {:keys [prev-header data-sample]} state
        new-sample (take sample-window (cons data data-sample))
        table-lines (render-fn new-sample)
        header (cond->> (take header-size table-lines)
                 trim? (map string/trim))
        data-line (nth table-lines header-size)]
    {:prev-header header
     :data-sample new-sample
     :lines (cond-> []
              (not= prev-header header) (into header)
              :always (conj data-line))}))

(defn- stream-seq*
  [formatter data state]
  (lazy-seq
    (when data
      (let [{:keys [lines] :as new-state} (next-state state formatter (first data))]
        (concat lines (stream-seq* formatter (next data) new-state))))))

(defn stream-seq
  [formatter data]
  (stream-seq* formatter data initial-state))
