(ns test-ns)

(defn f []
  :f)

(defmacro m []
  :m)

(def not-me nil)

(defprotocol P (p [_]))

(meta #'p)
