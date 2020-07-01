(ns app.main
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]

    [app.json :as json]
    [app.edn :as edn]

    [clojure.tools.cli :refer [parse-opts]]
    [doric.org :as doric.org]
    [doric.csv :as doric.csv]
    [doric.raw :as doric.raw]
    [doric.html :as doric.html]
    [doric.core :as doric :refer [csv org raw html]]
    [fancy.table]
    [pod-racer.core :as pod]
    )
  (:import
    [clojure.lang LineNumberingPushbackReader]
    [java.io File])
  (:gen-class))

(def formatters
  {"fancy" fancy.table/print-table
   "org" #(->> % (doric/table {:format org}) println)
   "csv" #(->> % (doric/table {:format csv}) println)
   "raw" #(->> % (doric/table {:format raw}) println)
   "html" #(->> % (doric/table {:format html}) println)})

(def cli-options
  [["-e" "--edn" "Input is JSON"]
   ["-f" "--file FILE" "File to process instead of stdin"]
   ["-j" "--json" "Input is EDN"]
   ; ["-c" "--csv" "Input is CSV"]
   ["-m" "--mode MODE" (str "Formatting mode, available options: " (string/join ", " (keys formatters)))
    :default "fancy"
    :validate [#(contains? formatters %) (str "Must be one of " (string/join ", " (keys formatters)))]]
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
                 :var/fn (fn [ctx & args]
                           (let [{:keys [out-fn]} ctx]
                             (->> (apply fancy.table/render-table args)
                                  (run! out-fn))))
                 :racer/include-context? true}]}
    {:pod/ns "pod.tabl.doric"
     :pod/vars [{:var/name "table"
                 :var/fn doric/table}
                {:var/name "print-table"
                 :var/fn (fn [ctx & args]
                           (let [{:keys [out-fn]} ctx]
                             (out-fn (apply doric/table args))))
                 :racer/include-context? true}]}]})

(defn -main [& args]
  (let [parsed (parse-opts args cli-options)
        {:keys [options]} parsed]
    (if (System/getenv "BABASHKA_POD")
      (pod/launch pod-config)
      (or (some->> (find-errors parsed)
                   (print-errors parsed)
                   (System/exit))
          (let [data (->> (select-input options)
                          (input->seq options))
                formatter (get formatters (:mode options))]
            (formatter data))))))
