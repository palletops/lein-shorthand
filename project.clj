(defproject com.palletops/lein-shorthand "0.4.1-SNAPSHOT"
  :description
  "A leiningen plugin to create clojure namespaces with short names,
   so you can easily call utility functions in the REPL using fully
   qualified symbols."
  :url "https://github.com/palletops/lein-shorthand"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.6.0"]
                                       [leiningen "2.5.0"]]}}
  :eval-in :leiningen)
