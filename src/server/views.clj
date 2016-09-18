(ns server.views
  (:require [server.db :as db]
            [clojure.string :as str]
            [hiccup.page :as hic-p]))

(defn gen-page-head
  [title]
  [:head
   [:title (str "Locations: " title)]
   (hic-p/include-css "/css/styles.css")])

(def header-links
  [:div#header-links
   "[ "
   [:a {:href "/"} "Home"]
   " | "
   [:a {:href "/add-location"} "Add a Location"]
   " | "
   [:a {:href "/all-locations"} "View All Locations"]
   " ]"])

(defn home-page
  []
  (hic-p/html5
   (gen-page-head "Home")
   header-links
   [:h1 "Home"]
   [:p "Webapp to store and display some 2D (x,y) locations."]))

(defn add-location-page
  []
  (hic-p/html5
   (gen-page-head "Add a Location")
   header-links
   [:h1 "Add a Location"]
   [:form {:action "/add-location" :method "POST"}
    [:p "x value: " [:input {:type "text" :name "x"}]]
    [:p "y value: " [:input {:type "text" :name "y"}]]
    [:p [:input {:type "submit" :value "submit location"}]]]))

(defn add-location-results-page
  [{:keys [x y]}]
  (let [id (db/add-location-to-db x y)]
    (hic-p/html5
     (gen-page-head "Added a Location")
     header-links
     [:h1 "Added a Location"]
     [:p "Added [" x ", " y "] (id: " id ") to the db. "
      [:a {:href (str "/location/" id)} "See for yourself"]
      "."])))

(defn location-page
  [loc-id]
  (let [{x :x y :y} (db/get-xy loc-id)]
    (hic-p/html5
     (gen-page-head (str "Location " loc-id))
     header-links
     [:h1 "A Single Location"]
     [:p "id: " loc-id]
     [:p "x: " x]
     [:p "y: " y])))

(defn all-locations-page
  []
  (let [all-locs (db/get-all-locations)]
    (hic-p/html5
     (gen-page-head "All Locations in the db")
     header-links
     [:h1 "All Locations"]
     [:table
      [:tr [:th "id"] [:th "x"] [:th "y"]]
      (for [loc all-locs]
        [:tr [:td (:id loc)] [:td (:x loc)] [:td (:y loc)]])])))

;; Of course, this needs to be changed!
;;; Leaking hashes is no good, keep for debugging?
(defn user-list []
  (let [all-users (db/get-all-users)]
    (hic-p/html5
     (gen-page-head "All users")
     header-links
     [:h1 "All users"]
     [:table
      [:tr [:th "id"] [:th "username"] [:th "hash"] [:th "salt"] [:th "email"] [:th "created"]]
      (for [user all-users]
        [:tr [:td (:id user)] [:td (:username user)] [:td (:passwordhash user)] [:td (:salt user)] [:td (:email user)] [:td (:creationdate user)]])])))

(defn user-page [username]
  (let [user (db/get-user-by-name username)]
    (hic-p/html5
     (gen-page-head (str "Userpage for " username))
     header-links
     [:h1 (:username user)]
     [:p "id: " (:id user)]
     [:p "email: " (:email user)]
     [:p "Joined: " (:creationdate user)])))

(defn post-page [username postid]
  (let [post (db/get-post username postid)]
    (hic-p/html5
     (gen-page-head (str postid))
     header-links
     [:h1 (:project post) ": " (:title post)]
     [:p "Posted: " (:creationdate post)] ;; conditionally add modification date?
     [:p (:content post)])))
