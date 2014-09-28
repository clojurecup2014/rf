(ns retro-fever.util)

(defn current-time-ms []
  "Returns the current time in ms"
  (.getTime (js/Date.)))

(defn get-image-dimensions [image]
  "Returns map with dimensions of given image"
  (hash-map :width (aget image "width")
            :height (aget image "height")))
