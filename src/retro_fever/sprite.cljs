(ns retro-fever.sprite)

(defn  ^:export init-image
""
[src]
(let [img (js/Image.)]
  (set! (.-onload img) (fn [] (.log js/console "Image loaded")))
  (set! (.-src img) src)
  img))

(defn init-canvas [id width height]
  (let [canvas (.getElementById js/document (name id))]
    (set! (.-width canvas) width)
    (set! (.-height canvas) height)
    (.getContext canvas "2d")))

 (defn ^:export render [canvas image]
   (do (.log js/console canvas)
       (.drawImage canvas image 0 0 100 100 0 0 100 100)))


(defn  ^:export init []
  (render (init-canvas "coinAnimation" 100 100) (init-image "images/coin-sprite-animation.png")))
