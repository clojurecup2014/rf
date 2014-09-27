(ns rf-input1.core
  (:require [retro-fever.input :as input]
            [enfocus.core :as ef]
            [enfocus.bind :as bind]))

(defn render-kbd
  [node kbd-state]
  (ef/at node (ef/content (prn-str kbd-state))))

(defn render-a-pressed
  [node kbd-state]
  (ef/at node (ef/content (if (input/key-pressed? :a) "yes" "no"))))

(defn ^:export init
 []
 (ef/at "#input" (bind/bind-view input/kbd-state render-kbd))
 (ef/at "#answer" (bind/bind-view input/kbd-state render-a-pressed))
 (input/init))
