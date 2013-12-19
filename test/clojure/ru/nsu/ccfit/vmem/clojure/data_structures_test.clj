(ns ru.nsu.ccfit.vmem.clojure.data-structures-test
  (:use clojure.test
        ru.nsu.ccfit.vmem.clojure.data-structures))

;(run-tests)

(deftest stack-test
  (testing "Stack functions"
   (is (= () (stack-new)))
   (is (= '(1) (stack-push (stack-new) 1)))
   (is (= '(1) (stack-pop (stack-push (stack-push (stack-new) 1) 2))))
   (is (= 2 (stack-top (stack-push (stack-push (stack-new) 1) 2))))))

(deftest queue-test
  (testing "Queue functions"
   (is (= () (queue-new)))
   (is (= '(1) (queue-push (queue-new) 1)))
   (is (= '(2) (queue-pop (queue-push (queue-push (queue-new) 1) 2))))
   (is (= 1 (queue-top (queue-push (queue-push (queue-new) 1) 2))))
   (is (= 2 (queue-end (queue-push (queue-push (queue-new) 1) 2))))))

(deftest sets-test
  (testing "Set functions"
  (is (= #{} (set-new)))
  (is (= #{1 2} (set-add (set-add (set-new) 1) 2)))
  (is (= #{2} (set-rem (set-add (set-add (set-new) 1) 2) 1)))))

;;Version structures

(deftest vset-test
  (testing "Version set functions"
  (is (= [#{1 2} #{} #{}] (to-version-set [1 2])))
  (is (= #{1 2 3 4} (from-version-set (vset-add (to-version-set [1 2 3]) 4))))
  (is (= #{1 2 3} (from-version-set (vset-remove (to-version-set [1 2 3 4]) 4))))
  (is (= #{1 2 3 4 5} (from-version-set (vset-add-many (to-version-set [1 2 3]) 4 5))))
  (is (= #{1 2 3} (from-version-set (vset-remove-many (to-version-set [1 2 3 4 5]) 4 5))))))


(deftest vset-merge-test
  (testing "Merge version sets"
   (let [vset1 (to-version-set [1 2 3]) vset2 (to-version-set [1 2 3])]        
   (is (= #{1 2 3 4}  (join-set (vset-add vset1 4) vset2)))      
   (is (= #{1 2 3 4 5} (join-set (vset-add vset1 4) (vset-add vset2 5))))
   (is (= #{1} (join-set (vset-remove vset1 2) (vset-remove vset2 3))))
   (is (= #{1 2 4} (join-set (vset-remove vset1 3) (vset-add vset2 4))))
   (is (= #{1 4 5} (join-set (vset-remove (vset-add vset1 4) 2) (vset-add (vset-remove vset2 3) 5)))))))

(deftest vstack-test
  (testing "Version stack functions"
  (is (= ['(1 2 3) () 0] (to-version-stack '(1 2 3))))
  (is (= '(4 1 2 3) (from-version-stack (vstack-push (to-version-stack '(1 2 3)) 4))))
  (is (= '(2 3 4) (from-version-stack (vstack-pop (to-version-stack '(1 2 3 4))))))
  (is (= '(5 4 1 2 3) (from-version-stack (vstack-push-many (to-version-stack '(1 2 3)) 4 5))))
  (is (= '(3 4) (from-version-stack (vstack-pop-many (to-version-stack '(1 2 3 4)) 2))))))

(deftest vstack-merge-test
  (testing "Merge version stacks"
    (let [vstack1 (to-version-stack '(1 2 3)) vstack2 (to-version-stack '(1 2 3))]  
     (is (= '(4 1 2 3) (join-stack (vstack-push vstack1 4) vstack2)))
     (is (= '(5 4 1 2 3) (join-stack (vstack-push vstack1 4) (vstack-push vstack2 5))))
     (is (= '(3) (join-stack (vstack-pop vstack1) (vstack-pop (vstack-pop vstack2)))))
     (is (= '(4 2 3) (join-stack (vstack-push vstack1 4) (vstack-pop vstack2))))
     (is (= '(5 4 2 3) (join-stack (vstack-push (vstack-pop vstack1) 4) (vstack-push (vstack-pop vstack2) 5))))
     (is (= '(4 2 3) (join-stack (vstack-push (vstack-pop vstack1) 4) (vstack-pop (vstack-push vstack2 5))))))))

(deftest vqueue-test
  (testing "Version queue functions"
  (is (= ['(1 2 3) () 0] (to-version-queue '(1 2 3))))
  (is (= '(1 2 3 4) (from-version-queue (vqueue-add (to-version-queue '(1 2 3)) 4))))
  (is (= '(2 3 4) (from-version-queue (vqueue-remove (to-version-queue '(1 2 3 4))))))
  (is (= '(1 2 3 4 5) (from-version-queue (vqueue-add-many (to-version-queue '(1 2 3)) 4 5))))
  (is (= '(3 4) (from-version-queue (vqueue-remove-many (to-version-queue '(1 2 3 4)) 2))))
  ))

(deftest vqueue-merge-test
  (testing "Merge version queues"
    (let [vqueu1 (to-version-queue '(1 2 3)) vqueue2 (to-version-queue '(1 2 3))]  
      (is (= '(1 2 3 4) (join-queue (vqueue-add vqueu1 4) vqueue2)))
      (is (= '(1 2 3 4 5) (join-queue (vqueue-add vqueu1 4) (vqueue-add vqueue2 5))))
      (is (= '(3) (join-queue (vqueue-remove vqueu1) (vqueue-remove (vqueue-remove vqueue2)))))
      (is (= '(2 3 4) (join-queue (vqueue-add vqueu1 4) (vqueue-remove vqueue2))))
      (is (= '(2 3 4 5) (join-queue (vqueue-add (vqueue-remove vqueu1) 4) (vqueue-add (vqueue-remove vqueue2) 5))))
      (is (= '(3 4 5) (join-queue (vqueue-add (vqueue-remove (vqueue-remove vqueu1)) 4) (vqueue-add (vqueue-remove vqueue2) 5))))
      (is (= '(3 5 4) (join-queue (vqueue-add (vqueue-remove vqueu1) 4) (vqueue-add (vqueue-remove (vqueue-remove vqueue2)) 5)))))))

;Revision convertion API
(deftest rev-ver-test
  (testing "Convertation revision list to version structures"
      (is (= '(1) (find-rem-elem '(1 2 3) '(2 3 4))))
      (is (= '((4) (1)) (compare-list '(1 2 3) '(2 3 4))))
      (is (= [#{1 2 3} #{4} #{1}] (rev-to-vset (java.util.LinkedList. '((1 2 3) (2 3 4))))))
      (is (= [#{1 2 3} #{4 5 6} #{1 3}] (rev-to-vset (java.util.LinkedList. '((1 2 3) (2 3 4) (2 4 5 6))))))
      (is (= ['(1 2 3) '(4) 1] (rev-to-vstack (java.util.LinkedList. '((1 2 3) (4 2 3))))))
      (is (= ['(1 2 3) '(5 6) 2] (rev-to-vstack (java.util.LinkedList. '((1 2 3) (4 2 3) (6 5 3))))))
      (is (= ['(1 2 3 4) '(5) 1] (rev-to-vqueue (java.util.LinkedList. '((1 2 3 4) (2 3 4 5))))))
      (is (= ['(1 2 3 4) '(5 6 7) 3] (rev-to-vqueue (java.util.LinkedList. '((1 2 3 4) (2 3 4 5) (4 5 6 7))))))
      
      ))

(run-tests)
