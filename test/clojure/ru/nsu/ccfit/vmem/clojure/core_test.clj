(ns ru.nsu.ccfit.vmem.clojure.core-test
  (:use clojure.test)
  (:use ru.nsu.ccfit.vmem.clojure.core))

(deftest leinworks_test
  (is (= 1 1)))

(deftest vref_create
  (is (v-ref (fn [x y z] y) 1)))