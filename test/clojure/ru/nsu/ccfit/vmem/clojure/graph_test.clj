(ns ru.nsu.ccfit.vmem.clojure.graph-test
  (:use clojure.test
        ru.nsu.ccfit.vmem.clojure.core
        ru.nsu.ccfit.vmem.clojure.base-data-structures
        ru.nsu.ccfit.vmem.clojure.data-structures
        ru.nsu.ccfit.vmem.clojure.graph
        ru.nsu.ccfit.vmem.clojure.user-api))

(deftest graph-set-test
  (letfn [(probe []
            (let [rf (v-ref (join-set-conflict-analyzer join-set-graph-added-none-conflict
                                                        join-set-graph-removed-none-conflict
                                                        join-set-graph-addrem-none-conflict
                                                        join-set-graph-added-added-conflict
                                                        join-set-graph-removed-removed-conflict
                                                        join-set-graph-added-removed-conflict
                                                        join-set-graph-addrem-addrem-conflict ) #{'(1 2) '(1 3) '(3 4) '(3 5)})]
              (doall (pcalls
                       (fn []
                         (v-sync
                           (v-ref-set rf (set-rem (v-deref rf) '(3 4)))))
                       (fn []
                         (v-sync
                           (v-ref-set rf (set-add (v-deref rf) '(5 6)))))))
              (is (= #{'(1 2) '(1 3) '(3 5) '(5 6)} (v-deref rf))))
            )]
    (dotimes [i 1000]
      probe)
    ))

