(ns dottiesbot.github.actions
  (:require
   [clojure.java.shell :as shell]
   [clojure.string :as str]
   [clj-http.client :as client]
   [dottiesbot.util :refer [to-json-req]]))

(def clone-url-base "https://github.com/REPO.git")
(def clone-url-base-with-token "https://dotties-bot:TOKEN@github.com/REPO.git")

(def bot-username "dotties-bot")
(def branch-name "dotties-bot-generated")
(def access-token (System/getenv "GITHUB_ACCESS_TOKEN"))

(def pull-request-api "https://api.github.com/repos/REPO/pulls")
(def fork-request-api "https://api.github.com/repos/REPO/forks")

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
  (-> (str/replace clone-url-base-with-token #"REPO" (replace-repo-owner repo-name))
      (str/replace #"TOKEN" access-token)))

(defn get-target-dir
  "Get target directory for clone, git operations and delete. Format: 'repos/reponame'"
  [repo-name]
  (str "repos/" (get (str/split repo-name #"/") 1) "/"))

(defn clone [repo-name]
  (println (:out (shell/sh "git" "clone" (get-repo-url repo-name) (get-target-dir repo-name)))))

(clone "Matsuuu/dotfiles")

(defn fork-request
  [repo-name]
  (client/post (get-fork-api-url repo-name) {:accept "application/vnd.github.v3+json"
                                             :oauth-token access-token}))

(defn fork
  "Create a fork of the current repo"
  [repo-name]
  (fork-request repo-name)
  (println (:out (shell/sh "git" "remote" "set-url" "origin" (get-fork-repo-url repo-name) :dir (get-target-dir repo-name))))
  (println (:out (shell/sh "git" "checkout" "-b" branch-name :dir (get-target-dir repo-name)))))

(defn commit-and-push
  [repo-name]
  (println (:out (shell/sh "git" "add" "dotties.json" :dir (get-target-dir repo-name))))
  (println (:out (shell/sh "git" "commit" "-m" "dotties.json generated" :dir (get-target-dir repo-name))))
  (println (:out (shell/sh "git" "push" "-u" "origin" branch-name :dir (get-target-dir repo-name)))))

(defn generate-pull-request-body
  [default-branch]
  (to-json-req {:title "Update dotties.json"
                :head (str bot-username ":" branch-name)
                :base default-branch
                :body "This is a automatically generated PR from https://dotties.io\n\n Please review the changes and merge this update to enable activate your repository in dotties.io.\n\n - The Shy Boys Club"}))

(defn pull-request
  [repo-name default-branch]
  (client/post (get-pr-url repo-name) {:accept "application/vnd.github.v3+json"
                                       :oauth-token access-token
                                       :body (generate-pull-request-body default-branch)}))

(defn clean-up
  "java.io only deletes files"
  [repo-name]
  (println (:out (shell/sh "rm" "-rf" (get-target-dir repo-name) :dir (get-target-dir repo-name)))))

