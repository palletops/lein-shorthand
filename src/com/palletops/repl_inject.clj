(ns com.palletops.repl-inject
  "Namespace injection for use at the repl.")

(defn inject-var
  "Return a function definition that in namespace, ns, define sym,
  defined as var, v."
  []
  `(fn [ns# sym# v#]
     (intern
      ns#
      (with-meta sym# (dissoc (meta v#) :name :ns))
      @v#)))

(defn ns-entry
  "Return a function definition that, given a namespace entry as
  either a fully qualified symbol or a 2-vector of symbol and fully
  qualified symbol, return a map with :local-sym, :var-sym and :var-ns
  keys."
  []
  `(fn [x#]
     (cond
      (symbol? x#) {:var-ns (symbol (namespace x#))
                    :local-sym (symbol (name x#))
                    :var-sym x#}
      (and (sequential? x#) (= 2 (count x#))) {:var-ns (symbol
                                                        (namespace (second x#)))
                                               :local-sym (first x#)
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
  [ns-sym-map]
  `(let [orig-ns# (ns-name *ns*)
         inject-var# ~(inject-var)
         ns-entry# ~(ns-entry)]
     (try
       (doseq [[ns-sym# syms#] '~ns-sym-map
               :let [req-nses# (group-by :var-ns (map ns-entry# syms#))]]
         (in-ns ns-sym#)
         (doseq [[req-ns# syms#] req-nses#]
           (try (require req-ns#)
                (catch Exception e#
                  (binding [*out* *err*]
                    (println "Problem loading namespace" req-ns#
                             " for injections:" (.getMessage e#)))))
           (doseq [{:keys [~'local-sym ~'var-sym]} syms#]
             (if-let [v# (resolve ~'var-sym)]
               (inject-var# ns-sym# ~'local-sym v#)
               (binding [*out* *err*]
                 (println (str "Failed to inject " (pr-str ~'var-sym))))))))
       (finally (in-ns orig-ns#)))))


(defn injections
  "Return a vector of leiningen injections for the specified ns symbol
  mappings.

      (injections {'. ['clojure.pprint/pprint]})"
  [ns-sym-map]
  [(inject-nses ns-sym-map)])
