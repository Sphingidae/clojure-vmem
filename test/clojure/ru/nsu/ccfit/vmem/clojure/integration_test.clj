(ns ru.nsu.ccfit.vmem.clojure.integration-test
  (:use clojure.test
        ru.nsu.ccfit.vmem.clojure.data-structures
        ru.nsu.ccfit.vmem.clojure.core))

(deftest reference_test
  (is (= #{1 2} (v-deref (v-ref join-set #{1 2})))))

(deftest transactions_test
  (is
    (= #{1 3 5 7}
      (do
        (def rf (v-ref join-set #{1 2}))
        (v-sync
          (v-ref-set rf #{1 3 5 7}))
        (v-deref rf)
        ))
    ))