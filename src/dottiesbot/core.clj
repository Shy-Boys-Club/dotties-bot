(ns dottiesbot.core
  (:gen-class
   :methods [^:static [handler [String] String]])
  (:require [dottiesbot.dotties.filewriter :refer [add-dotties-json]]
            [dottiesbot.util :refer [from-json to-json]]
            [dottiesbot.github.actions :refer [fork commit-and-push pull-request clone clean-up get-target-dir set-gh-user]]))

(def test-json (slurp "dotties-add-json.json"))

(defn handle-request
  [request]
  (let [request-json    (from-json request)
        repo            (get request-json "repository")
        dotties         (get request-json "dotties")
        default-branch  (get request-json "defaultBranch")
        target-dir      (get-target-dir repo)]

    (clone repo target-dir)
    (set-gh-user)
    (fork repo target-dir)
    (add-dotties-json repo dotties)
    (commit-and-push target-dir)
    (pull-request repo default-branch)
    (clean-up target-dir)))

(defn -handler [s]
  (handle-request s)
  (println "Done!")
  (to-json {:success true}))

(defn -main [s]
  (println "Called with data" s)
  (-handler s)
  (System/exit 0))
