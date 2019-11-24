(ns app.edn
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    )
  (:import
    [java.io PushbackReader]
    ))

(defn edn-seq
  [reader]
  (lazy-seq
    (if-let [obj (edn/read {:eof nil} reader)]
      (cons obj (edn-seq reader))
      (.close ^PushbackReader reader))))

(defn reader->seq
  [reader]
  (-> (io/reader reader)
      (PushbackReader.)
      (edn-seq)))
