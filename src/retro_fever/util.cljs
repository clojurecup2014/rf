(ns retro-fever.util)

(defn current-time-ms []
  (. (js/Date.) (getTime)))
