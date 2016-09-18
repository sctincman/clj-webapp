(ns server.handlers
  (:require [server.views :as views]
            [compojure.core :as cc]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(cc/defroutes app-routes
  (cc/GET "/"
       []
       (views/home-page))
  (cc/GET "/add-location"
       []
       (views/add-location-page))
  (cc/POST "/add-location"
        {params :params}
        (views/add-location-results-page params))
  (cc/GET "/location/:loc-id"
       [loc-id]
       (views/location-page loc-id))
  (cc/GET "/all-locations"
       []
       (views/all-locations-page))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
