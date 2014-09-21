(ns lein-inject.plugin
  "Plugin to inject namespaces"
  (:require
   [com.palletops.repl-inject :refer [injections]]
   [leiningen.core.project :as project]
   [leiningen.repl]
   [robert.hooke :refer [add-hook]]))

(defn injection-profiles
  [project]
  {:plugin.lein-inject/injections
   {:injections (vec
                 (concat
                  (injections (:inject-ns project))
                  (injections (:inject-ns-fns project) {})
                  (injections (:inject-ns-macros project)
                              {:macro true})
                  (injections (:inject-ns-protocol-fns project)
                              {:protocol true})))}})

(defn middleware
  "Middleware to add a profile defining :injections."
  [project]
  (project/add-profiles project (injection-profiles project)))


(defn repl-hook
  [task & [project & args]]
  (apply task
         (project/merge-profiles project [:plugin.lein-inject/injections])
         args))

(defn hooks
  []
  ;; we would like to be able to hook a function after the :repl
  ;; profile is merged, but that is not very feasible at the moment.
  (add-hook #'leiningen.repl/repl repl-hook))
