(ns Vmem.core)

;;;;Structures

;Stack
(defn stack-new []
  "Default empty stack"
  (list))

(defn stack-pop [coll] 
  "Return the stack without the top element"
  (pop coll))

(defn stack-push [coll val] 
  "Return the stack with the new element inserted"
  (conj coll val))

(defn stack-top [coll] 
  "Return the top value of the stack"
  (peek coll))

;Queue
(defn queue-new []
  "Default empty queue"
  clojure.lang.PersistentQueue/EMPTY)

(defn queue-pop [coll] 
  "Return the queue without the end element"
  (pop coll))

(defn queue-push [coll val] 
  "Return the queue with the new element inserted"
  (conj coll val))

(defn queue-top [coll] 
  "Return the top value of the queue"
  (peek coll))

(defn queue-end [coll]
   "Return the end value of the queue"
   (last coll))

;Set
(defn set-new [] 
   "Default empty set"
   (hash-set))

(defn set-add [coll val] 
  "Return the set with the new element inserted"
  (conj coll val))

(defn set-rem [coll val] 
  "Return the set without element"
  (disj coll val))

;;;Version Structure Set
;(base elements, added, removed)

(defn to-version-set[coll]
  "Make version set from collection"
  [(apply hash-set coll) (hash-set) (hash-set)])

(defn from-version-set[[elems added removed]]
  "Make set from version set"
  (let [added-elems 
        (if (empty? added)
          elems
          (apply conj elems added))] 
  (if (empty? removed)
        added-elems
        (apply disj added-elems removed))))

(defn vset-add[[elems added removed] val]
  "Add element to version set structure"
     (if (contains? removed val)
          [elems added (disj removed val)]
          [elems (conj added val) removed]))

(defn vset-add-many[vset & vals]
  "Add some elements to version set structure"
  (if (empty? vals)
    vset
    (let [[val & tail] vals] 
      (apply vset-add-many 
        ;add 1 element to version set
        (vset-add vset val) tail))))

(defn vset-remove[[elems added removed] val]
  "Remove element from version set structure"
  (if (contains? added val)
    [elems (disj added val) removed]
    (if (contains? elems val)
    [elems added (conj removed val)]
    [elems added removed])))

(defn vset-remove-many[vset & vals]
  "Remove some elements from version set structure"
  (if (empty? vals)
    vset
    (let [[val & tail] vals] 
      (apply vset-remove-many 
        (vset-remove vset val) tail))))


;;;Version Structure Stack
;(base elements, pushed elements, pop count)

(defn to-version-stack[coll]
  "Make version stack from collection"
  [(apply list coll) (list) 0])

(defn from-version-stack[[elems pushed popnum]]
  "Make stack from version stack"
  (concat pushed (drop popnum elems)))

(defn vstack-push[[elems pushed popnum] val]
  "Add element to version stack structure"
  [elems (conj pushed val) popnum])

(defn vstack-push-many[vstack & vals]
  "Add some elements to version stack structure"
  (if (empty? vals)
    vstack
    (let [[val & tail] vals] 
      (apply vstack-push-many 
        ;push 1 element to version stack
        (vstack-push vstack val) tail))))

(defn vstack-pop[[elems pushed popnum]]
  "Remove element from version stack structure"
  (if (> (count pushed) 0)
    [elems (pop pushed) popnum]
    [elems pushed (+ 1 popnum)]))

(defn vstack-pop-many[vstack num]
  "Remove some elements from version stack structure"
  (if (= num 0)
    vstack
   (vstack-pop-many 
        (vstack-pop vstack) (- num 1))))

;;;Version Structure Queue
;(base elements, added, remove count)

(defn to-version-queue[coll]
  "Make version queue from collection"
  [(apply list coll) (list) 0])

(defn from-version-queue[[elems added remnum]]
  "Make queue from version queue"
  (concat (drop remnum elems) added))

(defn vqueue-add[[elems added remnum] val]
  "Add element to version queue structure"
  [elems (concat added [val]) remnum])

(defn vqueue-add-many[vqueue & vals]
  "Add some elements to version queue structure"
  (if (empty? vals)
    vqueue
    (let [[val & tail] vals] 
      (apply vqueue-add-many 
        ;Add 1 element to version queue
        (vqueue-add vqueue val) tail))))


(defn vqueue-remove[[elems added remnum]]
    "Remove element from version queue structure"
    [elems added (+ 1 remnum)])

(defn vqueue-remove-many[vqueue num]
  "Remove some elements from version queue structure"
  (if (= num 0)
    vqueue
   (vqueue-remove-many 
        (vqueue-remove vqueue) (- num 1))))

;;;Merge API

(defn split-set[coll]
  "Return version set structure"
  (to-version-set coll))

(defn join-set[[elems1 added1 removed1] [elems2 added2 removed2]]
  "Syncronize version sets and make one set from them"
  (let [rem-elems1  (if (empty? removed1)
                      elems1
                      (apply disj elems1 removed1))]
 (apply hash-set (concat (if (empty? removed2)
                           rem-elems1
                           (apply disj rem-elems1 removed2))
                         added1 added2))))

(defn split-stack[coll]
  "Return version stack structure"
  (to-version-stack coll))

(defn join-stack[[elems1 pushed1 popnum1] [elems2 pushed2 popnum2]]
  "Syncronize version stacks and make one stack from them"
  (concat pushed2 pushed1 (drop (max popnum1 popnum2) elems1)))

(defn split-queue[coll]
  "Return version queue structure"
  (to-version-queue coll))

(defn join-queue[[elems1 added1 remnum1] [elems2 added2 remnum2]]
  "Syncronize version queues and make one queue from them"
  (if (>= remnum1 remnum2)
    (concat (drop remnum1 (concat elems1 added1)) added2)
    (concat (drop remnum2 (concat elems1 added2)) added1)))


;;;API for Rev-VersionStructure convertion

;Revision list to Version Set

(defn find-rem-elem [list1 list2]
  "Find elements from list1 removed from list2"
  (reduce (fn [acc x] 
            (if (some #{x} list2)
              acc
              (conj acc x)))
     '() list1))

;((added) (deleted))
(defn compare-list[list1 list2]
  "Compare two lists to find out what elements were added/deleted"
  (list (find-rem-elem list2 list1) (find-rem-elem list1 list2)))

(defn rev-to-vset1[prev revisions result]
  "Constract version set from previous step and list of revisions"
  (if (empty? revisions)
    result
    (let [[current & tail] revisions
          [added removed] (compare-list prev current)
          new-result (apply vset-remove-many (apply vset-add-many result added) removed)]
      (rev-to-vset1 current tail new-result))))

(defn rev-to-vset[revisions]
  "Convert linked list of revisions to version set structure"
  (let [revlist (reverse (into '() (java.util.LinkedList. revisions)))
        vset (to-version-set (first revlist))]
    (rev-to-vset1 (first revlist) (rest revlist) vset)))

;Revision list to Version Stack

(defn get-common-head[stack1 stack2]
  "Find amount of poped elements and pushed elements"
  (if (or (empty? stack1) (empty? stack2) (not= (first stack1) (first stack2)))
    (list stack2 (count stack1)) 
    (get-common-head (rest stack1) (rest stack2))))

(defn compare-stack[stack1 stack2]
  "Compare two stacks to  find the difference"
  (get-common-head (reverse stack1) (reverse stack2)))

(defn rev-to-vstack1[prev revisions result]
  "Constract version stack from previous step and list of revisions"
  (if (empty? revisions)
    result
    (let [[current & tail] revisions
          [added remnum] (compare-stack prev current)
          new-result (apply vstack-push-many (vstack-pop-many result remnum) (reverse added))]
      (rev-to-vstack1 current tail new-result))))

(defn rev-to-vstack[revisions]
  "Convert linked list of revisions to version stack structure"
  (let [revlist (reverse (into '() (java.util.LinkedList. revisions)))
        vstack (to-version-stack (first revlist))]
    (rev-to-vstack1 (first revlist) (rest revlist) vstack)))

;Revision list to Version Queue

(defn get-common-body[queue1 queue2 position]
  "Find common body of two queues"
  (if (or (empty? queue2) (= queue2 (take (count queue2) queue1)))
    (list position (- (count queue1) (count queue2)))
    (get-common-body queue1 (rest queue2) (+ position 1))))
    
(defn compare-queue[queue1 queue2]
  "Compare two queues to  find the difference"
  (let [[addednum remnum] (get-common-body queue2 queue1 0)]
    (list (drop (- (count queue2) addednum) queue2) remnum)))

(defn rev-to-vqueue1[prev revisions result]
  "Constract version stack from previous step and list of revisions"
  (if (empty? revisions)
    result
    (let [[current & tail] revisions
          [added remnum] (compare-queue prev current)
          new-result (vqueue-remove-many (apply vqueue-add-many result added) remnum)]
      (rev-to-vqueue1 current tail new-result))))

(defn rev-to-vqueue[revisions]
  "Convert linked list of revisions to version stack structure"
  (let [revlist (reverse (into '() (java.util.LinkedList. revisions)))
        vqueue (to-version-queue (first revlist))]
    (rev-to-vqueue1 (first revlist) (rest revlist) vqueue)))
