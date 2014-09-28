(ns retro-fever.stats
  (:require [retro-fever.util :as util]
            [goog.string :as gstring]
            [goog.string.format :as gformat]))

;; Atom holding the current recordings and calculated values
;; from the last recording iteration
(def recordings (atom {:update-time 0 :updates 0 :ups 0
                       :render-time 0 :renders 0 :fps 0
                       :start-time 0 :last-time nil
                       :interval-time 5000}))

(defn set-interval-time
  "Sets the time between each calculation of ups and fps"
  [interval-time]
  (swap! recordings assoc :interval-time interval-time))

(defn record-start []
  "Set the start time for interval"
  (let [time (util/current-time-ms)]
    (when (= (:start-time @recordings) 0)
      (swap! recordings assoc :start-time time))
    (swap! recordings assoc :last-time time)))


(defn record-update []
  "Record a update event"
  (let [time (util/current-time-ms)
        {:keys [last-time update-time updates]} @recordings]
    (swap! recordings assoc :update-time (+ update-time (- time last-time))
           :updates (inc updates)
           :last-time time)))

(defn record-render []
  "Record a render event"
  (let [time (util/current-time-ms)
        {:keys [last-time render-time renders]} @recordings]
    (swap! recordings assoc :render-time (+ render-time (- time last-time))
           :renders (inc renders)
           :last-time time)))

(defn calculate []
  "Calculate statistics based on the recordings from last interval"
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
  "Returns map with statistics from last interval"
  (select-keys @recordings [:ups :fps :avg-render-time :avg-update-time]))

(defn render-stats [context color x y]
  (let [{:keys [ups fps avg-render-time avg-update-time]} (get-stats)]
    (doto context
      (aset "font" "14px Arial")
      (aset "textAlign" "left")
      (aset "fillStyle" color)
      (.fillText (str "UPS: " ups) x y)
      (.fillText (str "FPS: " fps) x (+ y 16))
      (.fillText (str "Avg. update time: " avg-update-time) x (+ y 32))
      (.fillText (str "Avg. render time: " avg-render-time) x (+ y 48)))))
