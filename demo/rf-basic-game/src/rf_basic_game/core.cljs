(ns rf-basic-game.core
  (:require-macros [retro-fever.macros :refer [game]])
  (:require [retro-fever.core :as core]
            [retro-fever.input :as input]
            [retro-fever.sprite :as sprite]
            [retro-fever.asset :as asset]))

(def game-state (atom {:environment {:gravity 0.6}}))

(defn cursor-in-image? [state  x y]
  (let [clicked-x (get-in state [:x])
        clicked-y (get-in state [:y])]
  (if (and (>= clicked-x x)
           (<= clicked-x (+ x 50))
           (>= clicked-y y)
           (<= clicked-y (+ y 50)))
    true
    false)))

(defn check-for-movement [character]
  (merge character
         (let [running? (input/key-pressed? :shift)]
           (cond
            (or (and (contains? @input/cursor-state :x) (cursor-in-image? @input/cursor-state 30 270))
                (input/key-pressed? :left)) {:velocity-x (if running? -5 -3) :orientation :left}
            (or (and (contains? @input/cursor-state :x) (cursor-in-image? @input/cursor-state 560 270))
                (input/key-pressed? :right)) {:velocity-x (if running? 5 3) :orientation :right}
                :else {:velocity-x 0}))))

(defn check-for-jumping [{:keys [velocity-x] :as character}]
  (let [jumping? (= (:state character) :jumping)
        running? (>= (Math/abs velocity-x) 4)]
    (merge character
           (when (and (or (and (contains? @input/cursor-state :x) (cursor-in-image? @input/cursor-state 300 100))
                          (input/key-pressed? :space) (input/key-pressed? :up)) (not jumping?))
             {:state :jumping :velocity-y (if running? -14 -10)})
           (when jumping? {:state :jumping}))))

(defn consider-forces [{:keys [state velocity-y] :as character}]
  (merge character
         (when (= state :jumping)
           {:velocity-y (+ velocity-y (get-in @game-state [:environment :gravity]))})))

(defn check-bounds [{:keys [x] :as character}]
  (assoc character :x (cond
                       (< x 25) 25
                       (> x 615) 615
                       :else x)))

(defn check-for-base [{:keys [state y] :as character}]
  (merge character
         (when (and (= state :jumping) (>= y 420))
           {:state :idle :velocity-y 0 :y 420})))

(defn check-for-collisions [{:keys [x y velocity-y] :as character}]
  (let [collisions (filter #(and (< (- (:x %) 30) x (+ (:x %) 30))
                                 (> (- (:y %) 20) (+ y 60) (- (:y %) 36)))
                           (get-in @game-state [:platforms]))]
    (if (= (count collisions) 0)
      (if (< y 420)
        (assoc character :state :jumping)
        character)
      (if (> velocity-y 0)
        (merge character {:state :idle :velocity-y 0 :y (- (:y (first collisions)) 84)})
        character))))

(defn determine-state [{:keys [state velocity-x] :as character}]
  (assoc character :state (cond
                           (= state :jumping) :jumping
                           (= velocity-x 0) :idle
                           (< 0 (Math/abs velocity-x) 4) :walking
                           (<= 4 (Math/abs velocity-x)) :running)))

(defn set-animation [{:keys [state orientation] :as character} old-character]
  (if (and (= (:state old-character) state) (= (:orientation old-character) orientation))
    character
    (assoc character :animation (asset/get-animation [:character state orientation]))))

(defn update-character [character]
  (-> character
      check-for-movement
      check-for-jumping
      sprite/move
      check-bounds
      check-for-collisions
      check-for-base
      consider-forces
      determine-state
      (set-animation character)))

(defn update-fn []
  (swap! game-state update-in [:character] sprite/update))

(defn render-fn [context]
  (sprite/render-image context (asset/get-image :background) 0 0)
  (sprite/render-image context (asset/get-image :left) 30 220)
  (sprite/render-image context (asset/get-image :right) 560 220)
  (sprite/render-image context (asset/get-image :top) 300 30)
  (doall (map #(sprite/render % context) (get-in @game-state [:platforms])))
  (sprite/render (get-in @game-state [:character]) context))

(defn setup []
  (swap! game-state assoc
         :character (assoc (sprite/sprite (asset/get-animation [:character :idle :right]) 50 80 320 420)
                      :state :idle :orientation :right :update-fn update-character))

  (doall (map (fn [[img x y]]
                (swap! game-state update-in [:platforms] conj (sprite/sprite (asset/get-image [:platform img])
                                                                             48 48 x y)))
              [[:left 100 420] [:middle 148 420] [:right 196 420]
               [:left 220 340] [:middle 268 340] [:right 316 340]
               [:left 480 370] [:middle 528 370] [:right 572 370]
               [:left 440 170] [:middle 488 170] [:right 532 170]]))

  (game core/game-loop update-fn render-fn 60))

(defn ^:export init []
  (.log js/console "Launching a basic game")
  (core/init-canvas "game-canvas" 640 480)
  (input/init)
  (input/init-cursor-in-container "game-canvas")
  ; Load resources
  (asset/load-assets {:images [{:id [:background] :src "images/background.png"}
                               {:id [:platform :left] :src "images/grassl.png"}
                               {:id [:platform :middle] :src "images/ground0.png"}
                               {:id [:platform :right] :src "images/grassr.png"}
                               {:id [:dummy] :src "images/dummy_walk.png"}
                               {:id [:left] :src "images/left.png"}
                               {:id [:right] :src "images/right.png"}
                               {:id [:top] :src "images/top.png"}]
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
