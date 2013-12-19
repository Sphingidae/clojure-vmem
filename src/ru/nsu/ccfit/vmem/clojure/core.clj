(ns ru.nsu.ccfit.vmem.clojure.core
  (:import [ru.nsu.ccfit.vmem.core VRef VTransaction]))

(defn v-ref "Creates Versioned Reference for an object that
can be merged by merge handler ``handler'',
initial value may be defined."
  ([handler] (new VRef handler))
  ([handler value] (new VRef handler value)))

(defn v-ref-set
  "Must be called in a transaction. Sets the value of ref.
  Returns val."
  [ref value]
  (. ref (set value)))

(defn v-alter
  "Must be called in a transaction. Sets the in-transaction-value of
  ref to:

  (apply fun in-transaction-value-of-ref args)

  and returns the in-transaction-value of ref."
  [ref fn & args]
  (. ref (alter fn args)))

(defn v-deref
  "Returns the value of the reference ``ref''. If in transaction,
  returns changed but not yet committed value."
  [ref]
  (. ref (deref)))

(defmacro v-sync
  "Runs function in transaction."
  [& body]
  `(. VTransaction
     (runInTransaction (fn [] ~@body))))
