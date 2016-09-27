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

  (cc/GET "/new-project" []
          (views/new-project-page "root"))
  (cc/POST "/new-project" {{title :title description :description} :params}
           (views/new-project-results-page "root" title description))
  (cc/GET "/new-post" []
          (views/new-post-page "root"))
  (cc/POST "/new-post" {{project :project title :title content :content} :params}
           (views/new-post-results-page "root" project title content))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
