(ns retro-fever.sprite
  (:require [retro-fever.util :as util]))

(defprotocol TypeInfo
  (get-type [this] nil))

(defrecord Image [image]
  TypeInfo
  (get-type [this] :image))

(defrecord Spritesheet [image cols rows]
  TypeInfo
  (get-type [this] :spritesheet))

(defprotocol AnimationActions
  (update-frame [this])
  (get-cell [this])
  (set-running [this run])
  (reset [this]))

(defrecord Animation [spritesheet cycle interval repeat current-idx counter running]
  TypeInfo
  (get-type [this] :animation)
  AnimationActions
  (update-frame [this] (if-not running
                         this
                         (let [c (inc counter)]
                           (if (< c interval)
                             (assoc this :counter c)
                             (merge (assoc this :counter 0)
                                    (let [next-idx (inc current-idx)]
                                      (if (>= next-idx (count cycle))
                                        (if repeat
                                          {:current-idx 0}
                                          {:running false})
                                        {:current-idx next-idx})))))))
  (get-cell [this] (nth cycle current-idx))
  (set-running [this run] (assoc this :running run))
  (reset [this] (assoc this :counter 0 :current-idx 0)))

(defn animation
  "Wrapper function for dynamic creatin from other namespaces"
  [spritesheet cycle interval repeat current-idx counter running]
  (Animation. spritesheet cycle interval repeat current-idx counter running))

(defn ^:export render-image
  "Render given image at specified location"
  ([context {:keys [image width height] :as img} x y]
     (render-image context image x y width height 0 0 width height 0 0))
  ([context image x y width height]
     (render-image context image x y width height 0 0 width height 0 0))
  ([context image x y width height sx sy swidth sheight rel-x rel-y]
     (doto context
       (.save)
       (.translate x y)
       (.drawImage image sx sy swidth sheight rel-x rel-y width height)
       (.restore))))

(defn render-frame
  "Render the given frame from the specified spritesheet"
  ([context {:keys [cols] :as spritesheet} cell x y]
     (render-frame context spritesheet (mod cell cols) (int (/ cell cols)) x y))
  ([context {:keys [image cell-width cell-height] :as spritesheet} col row x y]
     (render-image context image x y cell-width cell-height (* col cell-width)
                   (* row cell-height) cell-width cell-height (* (/ cell-width 2) -1)
                   (* (/ cell-height 2) -1))))

(defn ^:export move [{:keys [x y velocity-x velocity-y] :as sprite}]
  "Moves a sprite based on its velocity on the x and y-axis"
  (assoc sprite
    :x (if velocity-x (+ x velocity-x) x)
    :y (if velocity-x (+ y velocity-y) y)))

(defprotocol SpriteActions
  (render [this context])
  (update [this]))

(defrecord ImageSprite [image width height x y]
  TypeInfo
  (get-type [this] :image-sprite)
  SpriteActions
  (render [this context] (render-image context (:image image) x y width height 0 0 width height
                                       (* (/ width 2) -1) (* (/ height 2) -1)))
  (update [this] (if-let [update-fn (:update-fn this)]
                   (update-fn this)
                   (move this))))

(defrecord SpritesheetSprite [spritesheet width height cell x y]
  TypeInfo
  (get-type [this] :spritesheet-sprite)
  SpriteActions
  (render [this context] (render-frame context spritesheet cell x y))
  (update [this] (if-let [update-fn (:update-fn this)]
                   (update-fn this)
                   (move this))))

(defrecord AnimatedSprite [animation width height x y]
  TypeInfo
  (get-type [this] :animated-sprite)
  SpriteActions
  (render [this context] (render-frame context (:spritesheet animation) (get-cell animation) x y))
  (update [this] (let [updated-sprite (if-let [update-fn (:update-fn this)]
                                        (update-fn this)
                                        (move this))]
                   (assoc updated-sprite :animation (update-frame (:animation updated-sprite))))))

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
