(ns dottiesbot.github.actions
  (:require
   [clojure.java.shell :as shell]
   [clojure.string :as str]
   [org.httpkit.client :as client]
   [dottiesbot.util :refer [to-json-req]]))

(def clone-url-base "https://github.com/REPO.git")
(def clone-url-base-with-token "https://dotties-bot:TOKEN@github.com/REPO.git")

(def bot-username "dotties-bot")
(def branch-name "dotties-bot-generated")

(defn get-access-token []
  (or (System/getenv "GITHUB_ACCESS_TOKEN") ""))

(def pull-request-api "https://api.github.com/repos/REPO/pulls")
(def fork-request-api "https://api.github.com/repos/REPO/forks")

(defn exec
  [& args]
  (println (str "Running sh command" args))
  (println (apply shell/sh args)))

(defn set-gh-user
  []
  (exec "git" "config" "--global" "user.email" "matias@shyboys.club")
  (exec "git" "config" "--global" "user.name" "dotties-bot"))

(defn get-pr-url
  [repo-name]
  (str/replace pull-request-api #"REPO" repo-name))

(defn get-fork-api-url
  [repo-name]
  (str/replace fork-request-api #"REPO" repo-name))

(defn get-repo-url
  "Get repository URL, build with clone-url-base -variable"
  [repo-name]
  (str/replace clone-url-base #"REPO" repo-name))

(defn replace-repo-owner
  [repo-name]
  (str bot-username "/" (get (str/split repo-name #"/") 1)))

(defn get-fork-repo-url
  [repo-name]

  (if (<= (count (get-access-token)) 0)
    (throw (Exception. "Access token not set")))

  (-> (str/replace clone-url-base-with-token #"REPO" (replace-repo-owner repo-name))
      (str/replace #"TOKEN" (get-access-token))))

(defn get-target-dir
  "Get target directory for clone, git operations and delete. Format: '/tmp/repos/reponame'
  Has to be under tmp to work with Lambda"
  [repo-name]
  (str "/tmp/repos/" (get (str/split repo-name #"/") 1) "/"))

(defn clone [repo-name target-dir]
  (exec "git" "clone" (get-repo-url repo-name) target-dir "--depth" "1"))

(defn fork-request
  [repo-name]
  (client/post (get-fork-api-url repo-name) {:accept "application/vnd.github.v3+json"
                                             :oauth-token (get-access-token)}))

(defn fork
  "Create a fork of the current repo"
  [repo-name target-dir]
  (let [fork-repo-url (get-fork-repo-url repo-name)]
    (fork-request repo-name)
    (exec "git" "remote" "set-url" "origin" fork-repo-url :dir target-dir)
    (exec "git" "checkout" "-b" branch-name :dir target-dir)))

(defn commit-and-push
  [target-dir]
  (exec "git" "add" "dotties.json" :dir target-dir)
  (exec "git" "commit" "-m" "dotties.json generated" :dir target-dir)
  (exec "git" "push" "-u" "origin" "-f" branch-name :dir target-dir))

(defn generate-pull-request-body
  [default-branch]
  (to-json-req {:title "Update dotties.json"
                :head (str bot-username ":" branch-name)
                :base default-branch
                :body "This is a automatically generated PR from https://dotties.io\n\n Please review the changes and merge this update to activate your repository in dotties.io.\n\n - The Shy Boys Club"}))

(defn pull-request
  [repo-name default-branch]
  (client/post (get-pr-url repo-name) {:accept "application/vnd.github.v3+json"
                                       :oauth-token (get-access-token)
                                       :body (generate-pull-request-body default-branch)}))

(defn clean-up
  "java.io only deletes files"
  [target-dir]
  (exec "rm" "-rf" target-dir))

