(ns dottiesbot.util
  (:require [cheshire.core :as json]))

(defn from-json
  [json-string]
  (json/parse-string json-string))

(defn to-json
  "Write JSON object to file, pretty printed"
  [json-obj]
  (json/generate-string json-obj {:pretty true}))

(defn to-json-req
  "Write JSON object for requests. No prettifying"
  [json-obj]
  (json/generate-string json-obj))
