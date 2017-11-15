(ns poki.random-service
  (:require
    [poki.game :as game]
    [compojure.core :refer [GET POST defroutes routes]]
    [compojure.handler :refer [api]]
    [ring.util.response :refer [file-response response]]
    [pneumatic-tubes.core :refer [receiver transmitter dispatch]]
    [pneumatic-tubes.httpkit :refer [websocket-handler]]))

(def tx (transmitter))
(def dispatch-to (partial dispatch tx))



(def game-state (atom {}))



(defn ini-game
  [goal]

  (reset!
    game-state
    (assoc
        (game/initial-state 0 (read-string goal))
        :game-state :waiting-player)))

(defn add-player
  []
  (swap! game-state game/add-player))

(defn roll
  []
  (swap! game-state assoc :game-state :rolling))

(defn roll-done
  []
  (let
    [res (game/random-dice)]
    (swap! game-state
           merge {
                  :last-roll res
                  :game-state :showing-luck})))


(defn roll-shown
   []
   (let
     [f (fn
          [s]
          (-> s
              (game/roll-done (:last-roll s))
              (assoc :game-state :waiting-player)))]
     (swap! game-state f)))


(defn hold
  []
  (swap! game-state assoc :game-state :showing-hold))


(defn hold-done
  []
  (let
     [f (fn
           [ s]
           (-> s
               (game/do-hold)
               (assoc :game-state :waiting-player)))]
     (swap! game-state f)))

(defn refresh-clients [ state ev]
  (dispatch-to :all [:poki.remote-events/refresh-game state ev]))

(def rx (receiver
          {:tube/on-create
           (fn [from _]
             from)

           :tube/on-destroy
           (fn [from _]
             from)

           :poki.remote-events/ini-remote
           (fn
             [from [ev goal]]
             (refresh-clients (ini-game goal) ev))

           :poki.remote-events/add-player-remote
           (fn
             [from [ev]]
             (let
               [new-state (add-player)
                player (dec (:num-players new-state))]
               (dispatch-to from [:poki.remote-events/player-assigned player])
               (refresh-clients new-state ev)
               (assoc from :player player)))


           :poki.remote-events/roll
           (fn
             [from [ev]]
             (refresh-clients (roll) ev)
             (dispatch-to from [:poki.remote-events/roll-response])
             from)

           :poki.remote-events/roll-done
           (fn
             [from [ev]]
             (refresh-clients (roll-done) ev)
             (dispatch-to from [:poki.remote-events/roll-done-response])
             from)

           :poki.remote-events/roll-shown
           (fn
             [from [ev]]
             (refresh-clients (roll-shown) ev)
             from)

           :poki.remote-events/hold
           (fn
             [from [ev]]
             (refresh-clients (hold) ev)
             (dispatch-to from [:poki.remote-events/hold-response])
             from)

           :poki.remote-events/hold-shown
           (fn
             [from [ev]]
             (println "SERVER" ev)
             (refresh-clients (hold-done) ev)
             from)}))


(defroutes handler
           (GET "/" [] (file-response "index.html" {:root "resources/public"}))
           (GET "/ws" [] (websocket-handler rx)))