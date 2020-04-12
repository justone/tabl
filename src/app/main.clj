(ns app.main
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]

    [app.json :as json]
    [app.edn :as edn]

    [clojure.tools.cli :refer [parse-opts]]
    [fancy.table :as table])
  (:import
    [clojure.lang LineNumberingPushbackReader]
    [java.io File])
  (:gen-class))

(def cli-options
  [["-e" "--edn" "Input is JSON"]
   ["-f" "--file FILE" "File to process instead of stdin"]
   ["-j" "--json" "Input is EDN"]
   ; ["-c" "--csv" "Input is CSV"]
   ["-h" "--help"]])

(defn print-usage
  [summary]
  (println "usage: tabl [opts]")
  (println " ")
  (println "options:")
  (println summary))

(defn find-errors
  [parsed]
  (let [{:keys [errors options]} parsed
        {:keys [file help edn json]} options]
    (cond
      help
      {:exit 0}

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
        {:keys [message exit]} errors]
    (when message
      (println message)
      (println " "))
    (print-usage summary)
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

(defn -main [& args]
  (let [parsed (parse-opts args cli-options)
        {:keys [options]} parsed]
    (or (some->> (find-errors parsed)
                 (print-errors parsed)
                 (System/exit))
        (->> (select-input options)
             (input->seq options)
             (table/print-table)))))
