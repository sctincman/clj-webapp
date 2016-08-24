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
