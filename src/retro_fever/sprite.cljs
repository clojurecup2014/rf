(ns retro-fever.sprite)

(defn  ^:export load-image
  "Load image from source"
  [src]
  (let [image (js/Image.)]
    (set! (.-src image) src)
    image))

(defn ^:export sprite
  "Creates a sprite record based on graphics resource"
  ([image x y]
     (sprite image (:width image) (:height image) x y))
  ([image width height x y & [cell]]
     (condp = (get-type image)
       :spritesheet (SpritesheetSprite. image width height cell x y)
       :animation (AnimatedSprite. image width height x y)
       (ImageSprite. image width height x y))))

(defn collides?
  "Checks wheter 2 sprites have collided using bounding boxes"
  [s1 s2]
  (and (< (* (Math/abs (- (:x s1) (:x s2))) 2) (+ (:width s1) (:width s2)))
       (< (* (Math/abs (- (:y s1) (:y s2))) 2) (+ (:height s1) (:height s2)))))

(defn distance-to
  "Calculates the distance between the centers of two sprites"
  [s1 s2]
  (let [diff-x (- (:x s1) (:x s2))
        diff-y (- (:y s1) (:y s2))]
    (Math/sqrt (+ (* diff-x diff-x)
                  (* diff-y diff-y)))))
