(ns com.palletops.shorthand
  "Create clojure namespaces with short names, so you can easily call
  utility functions in the REPL using fully qualified symbols.

  Transforms a map of namespace and fully qualified symbols from the
  project :shorthand key into namespace definitions specified in the
  project :injections key.")

(defn lazy-inject-var-fn
  "Return a function definition form for lazily injecting a var into a
  namespace."
  []
  `(fn ~'var-fn [ns# sym# v-sym# meta-m#]
     (intern
      ns# (with-meta sym# (merge
                           {:arglists '[[& not-yet-loaded]]}
                           meta-m#
                           ;; add meta from var if var available
                           (meta (resolve v-sym#))
                           (meta sym#)))
      (fn [& args#]
        (let [v-ns# (symbol (namespace v-sym#))]
          (try
            (require v-ns#)
            (catch Exception e#
              (throw
               (ex-info
                (str "Problem loading namespace " v-ns# " for injections:")
                {:ns ns# :name sym# :var v-sym#}
                e#)))))
        (let [src-v# (resolve v-sym#)
              tgt-v# (resolve (symbol (name ns#) (name sym#)))]
          (when-not src-v#
            (throw
             (ex-info
              (str "Failed to inject " v-sym# ". Could not resolve it.")
              {:ns ns# :name sym# :var v-sym#})))
          (alter-var-root tgt-v# (constantly src-v#))
          (alter-meta! tgt-v# merge (dissoc (meta src-v#) :name :ns))
          (apply @src-v# args#))))))

(defn inject-var-fn
  "Return a function definition that in namespace, ns, define sym,
  defined as var, v."
  []
  `(fn [ns# sym# v#]
     (intern
      ns#
      (with-meta sym# (dissoc (meta v#) :name :ns))
      v#)))

(defn ns-entry
  "Return a function definition that, given a namespace entry as
  either a fully qualified symbol or a 2-vector of symbol and fully
  qualified symbol, return a map with :local-sym, :var-sym and :var-ns
  keys."
  []
  `(fn [x#]
     (cond
      (symbol? x#) {:var-ns (symbol (namespace x#))
                    :local-sym (with-meta (symbol (name x#))
                                 (meta x#))
                    :var-sym x#}
      (and (sequential? x#) (= 2 (count x#))) {:var-ns (symbol
                                                        (namespace (second x#)))
                                               :local-sym (vary-meta
                                                           (first x#)
                                                           merge
                                                           (meta (second x#)))
                                               :var-sym (second x#)}
      :else (throw (ex-info
                    (str "Invalid namespace entry for injection.  "
                         "Must be a fully qualified symbol or 2-Tuple of "
                         "unqualified symbol and fully qualified symbol.")
                    {:value x#
                     :exit-code 1})))))

(defn inject-nses
  "Build namespaces from a map of namespace symbol to injected vars.
  Each namespace symbol maps to a either a map of unqualified symbol
  to fully qualified symbol, or to a sequence of fully qualified symbols,
  in which case the symbols' names will be used as the symbol to inject."
  [ns-sym-map meta-m]
  (let [lazy-inject-var (gensym "lazy-inject-var")
        inject-var (gensym "inject-var")
        ns-sym (gensym "ns-sym")
        req-ns (gensym "req-ns")]
    `(let [~lazy-inject-var ~(lazy-inject-var-fn)
           ~'inject-var ~(inject-var-fn)
           ns-entry# ~(ns-entry)]
       (doseq [[~ns-sym syms#] (read-string ~(binding [*print-meta* true]
                                               (pr-str ns-sym-map)))
               :let [req-nses# (group-by :var-ns (map ns-entry# syms#))]]
         (create-ns ~ns-sym)
         (doseq [[~req-ns syms#] req-nses#]
           (doseq [{:keys [~'local-sym ~'var-sym]} syms#]
             (if (or ~meta-m (:lazy (meta ~'local-sym)))
               (~lazy-inject-var ~ns-sym ~'local-sym ~'var-sym ~meta-m)
               (do
                 (try (require ~req-ns)
                      (catch Exception e#
                        (binding [*out* *err*]
                          (println "Problem loading namespace" ~req-ns
                                   " for injections:" (.getMessage e#)))))
                 (if-let [v# (resolve ~'var-sym)]
                   (~'inject-var ~ns-sym ~'local-sym v#)
                   (binding [*out* *err*]
                     (println
                      (str "Failed to inject " (pr-str ~'var-sym)))))))))))))


(defn injections
  "Return a vector of leiningen injections for the specified ns symbol
  mappings.  Specifying a metadata map, meta-m, makes the injection
  lazy.

      (injections {'. ['clojure.pprint/pprint]})"
  ([ns-sym-map meta-m]
     [(inject-nses ns-sym-map meta-m)])
  ([ns-sym-map]
     (injections ns-sym-map nil)))


;; (defn f [& args]
;;   (require 'clojure.pprint)
;;   (let [v (resolve 'clojure.pprint/pprint)]
;;     (alter-var-root #'f (constantly v))
;;     (alter-meta! #'f merge (dissoc (meta v) :name :ns))
;;     (apply @v args)))

;; (f 1)

;; (defn gg [x]
;;   :gi)


;; (defn g [& args]
;;   (let [v #'gg]
;;     (alter-var-root #'g (constantly v))
;;     (alter-meta! #'g merge (dissoc (meta v) :name :ns))))

;; (g 1)
;; (meta #'g)
