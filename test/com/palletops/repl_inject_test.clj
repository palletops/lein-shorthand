(ns com.palletops.repl-inject-test
  (:require [com.palletops.repl-inject :refer :all]
            [clojure.test :refer :all]))

(defmacro eager-inject-seq
  []
  `(do
     ~@(injections {'es ['test-ns/f
                         'test-ns/m]})))

(defmacro lazy-inject-seq
  []
  `(do
     ~@(injections {'ls ['test-ns/f]} {})
     ~@(injections {'ls ['test-ns/m]} {:macro true})))

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

(deftest inject-nses-test
  (testing "spec as symbol sequence"
    (testing "eager injection"
      (is (= :f (es/f)))
      (is (= '(:m) (es/m))))
    (testing "lazy injection"
      (is (= :f (ls/f)) "first invocation")
      (is (= :f (ls/f)))
      (is (= '(:m) (ls/m)) "first invocation")
      (is (= '(:m) (ls/m)))))
  (testing "spec as map"
    (is (= :f (inject-map/f1)))
    (is (= '(:m2) (inject-map/m))))
  (testing "inject-mixed spec"
    (is (= :f3 (inject-mixed/f)))
    (is (= :f2 (inject-mixed/f2)))
    (is (= '(:m3) (inject-mixed/m3)))))
