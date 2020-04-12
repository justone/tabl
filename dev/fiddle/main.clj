(ns fiddle.main
  (:require
    [clojure.tools.cli :refer [parse-opts]]

    [app.main :as main]))

(defn h
  [& args]
  (let [parsed (parse-opts args main/cli-options)]
    (some->> (main/find-errors parsed)
             (main/print-errors parsed))))


#_(h)
#_(h "-h")
#_(h "-x" "-h")
#_(h "-f" "not-found.edn" "-e")
#_(h "-f" "not-found.json" "-j")
