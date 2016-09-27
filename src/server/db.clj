(ns server.db
  (:require [clojure.java.jdbc :as j]))

(def db-spec {:dbtype "h2"
              :dbname "./db/server"})

(defn get-all-posts []
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select * from posts"]))]
    results))

(defn get-all-users []
  (j/with-db-connection [db-conn db-spec]
    (j/query db-conn
             ["select * from users"])))

(defn get-user-by-name [username]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select * from users where username = ?" username]))]
    (assert (= (count results) 1))
    (first results)))

(defn get-all-projects [owner]
  (j/with-db-connection [db-conn db-spec]
    (j/query db-conn
             ["select * from projects where owner = ?" owner])))

(defn get-project [owner pid]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select * from projects where owner = ? and id = ?" owner pid]))]
    (assert (= (count results) 1))
    (first results)))

(defn add-project [owner title description]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/insert! db-conn
                             :projects
                             {:owner owner :title title :description description :creationdate "2016-09-18 22:53:00"}))]
    (assert (= (count results) 1))
    (first (vals (first results)))))

(defn get-posts [owner projectid]
  (j/with-db-connection [db-conn db-spec]
    (let [results (j/query db-conn
                           ["select * from projects where owner = ? and id = ?" owner projectid])]
      (assert (= (count results) 1))
      (j/query db-conn
               ["select * from posts where owner = ? and project = ?" owner (:title (first results))]))))

;; AH! SQL injectionable
;;;; Possibly not, these are PreparedStatements from java.sql: they may properly escape any characters!
(defn get-post [username postid]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select * from posts where owner = ? and id = ?" username postid]))]
    (assert (= (count results) 1))
    (first results)))

(defn create-project [owner title description]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/insert! db-conn
                             :projects
                             {:owner owner :title title :description description :creationdate (java.sql.Timestamp/from (java.time.Instant/now))}))]
    (assert (= (count results) 1))
    (first (vals (first results)))))

(defn create-post [owner project title content]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/insert! db-conn
                             :posts
                             {:owner owner :title title :project project :content content :creationdate (java.sql.Timestamp/from (java.time.Instant/now))}))]
    (assert (= (count results) 1))
    (first (vals (first results)))))
