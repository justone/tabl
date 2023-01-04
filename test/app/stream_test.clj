(ns app.stream-test
  (:require [app.stream :as stream]
            [clojure.test :refer [deftest is testing]]))

(def sample-data
  [{:foo 1 :bar 2}
   {:foo "this is a long" :bar "this is a long"}
   {:foo 1 :bar 2}
   {:foo 3 :bar "this is a long"}
   {:foo 1 :bar 2}
   {:foo 3 :bar "this is a long"}
   {:foo 1 :bar 2}
   {:foo 1 :bar 2}
   {:foo 1 :bar 2}
   {:foo 1 :bar 2}
   {:foo 1 :bar 2}
   {:foo 1 :bar 2}
   {:foo 1 :bar 2}
   ])

(deftest formatting
  (testing "md"
    (is (= [" Foo | Bar "
            "-----|-----"
            " 1   | 2   "
            "       Foo      |       Bar      "
            "----------------|----------------"
            " this is a long | this is a long "
            " 1              | 2              "
            " 3              | this is a long "
            " 1              | 2              "
            " 3              | this is a long "
            " Foo |       Bar      "
            "-----|----------------"
            " 1   | 2              "
            " 1   | 2              "
            " 1   | 2              "
            " 1   | 2              "
            " Foo | Bar "
            "-----|-----"
            " 1   | 2   "
            " 1   | 2   "
            " 1   | 2   "]
           (stream/stream-seq (stream/formatters "md") sample-data))))

  (testing "fancy"
    (is (= [" :bar | :foo "
            "------|------"
            " 2    | 1    "
            " :bar           | :foo           "
            "----------------|----------------"
            " this is a long | this is a long "
            " 2              | 1              "
            " this is a long | 3              "
            " 2              | 1              "
            " this is a long | 3              "
            " :bar           | :foo "
            "----------------|------"
            " 2              | 1    "
            " 2              | 1    "
            " 2              | 1    "
            " 2              | 1    "
            " :bar | :foo "
            "------|------"
            " 2    | 1    "
            " 2    | 1    "
            " 2    | 1    "]
           (stream/stream-seq (stream/formatters "fancy") sample-data))))

  (testing "k8s"
    (is (= ["FOO   BAR"
            "1     2  "
            "FOO              BAR"
            "this is a long   this is a long"
            "1                2             "
            "3                this is a long"
            "1                2             "
            "3                this is a long"
            "FOO   BAR"
            "1     2             "
            "1     2             "
            "1     2             "
            "1     2             "
            "1     2  "
            "1     2  "
            "1     2  "]
           (stream/stream-seq (stream/formatters "k8s") sample-data))))
  )

