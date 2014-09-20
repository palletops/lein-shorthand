(ns com.palletops.repl-inject-test
  (:require [com.palletops.repl-inject :refer :all]
            [clojure.test :refer :all]))

(defmacro fred
  []
  (first (injections {'fred ['test-ns/f 'test-ns/m]})))

(fred)

(defmacro fred2
  []
  (first (injections {'fred2 {'f 'test-ns2/f2
                              'm 'test-ns2/m2}})))

(fred2)

(defmacro fred3
  []
  (first (injections {'fred3 [['f 'test-ns3/f3]
                              'test-ns3/m3]})))

(fred3)

(deftest inject-nses-test
  (testing "spec as symbol sequence"
    (is (= :f (fred/f)))
    (is (= :m (fred/m))))
  (testing "spec as map"
    (is (= :f2 (fred2/f)))
    (is (= :m2 (fred2/m))))
  (testing "mixed spec"
    (is (= :f3 (fred3/f)))
    (is (= :m3 (fred3/m3)))))
