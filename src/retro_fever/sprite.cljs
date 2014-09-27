(ns retro-fever.sprite)

(def sprite-state (atom {:image ""
                         :canvas ""}))


(defn  ^:export init-image
  ""
  [src]
  (let [image (js/Image.)]
    (set! (.-src image) src)
    image))

(defn init-canvas [id width height]
  (let [canvas (.getElementById js/document (name id))]
    (set! (.-width canvas) width)
    (set! (.-height canvas) height)
    (.getContext canvas "2d")))

(defn ^:export render [canvas image]
  (let [width (.-width image)]
    (.log js/console width)
    (.log js/console canvas)
    (set! (.-fillStyle canvas) "black")
    (.drawImage canvas image 0 0 100 100 0 0 10 10)))

(defn game-loop []
  (render (:canvas @sprite-state) (:image @sprite-state)))

(defn  ^:export init []
  (let [image (init-image "images/coin-sprite-animation.png" 10)
        canvas (init-canvas "coinAnimation" 100 100)]
    (swap! sprite-state update-in [:image] (fn [] image))
    (swap! sprite-state update-in [:canvas]  (fn [] canvas))
    (.addEventListener image "load" game-loop)))
