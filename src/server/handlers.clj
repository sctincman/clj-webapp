(ns server.handlers
  (:require [server.views :as views]
            [compojure.core :as cc]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(cc/defroutes app-routes
  (cc/GET "/"
       []
       (views/home-page))

  (cc/GET "/users" []
          (views/user-list))
  (cc/context "/user/:username" [username]
           (cc/GET "/" []
                   (views/user-page username))
           (cc/GET "/projects" []
                   (views/project-list username))
           (cc/GET "/projects/:pid" [pid]
                   (views/project-page username pid))
           (cc/GET "/projects/:pid/posts" [pid]
                   (views/post-list username pid))
           (cc/GET "/posts/:postid" [postid]
                   (views/post-page username postid)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
