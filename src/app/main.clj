(ns app.main
  (:require [app.edn :as edn]
            [app.json :as json]
            [app.stream :as stream]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [doric.core :as doric]
            [doric.csv]
            [doric.html]
            [doric.org]
            [doric.raw]
            [fancy.table]
            [pod-racer.core :as pod]
            [tabl.format.k8s]
            [tabl.format.markdown])
  (:import
    [java.io File])
  (:gen-class))

(def formatters
  {"fancy" fancy.table/print-table
   "org" #(->> % (doric/table {:format 'doric.org}) println)
   "csv" #(->> % (doric/table {:format 'doric.csv}) println)
   "raw" #(->> % (doric/table {:format 'doric.raw}) println)
   "html" #(->> % (doric/table {:format 'doric.html}) println)
   "md" #(->> % (doric/table {:format 'tabl.format.markdown}) println)
   "k8s" #(->> % (doric/table {:format 'tabl.format.k8s}) println)})

(def cli-options
  [["-e" "--edn" "Input is JSON"]
   ["-f" "--file FILE" "File to process instead of stdin"]
   ["-j" "--json" "Input is EDN"]
   ; ["-c" "--csv" "Input is CSV"]
   ["-m" "--mode MODE" (str "Formatting mode, available options: " (string/join ", " (keys formatters)))
    :default "fancy"
    :validate [#(contains? formatters %) (str "Must be one of " (string/join ", " (keys formatters)))]]
   ["-s" "--stream" "Stream table instead of waiting for all input"]
   ["-h" "--help"]
   ["-v" "--version" "Print version"]])

(defn print-usage
  [summary]
  (println "usage: tabl [opts]")
  (println " ")
  (println "options:")
  (println summary))

(defn find-errors
  [parsed]
  (let [{:keys [errors options]} parsed
        {:keys [file help edn json version]} options]
    (cond
      help
      {:exit 0}

      version
      {:message (string/trim (slurp (io/resource "VERSION")))
       :plain true
       :exit 0}

      errors
      {:message (string/join "\n" errors)
       :exit 1}

      (not (or edn json))
      {:message "please specify which format"
       :exit 1}

      (and file (not (.exists ^File (io/file file))))
      {:message (str "file not found: " file)
       :exit 1}
      )))

(defn print-errors
  [parsed errors]
  (let [{:keys [summary]} parsed
        {:keys [message exit plain]} errors]
    (if plain
      (println message)
      (do
        (when message
          (println message)
          (println " "))
        (print-usage summary)))
    exit))

(defn select-input
  [options]
  (let [{:keys [file]} options]
    (if file
      (io/reader file)
      *in*)))

(defn input->seq
  [options reader]
  (cond
    (:edn options)
    (edn/reader->seq reader)

    (:json options)
    (json/reader->seq reader)))

(def pod-config
  {:pod/namespaces
   [{:pod/ns "pod.tabl.fancy"
     :pod/vars [{:var/name "render-table"
                 :var/fn fancy.table/render-table}
                {:var/name "print-table"
                 :var/fn fancy.table/print-table}]}
    {:pod/ns "pod.tabl.doric"
     :pod/vars [{:var/name "table"
                 :var/fn doric/table}
                {:var/name "print-table"
                 :var/fn (fn [& args]
                           (println (apply doric/table args)))}]}]})

(defn -main [& args]
  (let [parsed (parse-opts args cli-options)
        {:keys [options]} parsed
        pod? (System/getenv "BABASHKA_POD")
        exit? (when-not pod?
                (some->> (find-errors parsed)
                         (print-errors parsed)))
        data (when-not exit?
               (->> (select-input options)
                    (input->seq options)))
        {:keys [mode stream]} options
        formatter (if stream
                    (get stream/formatters mode)
                    (get formatters mode))]
    (try
      (cond
        pod? (pod/launch pod-config)
        exit? (System/exit exit?)
        (not stream) (formatter data)
        (nil? formatter) (do (println (format "streaming with mode '%s' not supported" mode))
                             (System/exit 1))
        :else (run! println (stream/stream-seq formatter stream/default-config data)))
      (finally
        (shutdown-agents)))))
