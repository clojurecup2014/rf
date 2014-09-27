(ns retro-fever.sprite)

(def sprite-state (atom {}))


(defn  ^:export init-sprite
  ""
  [src width height x y]
  (let [image (js/Image.)]
    (set! (.-src image) src)
    image))

(defn ^:export sprite
  [image width height x y]
  {:image image
   :width width
   :height height
   :x x
   :y y})

(defn ^:export render [context sprite]
  (let [{:keys [image width height x y]} sprite]
    (.drawImage context image x y width height)))
