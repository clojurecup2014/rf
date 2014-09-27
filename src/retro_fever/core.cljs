(ns retro-fever.core
  (:require [retro-fever.input :as input]))

(defn ^:export init
  []
  (.log js/console "We have a take off!")
  (input/init)
)
