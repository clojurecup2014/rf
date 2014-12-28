(defproject rf-basic-game "0.1.0-SNAPSHOT"
  :description "Demoing a basic game using Retro Fever"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2356"]
                 [retro-fever "0.1.0-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {
    :builds [{
        :source-paths ["src"]
        :compiler {
          :output-to "resources/rf-basic-game.js"
          :optimizations :whitespace
          :pretty-print true}}]}
)
