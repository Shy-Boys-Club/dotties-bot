(ns dottiesbot.util
  (:require [clojure.data.json :as json]))

(defn from-json
  [json-string]
  (json/read-str json-string))

(defn to-json
  "Write JSON object to file, pretty printed"
  [json-obj]
  (with-out-str (json/pprint json-obj :escape-slash false)))

(defn to-json-req
  "Write JSON object for requests. No prettifying"
  [json-obj]
  (json/write-str json-obj))
