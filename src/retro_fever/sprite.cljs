(ns retro-fever.sprite)

(defn  ^:export load-image
  "Load image from source"
  [src]
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
    (.save context)
    (.translate context x y)
    (.drawImage context image (* (/ width 2) -1) (* (/ height 2) -1) width height)
    (.restore context)))

(defn ^:export draw-image
  "Draw given image with top left at specified location"
  [context image x y]
  (.drawImage context image x y (aget image "width") (aget image "height")))

(defn collides?
  [s1 s2]
  (and (< (* (Math/abs (- (:x s1) (:x s2))) 2) (+ (:width s1) (:width s2)))
       (< (* (Math/abs (- (:y s1) (:y s2))) 2) (+ (:height s1) (:height s2)))))

(defn distance-to
  [s1 s2]
  (let [diff-x (- (:x s1) (:x s2))
        diff-y (- (:y s1) (:y s2))]
    (Math/sqrt (+ (* diff-x diff-x)
                  (* diff-y diff-y)))))
