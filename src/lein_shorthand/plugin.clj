(ns lein-shorthand.plugin
  "Plugin to inject namespaces"
  (:require
   [com.palletops.shorthand :refer [injections]]
   [leiningen.core.main :as main]
   [leiningen.core.project :as project]
   [robert.hooke :refer [add-hook]]))

(defn injection-profiles
  [project]
  {:plugin.lein-shorthand/injections
   {:injections (vec
                 (concat
                  (injections (:shorthand project))
                  (injections (:shorthand-fns project) {})
                  (injections (:shorthand-macros project)
                              {:macro true})
                  (injections (:shorthand-protocol-fns project)
                              {:protocol true})))}})

(defn middleware
  "Middleware to add a profile defining :injections."
  [project]
  (project/add-profiles project (injection-profiles project)))

;; We would like to be able to hook a function after the :repl profile
;; is merged, but that is not very feasible at the moment.

;; We also want to avoid require'ing leiningen.repl just to hook it,as
;; it can add considerably to lein startup time when not using the repl.

(defn repl-hook
  [task & [project & args]]
  (apply task
         (project/merge-profiles project [:plugin.lein-shorthand/injections])
         args))

(defn add-repl-hook  []
  (when-let [repl (resolve 'leiningen.repl/repl)]
    (add-hook repl repl-hook)))

(defn resolve-task-hook
  [f & args]
  (let [r (apply f args)]
    (add-repl-hook)
    r))


(defn hooks
  []
  (add-hook #'main/resolve-task resolve-task-hook))
