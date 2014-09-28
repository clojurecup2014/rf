(ns retro-fever.util)

(defn current-time-ms []
  (.getTime (js/Date.)))

(defn get-image-dimensions [image]
  (hash-map :width (aget image "width")
            :height (aget image "height")))
