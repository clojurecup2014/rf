(ns retro-fever.stats
  (:require [retro-fever.util :as util]
            [goog.string :as gstring]
            [goog.string.format :as gformat]))

(def recordings (atom {:update-time 0 :updates 0 :ups 0
                       :render-time 0 :renders 0 :fps 0
                       :start-time 0 :last-time nil
                       :interval-time 10000}))

(defn set-interval-time
  [interval-time]
  (swap! recordings assoc :interval-time interval-time))

(defn record-start []
  (let [time (util/current-time-ms)]
    (when (= (:start-time @recordings) 0)
      (swap! recordings assoc :start-time time))
    (swap! recordings assoc :last-time time)))


(defn record-update []
  (let [time (util/current-time-ms)
        {:keys [last-time update-time updates]} @recordings]
    (swap! recordings assoc :update-time (+ update-time (- time last-time))
           :updates (inc updates)
           :last-time time)))

(defn record-render []
  (let [time (util/current-time-ms)
        {:keys [last-time render-time renders]} @recordings]
    (swap! recordings assoc :render-time (+ render-time (- time last-time))
           :renders (inc renders)
           :last-time time)))

(defn calculate []
  (let [{:keys [interval-time start-time last-time render-time renders update-time updates]} @recordings
        elapsed-time (- last-time start-time)]
    (if (>= elapsed-time interval-time)
      (swap! recordings assoc
             :fps (gstring/format "%.2f" (double (* (/ renders elapsed-time) 1000)))
             :ups (gstring/format "%.2f" (double (* (/ updates elapsed-time) 1000)))
             :avg-render-time (gstring/format "%.4f" (double (/ render-time renders)))
             :avg-update-time (gstring/format "%.4f" (double (/ update-time updates)))
             :updates 0
             :renders 0
             :update-time 0
             :render-time 0
             :start-time 0))))

(defn get-stats []
  (select-keys @recordings [:ups :fps :avg-render-time :avg-update-time]))
