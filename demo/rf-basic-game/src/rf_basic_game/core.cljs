(ns rf-basic-game.core
  (:require-macros [retro-fever.macros :refer [game]])
  (:require [retro-fever.core :as core]
            [retro-fever.input :as input]
            [retro-fever.sprite :as sprite]))

(def game-state (atom {}))

(defn update-fn []
  nil)

(defn render-fn [context]
  (sprite/draw-image context (get-in @game-state [:images :background]) 0 0)
  (sprite/render context (get-in @game-state [:character])))

(defn ^:export init []
  (.log js/console "Launching a basic game")
  (core/init-canvas "game-canvas" 640 480)
  (input/init)

  ; Load resources
  (swap! game-state assoc-in [:images :background] (sprite/load-image "images/background.png"))
  (swap! game-state assoc-in [:images :dummy] (sprite/load-image "images/dummy_walk.png"))
  (swap! game-state assoc-in [:character] (sprite/sprite (get-in @game-state [:images :dummy])
                                                         50 80 320 420))

  ; Setup game loop
  (game core/game-loop update-fn render-fn)
)