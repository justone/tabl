(ns tabl.format.k8s
  (:require
    [clojure.string :as string]
    [doric.core :refer [aligned-th aligned-td]]
    ))

(defn th
  [col]
  (aligned-th (assoc col :title-align :left)))

(def td aligned-td)

(defn render [table]
  (concat [(str (string/join "   " (map string/upper-case (first table))))]
          (for [tr (rest table)]
            (str (string/join "   " tr)))))
