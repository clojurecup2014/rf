(ns rf-input1.core
  (:require [retro-fever.input :as input]
            [enfocus.core :as ef]
            [enfocus.bind :as bind]))

(defn render-kbd
  [node kbd-state]
  (ef/at node (ef/content (prn-str kbd-state))))

(defn ^:export init
 []
 (ef/at "#input" (bind/bind-view input/kbd-state render-kbd))
 (input/init))
