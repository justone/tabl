(ns app.main
  (:require
    [clojure.java.io :as io]

    [app.edn :as edn]
    [app.json :as json]

    [fancy.table :as table]
    ))

#_(->> (io/reader "test.json")
       json/reader->seq
       (table/render-table)
       (run! println))

#_(->> (io/reader "test.edn")
       edn/reader->seq
       (table/render-table)
       (run! println))
