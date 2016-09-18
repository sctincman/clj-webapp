(ns server.db
  (:require [clojure.java.jdbc :as j]))

(def db-spec {:dbtype "h2"
              :dbname "./db/server"})

(defn add-location-to-db
  [x y]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/insert! db-conn
                             :locations
                             {:x x :y y}))]
    (assert (= (count results) 1))
    (first (vals (first results)))))

(defn get-xy
  [loc-id]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select x, y from locations where id = ?" loc-id]))]
    (assert (= (count results) 1))
    (first results)))

(defn get-all-locations
  []
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select id, x, y from locations"]))]
    results))
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

;; AH! SQL injectionable
;;;; Possibly not, these are PreparedStatements from java.sql: they may properly escape any characters!
(defn get-post [username postid]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select * from posts where owner = ? and id = ?" username postid]))]
    (assert (= (count results) 1))
    (first results))))
