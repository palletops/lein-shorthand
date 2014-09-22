(defproject com.palletops/lein-inject "0.3.1"
  :description "Leiningen plugin to build injected namespaces."
  :url "https://github.com/palletops/lein-inject"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.6.0"]
                                       [leiningen "2.5.0"]]}}
  :eval-in :leiningen)
