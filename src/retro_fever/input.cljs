(ns retro-fever.input
  (:require [clojure.string :as string]))

(def kbd-state (atom {}))

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
  (true? (get @kbd-state key-code)))

(defn ^:export init
  []
  (aset js/window "onkeydown" (kbd-state-change kbd-state #(assoc %1 %2 true)))
  (aset js/window "onkeyup" (kbd-state-change kbd-state #(dissoc %1 %2))))
