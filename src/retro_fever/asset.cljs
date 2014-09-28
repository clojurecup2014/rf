(ns retro-fever.asset
  (:require [clojure.walk :as walk]
            [retro-fever.sprite :as sprite]
            [retro-fever.util :as util]))

; Map to hold all the loaded game assets
(def asset-store (atom {}))

; Vector of assets with dependencies to other assets
(def dependent-assets (atom []))

(defn- add-id-prefix [prefix id]
  "Helper function to add prefix to a id selector"
  (if (vector? id) (concat [prefix] id) [prefix id]))

(defn ^:export image
  "Load image from source"
  [src]
  (let [image (js/Image.)]
    (set! (.-src image) src)
    image))

(defn- image-loaded [id]
  "Callback function for loaded images"
  (let [image (get-in @asset-store id)]
    (swap! asset-store update-in id merge (util/get-image-dimensions (:image image)) {:loaded true})))

(defn ^:export load-image
  "Load image from resource"
  [{:keys [id src]}]
  (let [id (add-id-prefix :images id)
        image (image src)]
    (swap! asset-store assoc-in id (assoc (sprite/Image. image) :loaded false))
    (.addEventListener image "load" #(image-loaded id))))

(defn- spritesheet-loaded [id]
  "Callback function for loaded spritesheets"
  (let [{:keys [cols rows] :as spritesheet} (get-in @asset-store id)
        {:keys [width height] :as image-size} (util/get-image-dimensions (:image spritesheet))]
    (swap! asset-store update-in id merge image-size
           {:cell-width (/ width cols) :cell-height (/ height rows) :loaded true})))

(defn ^:export load-spritesheet
  "Load spritesheet from source"
  [{:keys [id src cols rows]}]
  (let [id (add-id-prefix :spritesheets id)
        image (image src)]
    (swap! asset-store assoc-in id (assoc (sprite/Spritesheet. image cols rows) :loaded false))
    (.addEventListener image "load" #(spritesheet-loaded id))))

(defn ^:export load-animation
  "Load animation from source or spritesheet entry"
  [{:keys [id src cycle interval repeat running] :as animation}]
  (when (string? src)
    (load-spritesheet animation))
  (swap! dependent-assets conj
         {:type :animation
          :id (add-id-prefix :animations id)
          :dependency-id (add-id-prefix :spritesheets (if (string? src) id src))
          :options [cycle (or interval 20) (or repeat true) 0 0 (or running false)]}))

(defn- create-by-type [type options]
  "Helper function to call the right constructor for dependent assets"
  (apply (condp = type
           :animation sprite/animation)
         options))

(defn load-dependent-assets []
  "Runs through all assets with dependencies and adds them to the asset store"
  (doseq [{:keys [id dependency-id type options]} @dependent-assets]
    (let [image (get-in @asset-store dependency-id)]
      (swap! asset-store assoc-in id
             (assoc (create-by-type type (concat [image] options)) :loaded true)))))

(defn- collapse-assets
  "Helper function to check wheter all resources have been loaded"
  [data & [key sub-key]]
  (if (map? data)
    (keep (fn [[k v]]
            (collapse-assets v k key)) data)
    (when (= key :loaded)
      (hash-map sub-key data))))

(defn resources-loaded? []
  "Checks wheter all asynchronous loaded resources have completed"
  (every? true? (vals (apply merge (flatten (collapse-assets @asset-store))))))

(defn load-assets
  "Load assets based on a specification map"
  [asset-spec]
  (doseq [[k f] {:images load-image
                 :spritesheets load-spritesheet
                 :animations load-animation}]
    (doall (map #(f %) (k asset-spec)))))

(defn- get-asset
  "Internal function to extrac assets from the asset store"
  [id]
  (get-in @asset-store id))

(defn get-image
  "Extracts a given image from the asset store"
  [id]
  (get-asset (add-id-prefix :images id)))

(defn get-spritesheet
  "Extracts a given image from the asset store"
  [id]
  (get-asset (add-id-prefix :spritesheets id)))

(defn get-animation
  "Extracts a given animation from the asset store"
  [id]
  (get-asset (add-id-prefix :animations id)))
