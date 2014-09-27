(ns retro-fever.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [retro-fever.macros :refer [game]])
  (:require [retro-fever.input :as input]
            [enfocus.core :as ef]
            [enfocus.bind :as bind]
            [cljs.core.async :refer [<! put! alts! chan timeout]]))

(defn render-kbd
  [node kbd-state]
  (ef/at node (ef/content (prn-str kbd-state))))

(def app (atom {:game {:loop nil}}))

(defn current-time-ms []
  (. (js/Date.) (getTime)))

(defn update-loop [tick-interval update-fn]
  (fn [next-tick]
    (loop [tick next-tick skips 0]
      (if (and (< tick (current-time-ms)) (< skips 5))
        (do (update-fn)
            (recur (+ tick tick-interval) (inc skips)))
        tick))))

(defn game-loop
  [tick-interval update-fn draw-fn]
  (let [quit-chan (chan)
        update (update-loop tick-interval update-fn)]
    (go (loop [next-tick (current-time-ms)]
      (let [[v ch] (alts! [quit-chan (timeout (- next-tick (current-time-ms)))])]
        (when-not (= ch quit-chan)
          (let [tick (update next-tick)]
            (draw-fn)
            (recur tick))))))
    (swap! app assoc-in [:game :loop] quit-chan)))

(defn stop-loop []
  (put! (get-in @app [:game :loop]) false))

(defn ^:export init
  []
  (.log js/console "We have a take off!")
  (ef/at "#input" (bind/bind-view input/kbd-state render-kbd))

  (input/init)
)
