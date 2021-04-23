(defproject dottiesbot "0.1.0-SNAPSHOT"
  :description "A PR bot for dotties.io"
  :main dottiesbot.core
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cheshire "5.10.0"]
                 [http-kit "2.5.3"]
                 [uswitch/lambada "0.1.2"]]
  :repl-options {:init-ns dottiesbot.core}

  :profiles {:uberjar {:aot :all}})
