(defproject rf-input1 "0.1.0-SNAPSHOT"
  :description "Demo of the input module for Retro Fever"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2356"]
                 [retro-fever "0.1.0-SNAPSHOT"]
                 [enfocus "2.1.0"]]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {
    :builds [{
        :source-paths ["src"]
        :compiler {
          :output-to "resources/rf-input1.js"
          :optimizations :whitespace
          :pretty-print true}}]}
)
