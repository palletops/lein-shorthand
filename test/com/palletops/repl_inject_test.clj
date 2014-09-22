(ns com.palletops.repl-inject-test
  (:require [com.palletops.repl-inject :refer :all]
            [clojure.test :refer :all]))

;; Remove namespaces, so we can test the automated require of the
;; namespaces when running tests multiple times at the REPL.

(defn unload-ns [ns-sym]
  (remove-ns ns-sym)
  (dosync (commute @#'clojure.core/*loaded-libs* disj ns-sym))
  nil)

(unload-ns 'test-ns)
(unload-ns 'test-ns2)
(unload-ns 'test-ns3)
(unload-ns 'es)
(unload-ns 'ls)
(unload-ns 'inject-map)
(unload-ns 'inject-mixed)

(defmacro eager-inject-seq
  []
  `(do
     ~@(injections {'es ['test-ns/f
                         'test-ns/m]})))

(defmacro lazy-inject-seq
  []
  `(do
     ~@(injections {'ls ['test-ns2/f2]} {})
     ~@(injections {'ls ['test-ns2/m2]} {:macro true})))

(eager-inject-seq)
(lazy-inject-seq)

(defmacro inject-map
  []
  `(do
     ~@(injections {'inject-map {'f1 'test-ns/f
                                 'm 'test-ns2/m2}})))

(inject-map)

(defmacro inject-mixed
  []
  `(do
     ~@(injections {'inject-mixed [['f 'test-ns3/f3]
                                   'test-ns2/f2
                                   'test-ns3/m3]})))

(inject-mixed)

(deftest injections-test
  (testing "spec as symbol sequence"
    (testing "eager injection"
      (is (= :f (es/f)))
      (is (= '(:m) (es/m))))
    (testing "lazy injection"
      (is (= :f2 (ls/f2)) "first invocation")
      (is (= :f2 (ls/f2)))
      (is (= '(:m2) (ls/m2)) "first invocation")
      (is (= '(:m2) (ls/m2)))))
  (testing "spec as map"
    (is (= :f (inject-map/f1)))
    (is (= '(:m2) (inject-map/m))))
  (testing "inject-mixed spec"
    (is (= :f3 (inject-mixed/f)))
    (is (= :f2 (inject-mixed/f2)))
    (is (= '(:m3) (inject-mixed/m3)))))


(defmacro inject-meta-test
  [s]
  `(do
     ~@(injections (read-string s))))

(unload-ns 'test-ns4)
(unload-ns 'im)
(inject-meta-test
 "{im [^:lazy test-ns4/f4
       [^:lazy ^:macro m4 test-ns4/m4]
       [m44 ^:lazy ^:macro test-ns4/m4]]}")

(deftest inject-ns-test
  (is (= :f4 (im/f4)))
  (is (= '(:m4) (im/m4)))
  (is (= '(:m4) (im/m44))))
