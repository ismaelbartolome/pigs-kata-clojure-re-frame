(ns poki.random-service
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [poki.game :as game]
    [clojure.data.json :as json]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.cors :refer [wrap-cors]]))

(def game-state (atom {}))
;; :game-state :waiting-player  ;; :rolling  :showing-luck : ""

(defn roll-response
  []
  (do
    (Thread/sleep 1000)
    (json/write-str
      {
       :roll-result (game/random-dice)})))


(defn ini-game
  [goal]
  (json/write-str
    (reset!
      game-state
      (assoc
          (game/initial-state 0 (read-string goal))
          :game-state :waiting-player))))

(defn add-player
  []
  (json/write-str
    (swap! game-state game/add-player)))

(defn roll
  []
  (json/write-str
    (swap! game-state assoc :game-state :rolling)))

(defn roll-done
  []
  (let
    [res (game/random-dice)
     f (fn
         [ s res]
         (-> s
             (game/roll-done res)
             (assoc :game-state :showing-luck)
             (assoc :last-roll res)))]
    (json/write-str
      (swap! game-state f res))))

(defn roll-shown
   []
   (json/write-str
     (swap! game-state assoc :game-state :waiting-player)))

(defn hold
  []
  (json/write-str
    (swap! game-state assoc :game-state :showing-hold)))


(defn hold-done
  []
  (let
     [f (fn
           [ s]
           (-> s
               (game/do-hold)
               (assoc :game-state :waiting-player)))]
     (json/write-str
       (swap! game-state f))))

(defn refresh
  []
  (json/write-str
    @game-state))

(defroutes app-routes
           (GET "/" [] (roll-response))
           (GET "/ini" [goal] (ini-game goal))
           (GET "/add" [] (add-player))
           (GET "/roll" [] (roll))
           (GET "/roll-done" [] (roll-done))
           (GET "/roll-shown" [] (roll-shown))
           (GET "/hold" [] (hold))
           (GET "/hold-done" [] (hold-done))
           (GET "/refresh" [] (refresh))
           (route/not-found "Not Found"))


(def app
  (wrap-cors
    (wrap-defaults app-routes site-defaults)
    :access-control-allow-origin [#"http://localhost:3449"]
    :access-control-allow-methods [:get]))