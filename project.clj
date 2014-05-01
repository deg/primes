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
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 #_[org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [om "0.5.0"]
                 [com.facebook/react "0.9.0.1"]
                 ;; [TODO] Put back once cleaned up to prevent reader crash starting nrepl
                 #_[degel/degel-clojure-utils "0.1.21"]]
  
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
