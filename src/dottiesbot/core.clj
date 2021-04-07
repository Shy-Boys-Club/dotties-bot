(ns dottiesbot.core
  (:gen-class)
  (:require [dottiesbot.dotties.filewriter :refer [add-dotties-json]]
            [dottiesbot.util :refer [from-json]]
            [dottiesbot.github.actions :refer [fork commit-and-push pull-request clone clean-up get-target-dir]]))

(def test-json (slurp "dotties-add-json.json"))

(defn handle-request
  [request]
  (let [request-json    (from-json request)
        repo            (get request-json "repository")
        dotties         (get request-json "dotties")
        default-branch  (get request-json "defaultBranch")
        target-dir      (get-target-dir repo)]

    (clone repo target-dir)
    (add-dotties-json repo dotties)
    (fork repo target-dir)
    (commit-and-push target-dir)
    (pull-request repo default-branch)
    (clean-up target-dir)))

(defn -main
  [& args]
  (println "Starting...")
  (handle-request test-json)
  (println "Done!")
  (shutdown-agents))


;(-main)
