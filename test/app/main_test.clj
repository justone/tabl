(ns app.main-test
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clojure.test :refer [deftest is]]
    [clojure.tools.cli :refer [parse-opts]]
    [app.main :refer :all]))

(defn errors-in
  [& args]
  (find-errors (parse-opts args cli-options)))

(deftest errors
  (is (= {:exit 0}
         (errors-in "-h")))
  (is (= {:exit 0
          :plain true
          :message (string/trim (slurp (io/resource "VERSION")))}
         (errors-in "--version")))
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
  (is (= (str "|-----+-----|\n"
              "| Foo | Baz |\n"
              "|-----+-----|\n"
              "| bar | 4   |\n"
              "| oof | 4   |\n"
              "|-----+-----|\n")
         (with-out-str (-main "-e" "-m" "org" "-f" "samples/test.edn"))))
  (is (= (str "Foo Baz\n"
              "bar 4  \n"
              "oof 4  \n")
         (with-out-str (-main "-e" "-m" "raw" "-f" "samples/test.edn"))))
  (is (= (str "Foo,Baz\n"
              "bar,4\n"
              "oof,4\n")
         (with-out-str (-main "-e" "-m" "csv" "-f" "samples/test.edn"))))
  (is (= (str "<table>\n"
              "<tr><th>Foo</th><th>Baz</th></tr>\n"
              "<tr><td>bar</td><td>4</td></tr>\n"
              "<tr><td>oof</td><td>4</td></tr>\n"
              "</table>\n")
         (with-out-str (-main "-e" "-m" "html" "-f" "samples/test.edn"))))
  (is (= (str "FOO   BAZ\n"
              "bar   4  \n"
              "oof   4  \n")
         (with-out-str (-main "-e" "-m" "k8s" "-f" "samples/test.edn"))))
  (is (= (str " Foo | Baz \n"
              "-----|-----\n"
              " bar | 4   \n"
              " oof | 4   \n")
         (with-out-str (-main "-e" "-m" "md" "-f" "samples/test.edn"))))
  )
