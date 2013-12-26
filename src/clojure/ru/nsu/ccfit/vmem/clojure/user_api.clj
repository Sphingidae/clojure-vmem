(ns ru.nsu.ccfit.vmem.clojure.user-api
  (:use ru.nsu.ccfit.vmem.clojure.base-data-structures
        ru.nsu.ccfit.vmem.clojure.data-structures))

;;;Automatic conflict strategy analyzer for sets
(defn join-set-conflict-analyzer
  "Construct atomatic merge resolver based on user-provided functions."
  [join-set-user-added-none-conflict
   join-set-user-removed-none-conflict
   join-set-user-addrem-none-conflict
   join-set-user-added-added-conflict
   join-set-user-removed-removed-conflict
   join-set-user-added-removed-conflict
   join-set-user-addrem-addrem-conflict]

  (fn [committed pending]
    "Analyze conflicts between two version sets and choose the right strategy"
    (let [[elems1 added1 removed1] (rev-to-vset committed)
          [elems2 added2 removed2] (rev-to-vset pending)]
      (cond
        (and (empty? removed1) added1 (empty? removed2) (empty? added2))
        (join-set-user-added-none-conflict [elems1 added1 removed1] [elems2 added2 removed2])
        (and removed1 (empty? added1) (empty? removed2) (empty? added2))
        (join-set-user-removed-none-conflict [elems1 added1 removed1] [elems2 added2 removed2])
        (and removed1 added1 (empty? removed2) (empty? added2))
        (join-set-user-addrem-none-conflict [elems1 added1 removed1] [elems2 added2 removed2])
        (and (empty? removed1) added1 (empty? removed2) added2)
        (join-set-user-added-added-conflict [elems1 added1 removed1] [elems2 added2 removed2])
        (and removed1 (empty? added1) removed2 (empty? added2))
        (join-set-user-removed-removed-conflict [elems1 added1 removed1] [elems2 added2 removed2])
        (and (empty? removed1) added1 removed2 (empty? added2))
        (join-set-user-added-removed-conflict [elems1 added1 removed1] [elems2 added2 removed2])
        (and removed1 added1 removed2 added2)
        (join-set-user-addrem-addrem-conflict [elems1 added1 removed1] [elems2 added2 removed2])
        ;;if nothing equal, let's switch the arguments
        :else (recur pending committed)))
    ))


;;;Automatic conflict strategy analyzer for stacks
(defn join-stack-conflict-analyzer
  "Construct atomatic merge resolver based on user-provided functions."
  [join-stack-user-pushed-none-conflict
   join-stack-user-poped-none-conflict
   join-stack-user-pushpop-none-conflict
   join-stack-user-pushed-pushed-conflict
   join-stack-user-poped-poped-conflict
   join-stack-user-pushed-poped-conflict
   join-stack-user-pushpop-pushpop-conflict]

  (fn [committed pending]
    "Analyze conflicts between two version stacks and choose the right strategy"
    (let [[elems1 pushed1 popnum1] (rev-to-vset committed)
          [elems2 pushed2 popnum2] (rev-to-vset pending)]
      (cond
        (and (= popnum1 0) pushed1 (= popnum2 0) (empty? pushed2))
        (join-stack-user-pushed-none-conflict [elems1 pushed1 popnum1] [elems2 pushed2 popnum2])
        (and (> popnum1 0) (empty? pushed1) (= popnum2 0) (empty? pushed2))
        (join-stack-user-poped-none-conflict [elems1 pushed1 popnum1] [elems2 pushed2 popnum2])
        (and (> popnum1 0) pushed1 (= popnum2 0) (empty? pushed2))
        (join-stack-user-pushpop-none-conflict [elems1 pushed1 popnum1] [elems2 pushed2 popnum2])
        (and (= popnum1 0) pushed1 (= popnum2 0) pushed2)
        (join-stack-user-pushed-pushed-conflict [elems1 pushed1 popnum1] [elems2 pushed2 popnum2])
        (and (> popnum1 0) (empty? pushed1) (> popnum2 0) (empty? pushed2))
        (join-stack-user-poped-poped-conflict [elems1 pushed1 popnum1] [elems2 pushed2 popnum2])
        (and (= popnum1 0) pushed1 (> popnum2 0) (empty? pushed2))
        (join-stack-user-pushed-poped-conflict [elems1 pushed1 popnum1] [elems2 pushed2 popnum2])
        (and (> popnum1 0) pushed1 (> popnum2 0) pushed2)
        (join-stack-user-pushpop-pushpop-conflict [elems1 pushed1 popnum1] [elems2 pushed2 popnum2])
        ;;if nothing equal, let's switch the arguments
        :else (recur pending committed)))
    ))



;;;;Automatic conflict strategy analyzer for queues
(defn join-queue-conflict-analyzer
  "Construct atomatic merge resolver based on user-provided functions."
  [join-queue-user-added-none-conflict
   join-queue-user-removed-none-conflict
   join-queue-user-addrem-none-conflict
   join-queue-user-added-added-conflict
   join-queue-user-removed-removed-conflict
   join-queue-user-added-removed-conflict
   join-queue-user-addrem-addrem-conflict]

  (fn[committed pending]
    "Analyze conflicts between two version queues and choose the right strategy"
    (let [[elems1 added1 remnum1] (rev-to-vset committed)
          [elems2 added2 remnum2] (rev-to-vset pending)]
      (cond
        (and (= remnum1 0) added1 (= remnum2 0) (empty? added2))
        (join-queue-user-added-none-conflict [elems1 added1 remnum1] [elems2 added2 remnum2])
        (and (> remnum1 0) (empty? added1) (= remnum2 0) (empty? added2))
        (join-queue-user-removed-none-conflict [elems1 added1 remnum1] [elems2 added2 remnum2])
        (and (> remnum1 0) added1 (= remnum2 0) (empty? added2))
        (join-queue-user-addrem-none-conflict [elems1 added1 remnum1] [elems2 added2 remnum2])
        (and (= remnum1 0) added1 (= remnum2 0) added2)
        (join-queue-user-added-added-conflict [elems1 added1 remnum1] [elems2 added2 remnum2])
        (and (> remnum1 0) (empty? added1) (> remnum2 0) (empty? added2))
        (join-queue-user-removed-removed-conflict [elems1 added1 remnum1] [elems2 added2 remnum2])
        (and (= remnum1 0) added1 (> remnum2 0) (empty? added2))
        (join-queue-user-added-removed-conflict [elems1 added1 remnum1] [elems2 added2 remnum2])
        (and (> remnum1 0) added1 (> remnum2 0) added2)
        (join-queue-user-addrem-addrem-conflict [elems1 added1 remnum1] [elems2 added2 remnum2])
        ;;if nothing equal, let's switch the arguments
        :else (recur pending committed)))
    ))
