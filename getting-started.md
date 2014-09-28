# Getting Started

Building a game with Retro Fever.

Notice LightTable have issues with live evaluating code from this lib. We belive it to be a limitation in LightTable and whether we can work around it remains to be seen.
As an alternative we tried to get [ClojureScripts REPL][1] up and running but wasn't able to get it working.
So for now you will need the good old save (autobuild) refresh (<kbd>F5</kbd>) in the browser to test out things. This is subject to change - live eval is key!

[LightTable]: http://www.lighttable.com/
[1]: https://github.com/clojure/clojurescript/wiki/The-REPL-and-Evaluation-Environments

## Getting off the ground

    lein new rf-tut

Include ClojureScript and `retro-fever` as dependencies in your project, also for convinience add `cljsbuild` as plugin. After that you will propably have a `project.clj` in your root of the newly created project directory looking something like this:

```
(defproject rf-tut "0.1.0-SNAPSHOT"
  :description "Getting started with Retro Fever - a 2D game engine for ClojueScript"
  :url "http://example.com/FIXME"
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
          :output-to "resources/rf-tut.js"
          :optimizations :whitespace
          :pretty-print true}}]})
```

Remove the Clojure (`clj`) file:

    rm src/rf_tut/core.clj

Open LightTable and create a ClojureScript file `src/rf_tut/core.cljs` with the followin content:

```
(ns rf-tut.core
  ; This requires all the Retro Fever engine libs we are going to use
  (:require-macros [retro-fever.macros :refer [game]])
  (:require [retro-fever.core :as core]
            [retro-fever.input :as input]
            [retro-fever.sprite :as sprite]
            [retro-fever.asset :as asset]))

;; ^:export ensures that the functions keeps its name after compile to JavaScript
;; thus making it available from the HTML file
(defn ^:export init
  []
  (.log js/console "Ready for take off!"))
```

Now run:

    lein cljsbuild auto

```
<html>
  <head>
    <script src="rf-tut.js" type="text/javascript"></script>
  </head>
  <body>
    <h1>Retro Fever - Getting started</h1>
    <canvas id="game-canvas"></canvas>

    <script type="text/javascript">
        rf_tut.core.init();
    </script>
  </body>
</html>
```

Now open the HTML file in a browser of your choice, to see the message `Ready for take off!` in the console.
Note that Chrome have been used through out this guide (and the console can be opened using <kdb>F12</kdb> and must be open upon initial load for the message to be seen).

## Loading assets

Prepare an asset to load:

    mkdir -p resources/images/
    wget -O dummy_walk.png https://raw.githubusercontent.com/clojurecup2014/rf/master/demo/rf-basic-game/resources/images/dummy_walk.png?token=1869708__eyJzY29wZSI6IlJhd0Jsb2I6Y2xvanVyZWN1cDIwMTQvcmYvbWFzdGVyL2RlbW8vcmYtYmFzaWMtZ2FtZS9yZXNvdXJjZXMvaW1hZ2VzL2R1bW15X3dhbGsucG5nIiwiZXhwaXJlcyI6MTQxMjUzODk3M30%3D--e8172d16b7109d247b14ea700183c4f240794d3f

If you don't have `wget` just download the above link manual, name the file `dummy_walk.png` and put it inside `resources/images/`.

Add the following inside the `init` function in `core.cljs`:

  (asset/load-assets {:images [{:id [:dummy] :src "images/dummy_walk.png"}]})

Save the file and cljsbuild will pickup the save and rebuild `rf-tut.js`. Refresh the browser and look under the `Network` tab to see that the asset `dummy_walk.png` has been loaded.


