(defproject primes "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :min-lein-version "2.3.4"

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [om "0.6.2"]
                 [com.facebook/react "0.9.0.1"]]
  
  :plugins [[cider/cider-nrepl "0.7.0-SNAPSHOT"]
            [lein-cljsbuild "1.0.3"]]

  :hooks [leiningen.cljsbuild]

  :cljsbuild
  {:builds {:primes
            {:source-paths ["src/cljs"]
             :compiler
             {:output-to "dev-resources/public/js/primes.js"
              :optimizations :advanced
              :pretty-print false}}}})
