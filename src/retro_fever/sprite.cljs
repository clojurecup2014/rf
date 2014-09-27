(ns retro-fever.sprite)

(def sprite-state (atom {}))


(defn  ^:export init-sprite
  ""
  [src width height x y]
  (let [image (js/Image.)]
    (set! (.-src image) src)
    {:image image
     :width width
     :height height
     :x x
     :y y}))

(defn init-canvas [id width height]
  (let [canvas (.getElementById js/document (name id))]
    (set! (.-width canvas) width)
    (set! (.-height canvas) height)
    {:canvas-2d (.getContext canvas "2d")
     :canvas-width width
     :canvas-height height}))

(defn ^:export render [canvas sprite]
  (let  [{:keys [image width height x y]} sprite
         {:keys [canvas-2d canvas-width canvas-height]} canvas]
    (.log js/console canvas)
    (.log js/console canvas-width)
    (.drawImage canvas-2d image 0 0 canvas-width canvas-height x y width height)))

(defn game-loop []
  (render (:canvas @sprite-state) (:sprite @sprite-state)))

(defn  ^:export init []
  (let [sprite (init-sprite "images/coin-sprite-animation.png" 10 10 0 0)
        canvas (init-canvas "coinAnimation" 100 100)]
    (swap! sprite-state update-in [:sprite] (fn [] sprite))
    (swap! sprite-state update-in [:canvas]  (fn [] canvas))
    (.addEventListener (:image sprite) "load" game-loop)))
