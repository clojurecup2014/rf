(ns retro-fever.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [retro-fever.macros :refer [game]])
  (:require [retro-fever.input :as input]
            [enfocus.core :as ef]
            [enfocus.bind :as bind]
            [cljs.core.async :refer [<! put! alts! chan timeout]]
            [retro-fever.util :as util]
            [retro-fever.stats :as stats]))

(defn render-kbd
  [node kbd-state]
  (ef/at node (ef/content (prn-str kbd-state))))

(def app (atom {:game {:loop nil}}))

(defn update-loop [tick-interval update-fn]
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
              (let [tick (update next-tick)]
                (render-fn (get-in @app [:game :canvas :context]))
                (stats/record-render)
                (stats/calculate)
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
