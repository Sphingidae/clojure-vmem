(ns ru.nsu.ccfit.vmem.clojure.base-data-structures)

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

