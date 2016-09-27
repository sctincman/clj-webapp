(ns server.views
  (:require [server.db :as db]
            [clojure.string :as str]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [hiccup.page :as hic-p]))

(defn gen-page-head
  [title]
  [:head
   [:title (str "Plog: " title)]
   (hic-p/include-css "/css/styling.css")])

(def header-links
  [:div#header-links
   "[ "
   [:a {:href "/"} "Home"]
   " | "
   [:a {:href "/add-location"} "Add a Location"]
   " | "
   [:a {:href "/all-locations"} "View All Locations"]
   " ]"])

(defn header-bar [title]
  [:div#header-bar
   [:header.banner.banner-top
    [:h1 title]
    [:nav
     [:ul
      [:li [:a {:href "/users"} "Users"]]
      [:li [:a {:href "/posts"} "Posts"]]
      [:li [:a {:href "/projects"} "Projects"]]
      [:li [:a {:href "/about"} "About"]]]
     [:ul
      [:li [:a {:href "/new-post"} "New Post"]]
      [:li [:a {:href "/new-project"} "New Project"]]
      [:li [:a {:href "/profile"} "Profile"]]]]]])

(def footer-bar
  [:div#footer-bar
   [:footer.banner.banner-bottom
    "This work licensed under a "
    [:a {:href "http://creativecommons.org/licenses/by/4.0/"}
     "Creative Commons Attribution 4.0 International License"]]])

(defn home-page
  []
  (hic-p/html5
   (gen-page-head "Home")
   (header-bar "Home")
   [:h1 "Home"]
   [:p "Webapp to store and display some 2D (x,y) locations."]
   footer-bar))

(defn add-location-page
  []
  (hic-p/html5
   (gen-page-head "Add a Location")
   header-links
   [:h1 "Add a Location"]
   [:form {:action "/add-location" :method "POST"}
    (anti-forgery-field)
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
     (header-bar "All users")
     [:h1 "All users"]
     [:table
      [:tr [:th "id"] [:th "username"] [:th "hash"] [:th "salt"] [:th "email"] [:th "created"]]
      (for [user all-users]
        [:tr [:td (:id user)] [:td [:a {:href (str "/user/" (:username user))} (:username user)]] [:td (:passwordhash user)] [:td (:salt user)] [:td (:email user)] [:td (:creationdate user)]])]
     footer-bar)))

(defn user-page [username]
  (let [user (db/get-user-by-name username)
        projects (db/get-all-projects username)]
    (hic-p/html5
     (gen-page-head (str "Userpage for " username))
     (header-bar username)
     [:h1 (:username user)]
     [:p "id: " (:id user)]
     [:p "email: " (:email user)]
     [:p "Joined: " (:creationdate user)]
     [:ul
      (map (fn [project] [:li [:a {:href (str "/user/" username "/projects/" (:id project))} (:title project)] ": " (:description project)])
           projects)]
     footer-bar)))

(defn project-list [username]
  (let [projects (db/get-all-projects username)]
    (hic-p/html5
     (gen-page-head (str "Projects for " username))
     (header-bar (str username ": projects"))
     [:h1 "Projects"]
     [:h2 username]
     [:ul
      (for [project projects]
        [:li [:a {:href (str "/user/" username "/projects/" (:id project))} (:title project)] ": " (:decription project)])]
     footer-bar)))

(defn post-list [username projectid]
  (let [posts (db/get-posts username projectid)]
    (hic-p/html5
     (gen-page-head (str "Posts for " projectid))
     (header-bar (str username ": " projectid " (replace me by project lookup)"))
     [:h1 projectid " : Posts (lookup projectname)"]
     [:h2 username]
     [:ul
      (for [post posts]
        [:li [:a {:href (str "/user/" username "/posts/" (:id post))} (:title post)]])]
     footer-bar)))

(defn project-page [username projectid]
  (let [project (db/get-project username projectid)
        posts (db/get-posts username projectid)]
    (hic-p/html5
     (gen-page-head (str "Project page: " (:title project)))
     (header-bar (str username ": " (:title project)))
     [:h1 (:title project)]
     [:h2 username]
     [:p "Created: " (:creationdate project)]
     [:p (:description project)]
     [:ul
      (map (fn [post] [:li [:a {:href (str "/user/" username "/posts/" (:id post))} (:title post)]]) posts)]
     footer-bar)))

(defn post-page [username postid]
  (let [post (db/get-post username postid)]
    (hic-p/html5
     (gen-page-head (str postid))
     (header-bar (str (:project post) ": " (:title post)))
     [:h1 (:project post) ": " (:title post)]
     [:p "Posted: " (:creationdate post)] ;; conditionally add modification date?
     [:p (:content post)]
     footer-bar)))
