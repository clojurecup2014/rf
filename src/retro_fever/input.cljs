(ns retro-fever.input
  (:require [clojure.string :as string]))

; Map holding the current state of the kayboard keys
(def kbd-state (atom #{}))
(def cursor-state (atom {}))

(def special-key-codes
  {8  "Backspace"
   9  "Tab"
   16 "Shift"
   17 "Control"
   18 "Alt"
   13 "Enter"
   32 " "
   33 "PageUp"
   34 "PageDown"
   37 "Left"
   38 "Up"
   39 "Right"
   40 "Down"
   })

(defn number-or-character?
  "Takes a keyboard event key code and returns true if it is a char or number"
  [key-code]
  (or (< 47 key-code 57)
      (< 64 key-code 90)
      (< 96 key-code 122)))

(defn key-fallback
  [key-code]
  (cond
   (number-or-character? key-code) (str (char key-code))
   (contains? special-key-codes key-code) (get special-key-codes key-code)
   :else (str key-code)))

;; This is a wrapper because not all browsers have implemented KeyboardEvent.key
;; https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent.key
(defn get-key
  [e]
  (let [k (aget e "key")]
    (if (undefined? k)
      (key-fallback (aget e "keyCode"))
      k)))

;; KeyboardEvent.key uses a litteral space instead of the string "Space"
;; at least in Firefox :)
(defn space-as-space
  [k]
  (if (= k " ") "Space" k))

(defn kbd-state-change
  "Returns a function which takes e keyboard event and swaps the atom
state using the function f"
  [state f]
  (fn [e]
    (let [k (-> e
                get-key
                space-as-space
                string/lower-case
                keyword)]
      (apply swap! [state f k]))))

(defn key-pressed?
  [key-code]
  (contains? @kbd-state key-code))

(defn ^:export init
  "Initializes keyboard as input device"
  []
  (aset js/window "onkeydown" (kbd-state-change kbd-state conj))
  (aset js/window "onkeyup" (kbd-state-change kbd-state disj)))

(defn cursor-state-change
  "Registers a cursor change"
  [state coord-ev]
  (swap! state assoc :x (.-clientX coord-ev) :y (.-clientY coord-ev)))

(defn touch-state-change
  "Registers changes from touch devices"
  [state]
  (fn [e]
    (let [touches (.-changedTouches e)
          touch (.item touches 0)]
      (.preventDefault e)
      (cursor-state-change state touch))))

(defn mouse-state-change
  "Registers changes from the mouse"
  [state]
  (fn [e]
    (.preventDefault e)
    (cursor-state-change state e)))

(defn cursor-state-reset
  "Resets the cursor state"
  [state]
  (fn [e]
    (.preventDefault e)
    (swap! state dissoc :x :y)))

(defn ^:export init-cursor-in-container
  "Initialize mouse and touch deviced for container with given id"
  [id]
  (let [container (.getElementById js/document id)]
    (.addEventListener container "mousedown" (mouse-state-change cursor-state))
    (.addEventListener container "mouseup" (cursor-state-reset cursor-state))
    (.addEventListener container "touchstart" (touch-state-change cursor-state))
    (.addEventListener container "touchmove" (touch-state-change cursor-state))
    (.addEventListener container "touchend" (cursor-state-reset cursor-state))))
