(ns tabl.format.markdown
  (:require
    [clojure.string :as string]
    [doric.core :refer [aligned-th aligned-td]]
    ))

(def th aligned-th)

(def td aligned-td)

(defn render [table]
  (let [spacer (str "-"
                    (string/join "-|-"
                          (map #(apply str (repeat (.length ^java.lang.String %) "-"))
                               (first table)))
                    "-")]
    (concat [(str " " (string/join " | " (first table)) " ")
             spacer]
            (for [tr (rest table)]
              (str " " (string/join " | " tr) " ")))))
