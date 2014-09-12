(ns puppetlabs.liberator-util.representation
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [liberator.representation :as lib-rep]))

(def ^:dynamic *map-encoders* {})
(def ^:dynamic *seq-encoders* {})

(defn install-map-representation-dispatcher!
  [media-type default-encoder]
  (defmethod lib-rep/render-map-generic media-type
    [data _]
    (if-let [encoder (get *map-encoders* media-type)]
      (encoder data)
      (default-encoder data))))

(defn install-seq-representation-dispatcher!
  [media-type default-encoder]
  (defmethod lib-rep/render-seq-generic media-type
    [data _]
    (if-let [encoder (get *seq-encoders* media-type)]
      (encoder data)
      (default-encoder data))))

(defn map-encoder-wrapper
  [media-type encoder]
  (fn [handler]
    (fn [request]
      (binding [*map-encoders* (assoc *map-encoders* media-type encoder)]
        (handler request)))))

(defn seq-encoder-wrapper
  [media-type encoder]
  (fn [handler]
    (fn [request]
      (binding [*seq-encoders* (assoc *seq-encoders* media-type encoder)]
        (handler request)))))
