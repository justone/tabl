(ns app.json
  (:require
    [app.json.bb :as json.bb]
    )
  (:import
    [com.fasterxml.jackson.core JsonParser]
    ))

(defn json-seq
  [reader]
  (lazy-seq
    (if-let [obj (json.bb/parse-json reader true)]
      (cons obj (json-seq reader))
      (.close ^JsonParser reader))))

(defn reader->seq
  [reader]
  (-> (json.bb/json-parser reader)
      (json-seq)))
