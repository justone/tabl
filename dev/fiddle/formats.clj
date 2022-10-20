(ns fiddle.formats
  {:clj-kondo/config '{:linters {:unused-namespace {:level :off}}}}
  (:require
    [clojure.java.io :as io]

    [app.edn :as edn]
    [app.json :as json]

    [fancy.table :as table]
    [doric.core :as doric]
    ))

#_(->> (io/reader "test.json")
       json/reader->seq
       (table/render-table)
       (run! println))

#_(->> (io/reader "test.edn")
       edn/reader->seq
       (table/render-table)
       (run! println))

(def data
  [{"foo" 1, "really long field name" 2}
   {"foo" "really long field value", "really long field name" 1}
   {"foo" 3, "really long field name" 0}])

#_(println (doric/table {:format 'app.format.markdown} data))
#_(println (doric/table {:format 'app.format.k8s} data))
