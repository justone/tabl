(ns build
  (:require
    [clojure.tools.build.api :as b]
    [org.corfield.build :as cb]))

(def lib 'org.endot/tabl)
(def version (format "1.0.%s" (b/git-count-revs nil)))

(def clean cb/clean)

(defn uber
  "Build uberjar."
  [opts]
  (-> opts
      (cb/clean)
      (assoc :lib lib :version version :main 'app.main)
      (cb/uber)))
