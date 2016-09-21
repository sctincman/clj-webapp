(require '[clojure.java.jdbc :as j])

(def default-db-spec
  {:dbtype "h2"
   :dbname "./db/server"})

(defn init-db [db-spec]
  (j/with-db-connection [db-conn db-spec]
    (j/db-do-commands db-conn
                      [(j/create-table-ddl :users
                                           [[:id "bigint primary key auto_increment"]
                                            [:username "char(255) not null"]
                                            [:passwordhash "char not null"]
                                            [:salt "char(255) not null"]
                                            [:email "char not null"]
                                            [:creationdate "timestamp not null"]])
                       (j/create-table-ddl :posts
                                           [[:id "bigint primary key auto_increment"]
                                            [:owner "char(255) not null"]
                                            [:project "char(255) not null"]
                                            [:title "char(255) not null"]
                                            [:content "char not null"]
                                            [:creationdate "timestamp not null"]
                                            [:modificationdate "timestamp"]])
                       (j/create-table-ddl :projects
                                           [[:id "bigint primary key auto_increment"]
                                            [:owner "char(255) not null"]
                                            [:title "char(255) not null"]
                                            [:description "char not null"]
                                            [:creationdate "timestamp not null"]])])))
