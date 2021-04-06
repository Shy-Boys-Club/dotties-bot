(ns dottiesbot.dotties.filewriter
  (:require
   [dottiesbot.github.actions :refer [get-target-dir]]
   [dottiesbot.util :refer [to-json]]))

(defn dotties-file [repo-name]
  (str (get-target-dir repo-name) "dotties.json"))

(defn add-dotties-json
  [repo-name new-dotties-file]
  (spit (dotties-file repo-name) (to-json new-dotties-file)))

