(ns retro-fever.sprite)

(defn  ^:export init-image
""
[src width]
(let [image (js/Image.)]
  (set! (.-onload image) (fn []
                           (set! (.-width image) width)))
  (set! (.-src image) src)
  image))

(defn init-canvas [id width height]
  (let [canvas (.getElementById js/document (name id))]
    (set! (.-width canvas) width)
    (set! (.-height canvas) height)
    (.getContext canvas "2d")))

 (defn ^:export render [canvas image]
   (set! (.-onload image) (fn []
                          (do (.log js/console canvas)
                              (set! (.-fillStyle canvas) "black")
                              (.drawImage canvas image 0 0 100 100 0 0 100 100)))))


(defn  ^:export init []
  (render (init-canvas "coinAnimation" 100 100) (init-image "images/coin-sprite-animation.png")))
