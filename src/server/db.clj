(ns server.db
  (:require [clojure.java.jdbc :as j]))

(def db-spec
  "Default database sepcification: an local H2 db."
  {:dbtype "h2"
   :dbname "./db/server"})

(defn get-all-posts
  "Returns all posts with all fields from the database."
  []
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select * from posts"]))]
    results))

(defn get-all-users
  "Returns all users with all fields from the database."
  []
  (j/with-db-connection [db-conn db-spec]
    (j/query db-conn
             ["select * from users"])))

(defn get-user-by-name
  "Returns user data of passed username. Throws assertion if not found."
  [username]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select * from users where username = ?" username]))]
    (assert (= (count results) 1))
    (first results)))

(defn get-all-projects
  "Returns all projects owned by the passed username."
  [owner]
  (j/with-db-connection [db-conn db-spec]
    (j/query db-conn
             ["select * from projects where owner = ?" owner])))

(defn get-project
  "Returns a project owned by a user (specificed by username), identified by the projectid. Throws assertion if not found."
  [owner pid]
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

(defn get-posts
  "Returns all posts owned by the passed username, under a specific project (specified by projectid)."
  [owner projectid]
  (j/with-db-connection [db-conn db-spec]
    (let [results (j/query db-conn
                           ["select * from projects where owner = ? and id = ?" owner projectid])]
      (assert (= (count results) 1))
      (j/query db-conn
               ["select * from posts where owner = ? and project = ?" owner (:title (first results))]))))

(defn get-post
  "Returns a post owned by a user (specificed by username), identified by the postid. Throws assertion if not found."
  [username postid]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/query db-conn
                           ["select * from posts where owner = ? and id = ?" username postid]))]
    (assert (= (count results) 1))
    (first results)))

(defn create-project
  "Inserts a new project, and returns the id of the resulting entry. Requires the owning user, title, and description. Sets creation date from timestamp when insertion is called. Throws assertion if unable to insert."
  [owner title description]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/insert! db-conn
                             :projects
                             {:owner owner :title title :description description :creationdate (java.sql.Timestamp/from (java.time.Instant/now))}))]
    (assert (= (count results) 1))
    (first (vals (first results)))))

(defn create-post
  "Inserts a new post, and returns the id of the resulting entry. Requires the owning user, parent project title, title of post, and content. Sets creation date from timestamp when insertion is called. Throws assertion if unable to insert."
  [owner project title content]
  (let [results (j/with-db-connection [db-conn db-spec]
                  (j/insert! db-conn
                             :posts
                             {:owner owner :title title :project project :content content :creationdate (java.sql.Timestamp/from (java.time.Instant/now))}))]
    (assert (= (count results) 1))
    (first (vals (first results)))))
