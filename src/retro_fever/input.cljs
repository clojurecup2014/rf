(ns retro-fever.input)

(def kbd-state (atom {}))

(defn on-key-down
  "Update keyboard state map with key down event"
  [e]
  (let [key-code (keyword (str (aget e "keyCode")))]
    (swap! kbd-state assoc key-code true)))

(defn on-key-up
  "Update keyboard state map with key up event"
  [e]
  (let [key-code (keyword (str (aget e "keyCode")))]
    (swap! kbd-state dissoc key-code)))

(defn ^:export init
  []
  (aset js/window "onkeydown" on-key-down)
  (aset js/window "onkeyup" on-key-up))
