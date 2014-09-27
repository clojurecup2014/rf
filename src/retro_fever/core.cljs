(ns retro-fever.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [retro-fever.macros :refer [game]])
  (:require [cljs.core.async :refer [<! put! alts! chan timeout]]
            [retro-fever.util :as util]
            [retro-fever.stats :as stats]))

(def app (atom {:game {:canvas nil :loop nil}}))

(defn init-canvas [id width height]
  (let [canvas (.getElementById js/document (name id))]
    (set! (.-width canvas) width)
    (set! (.-height canvas) height)
    (swap! app assoc-in [:game :canvas] {:context (.getContext canvas "2d")
                                         :width width
                                         :height height})))

(defn- update-loop [tick-interval update-fn]
  (fn [next-tick]
    (loop [tick next-tick skips 0]
      (if (and (< tick (util/current-time-ms)) (< skips 5))
        (do (update-fn)
            (stats/record-update)
            (recur (+ tick tick-interval) (inc skips)))
        tick))))

(defn game-loop
  [tick-interval update-fn render-fn]
  (let [quit-chan (chan)
        update (update-loop tick-interval update-fn)]
    (go (loop [next-tick (util/current-time-ms)]
          (let [[v ch] (alts! [quit-chan (timeout (- next-tick (util/current-time-ms)))])]
            (when-not (= ch quit-chan)
              (stats/record-start)
              (let [tick (update next-tick)
                    {:keys [context width height]} (get-in @app [:game :canvas])]
                (.clearRect context 0 0 width height)
                (render-fn context)
                (stats/record-render)
                (stats/calculate)
                (recur tick))))))
    (swap! app assoc-in [:game :loop] quit-chan)))

(defn stop-loop []
  (put! (get-in @app [:game :loop]) false))
