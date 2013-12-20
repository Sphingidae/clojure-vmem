(ns ru.nsu.ccfit.vmem.clojure.integration-test
  (:use clojure.test
        ru.nsu.ccfit.vmem.clojure.data-structures
        ru.nsu.ccfit.vmem.clojure.core))

(deftest integration_test
  (is (= #{1 2} (v-deref (v-ref join-set #{1 2})))))