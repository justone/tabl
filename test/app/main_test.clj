(ns app.main-test
  (:require
    [clojure.test :refer [deftest is]]
    [clojure.tools.cli :refer [parse-opts]]
    [app.main :refer :all]))

(defn errors-in
  [& args]
  (find-errors (parse-opts args cli-options)))

(deftest errors
  (is (= {:exit 0}
         (errors-in "-h")))
  (is (= {:exit 1
          :message "please specify which format"}
         (errors-in)))
  (is (= {:exit 1
          :message "Unknown option: \"-x\""}
         (errors-in "-x")))
  (is (= {:exit 1
          :message "file not found: samples/not-found-test.edn"}
         (errors-in "-e" "-f" "samples/not-found-test.edn")))
  )

(deftest run
  (is (= (str " :baz | :foo \n"
              "------|------\n"
              " 4    | bar  \n"
              " 4    | oof  \n")
         (with-out-str (-main "-e" "-f" "samples/test.edn"))))
  )
