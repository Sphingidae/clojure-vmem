(ns ru.nsu.ccfit.vmem.clojure.graph
  (:use ru.nsu.ccfit.vmem.clojure.base-data-structures
        ru.nsu.ccfit.vmem.clojure.data-structures
        clojure.set))

(defn join-set-graph-added-none-conflict [[elems1 added1 removed1] [elems2 added2 removed2]]
  (apply conj elems1 added1))    

(defn join-set-graph-removed-none-conflict [[elems1 added1 removed1] [elems2 added2 removed2]]
  (apply disj elems1 removed1))

(defn join-set-graph-addrem-none-conflict [[elems1 added1 removed1] [elems2 added2 removed2]]
  (apply disj (apply conj elems1 added1) removed1))

(defn join-set-graph-added-added-conflict [[elems1 added1 removed1] [elems2 added2 removed2]]
  (apply conj elems1 (intersection added1 added2)))

(defn join-set-graph-removed-removed-conflict [[elems1 added1 removed1] [elems2 added2 removed2]]
  (apply disj elems1 (intersection removed1 removed2)))

(defn join-set-graph-added-removed-conflict [[elems1 added1 removed1] [elems2 added2 removed2]]
  (apply disj (apply conj elems1 added1) removed2))

(defn join-set-graph-addrem-addrem-conflict[[elems1 added1 removed1] [elems2 added2 removed2]]
  (apply disj (apply conj elems1 (intersection added1 added2)) (intersection removed1 removed2)))