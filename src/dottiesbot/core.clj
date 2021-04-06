(ns dottiesbot.core
  (:gen-class)
  (:require [dottiesbot.dotties.filewriter :refer [add-dotties-json]]
            [dottiesbot.util :refer [from-json]]
            [dottiesbot.github.actions :refer [fork commit-and-push pull-request clone clean-up]]))

(def test-json (slurp "dotties-add-json.json"))

(defn handle-request
  [request]
  (let [request-json (from-json request)
        repo (get request-json "repository")
        dotties (get request-json "dotties")
        default-branch (get request-json "defaultBranch")]
    (clone repo)
    (add-dotties-json repo dotties)
    (fork repo)
    (commit-and-push repo)
    (pull-request repo default-branch)
    (clean-up repo)))

(defn -main
  [& args]
  (handle-request test-json)
  (println "Done!")
  (shutdown-agents))

;(-main)
