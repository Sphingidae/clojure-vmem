(ns ru.nsu.ccfit.vmem.clojure.concurrency-test
  (:use clojure.test
        ru.nsu.ccfit.vmem.clojure.core
        ru.nsu.ccfit.vmem.clojure.base-data-structures
        ru.nsu.ccfit.vmem.clojure.data-structures))

(deftest threads-set-conflict
  (letfn [(probe []
            (let [rf (v-ref (fn [committedHistory pendingHistory]
                              (let [commited-vset (rev-to-vset committedHistory)
                                    pending-vset (rev-to-vset pendingHistory)]
                                (from-version-set (join-set commited-vset pending-vset)))) #{2 3 4})]
              (doall (pcalls
                       (fn []
                         (v-sync
                           (v-ref-set rf (set-rem (v-deref rf) 3))))
                       (fn []
                         (v-sync
                           (v-ref-set rf (set-add (v-deref rf) 5))))))
              (is (= #{2 4 5} (v-deref rf))))
            )]
    (dotimes [i 1000]
      probe)
    ))

