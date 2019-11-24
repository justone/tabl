(ns app.json.bb
  (:require
   [cheshire.factory :as factory]
   [cheshire.parse :as cheshire-parse]
   )
  (:import [com.fasterxml.jackson.core JsonFactory]
           [java.io Reader]))

(defn json-parser [reader]
  (.createParser ^JsonFactory factory/json-factory
                 ^Reader reader))

(defn parse-json [json-reader keywordize]
  (cheshire-parse/parse-strict json-reader keywordize nil nil))
