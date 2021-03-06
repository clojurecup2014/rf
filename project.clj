(defproject retro-fever "0.1.0-SNAPSHOT"
  :description "A 2D game engine targeting modern browsers and mobile devices"
  :url "http://rf.clojurecup.com"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2356"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {
    :builds [{
        :source-paths ["src"]
        :compiler {
          :output-to "resources/retro-fever.js"
          :optimizations :whitespace
          :pretty-print true}}]}
)
