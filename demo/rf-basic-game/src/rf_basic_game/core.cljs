(ns rf-basic-game.core
  (:require-macros [retro-fever.macros :refer [game]])
  (:require [retro-fever.core :as core]
            [retro-fever.input :as input]
            [retro-fever.sprite :as sprite]
            [retro-fever.asset :as asset]))

(def game-state (atom {:environment {:gravity 0.6}}))

(defn check-for-movement [character]
  (merge character
         (cond
          (input/key-pressed? :left) {:velocity-x -3 :state :walking :orientation :left}
          (input/key-pressed? :right) {:velocity-x 3 :state :walking :orientation :right}
          :else {:velocity-x 0 :state :idle})))

(defn check-for-jumping [character]
  (let [jumping? (< (:y character) 420)]
    (merge character
           (when (and (or (input/key-pressed? :space) (input/key-pressed? :up)) (not jumping?))
             {:state :jumping :velocity-y -10})
           (when jumping? {:state :jumping}))))

(defn check-for-landing [{:keys [state y] :as character}]
  (merge character
         (when (and (= state :jumping) (>= y 420))
           {:state :idle :velocity-y 0 :y 420})))

(defn consider-forces [{:keys [state velocity-y] :as character}]
  (merge character
         (when (= state :jumping)
           {:velocity-y (+ velocity-y (get-in @game-state [:environment :gravity]))})))

(defn set-animation [{:keys [state orientation] :as character} old-character]
  (if (and (= (:state old-character) state) (= (:orientation old-character) orientation))
    character
    (assoc character :animation (asset/get-animation [:character state orientation]))))

(defn update-character [character]
  (-> character
      check-for-landing
      check-for-movement
      check-for-jumping
      sprite/move
      consider-forces
      (set-animation character)))

(defn update-fn []
  (swap! game-state update-in [:character] sprite/update))

(defn render-fn [context]
  (sprite/render-image context (asset/get-image :background) 0 0)
  (sprite/render (get-in @game-state [:character]) context))

(defn setup []
  (swap! game-state assoc
         :character (assoc (sprite/sprite (asset/get-animation [:character :idle :right]) 50 80 320 420)
                      :state :idle :orientation :right :update-fn update-character))

  (game core/game-loop update-fn render-fn 60))

(defn ^:export init []
  (.log js/console "Launching a basic game")
  (core/init-canvas "game-canvas" 640 480)
  (input/init)

  ; Load resources
  (asset/load-assets {:images [{:id [:background] :src "images/background.png"}
                               {:id [:dummy] :src "images/dummy_walk.png"}]
                      :spritesheets [{:id :dummy-sheet :src "images/dummy.png" :cols 8 :rows 7}]
                      :animations [{:id [:character :idle :right] :src :dummy-sheet
                                    :cycle [0 1 2 3 4 5 6 7]
                                    :interval 6 :repeat true :running true}
                                   {:id [:character :idle :left] :src :dummy-sheet
                                    :cycle [40 41 42 43 44 45 46 47]
                                    :interval 6 :repeat true :running true}
                                   {:id [:character :walking :right] :src :dummy-sheet
                                    :cycle [8 9 10 11 12 13 14 15]
                                    :interval 6 :repeat true :running true}
                                   {:id [:character :walking :left] :src :dummy-sheet
                                    :cycle [16 17 18 19 20 21 22 23]
                                    :interval 6 :repeat true :running true}
                                   {:id [:character :running :right] :src :dummy-sheet
                                    :cycle [24 25 26 27 28 29 30 31]
                                    :interval 6 :repeat true :running true}
                                   {:id [:character :running :left] :src :dummy-sheet
                                    :cycle [32 33 34 35 36 37 38 39]
                                    :interval 6 :repeat true :running true}
                                   {:id [:character :jumping :right] :src :dummy-sheet
                                    :cycle [48] :interval 6 :repeat true :running false}
                                   {:id [:character :jumping :left] :src :dummy-sheet
                                    :cycle [49] :interval 6 :repeat true :running false}]})

  ; Use setup wrapper to ensure completed loading of assets
  (core/setup setup)
)
