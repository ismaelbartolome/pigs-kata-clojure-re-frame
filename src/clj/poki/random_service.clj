(ns poki.random-service
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [poki.game :as game]
    [clojure.data.json :as json]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.cors :refer [wrap-cors]]))

(defn roll-response
  []
  (do
    (Thread/sleep 1000)
    (json/write-str
      {
       :roll-result (game/random-dice)})))



(defroutes app-routes
           (GET "/" [] (roll-response))
           (route/not-found "Not Found"))


(def app
  (wrap-cors
    (wrap-defaults app-routes site-defaults)
    :access-control-allow-origin [#"http://localhost:3449"]
    :access-control-allow-methods [:get]))