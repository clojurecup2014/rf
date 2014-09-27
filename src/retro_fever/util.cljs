(ns retro-fever.util)

(defn current-time-ms []
  (.getTime (js/Date.)))
