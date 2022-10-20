#!/usr/bin/env bb

(require '[babashka.pods :as pods])
(pods/load-pod "./tabl")
; (pods/load-pod ["clojure" "-M" "-m" "app.main"])

(require '[pod.tabl.fancy :as fancy])
(require '[pod.tabl.doric :as doric])

(def data
  [{:foo 1 :bar 2}
   {:foo 2 :bar 3}])

(println "fancy")
(fancy/print-table data)
(println)

(def doric-styles
  ['doric.csv
   'doric.html
   'doric.org
   'doric.raw
   'tabl.format.k8s
   'tabl.format.markdown])

(doseq [t doric-styles]
  (println t)
  (doric/print-table {:format t} data)
  (println))
