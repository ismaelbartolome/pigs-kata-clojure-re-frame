(ns poki.events
  (:require [re-frame.core :as re-frame]
            [poki.db :as db]
            [poki.game :as game]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]))


(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
  ::hold
  (fn  [db _]
    (game/do-hold db)))


;; REMOTE ROLL

(re-frame/reg-event-fx        ;; <-- note the `-fx` extension
  ::roll-remote               ;; <-- the event id
  (fn                ;; <-- the handler function
    [{db :db} _]     ;; <-- 1st argument is coeffect, from which we extract db

    ;; we return a map of (side) effects
    {:http-xhrio {:method          :get
                  :uri             "http://localhost:3000"
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::roll-received]
                  :on-failure      [::bad-response]}
     :db  (assoc db :rolling? true)}))


(re-frame/reg-event-db
  ::roll-received
  (fn
    [db [_ response]] ;; destructure the response from the event vector
    (let
       [value (:roll-result (js->clj response))]


       (-> db
           (assoc :rolling? false) ;; take away that "Loading ..." UI
           (assoc :last-roll value)
           (game/roll-done value)))))  ;; fairly lame processing

(re-frame/reg-event-db
  ::bad-response
  (fn
    [db [_ response]]           ;; destructure the response from the event vector
    (-> db
        (assoc :rolling? false) ;; take away that "Loading ..." UI
        (assoc :error (js->clj response)))))  ;; fairly lame processing


;; BROWSER ROLL Side efect
(re-frame/reg-event-db
  ::roll
  (fn  [db _]
    (game/do-dice db game/random-dice)))


;; COEFECT ROLL

(re-frame/reg-event-fx
  ::roll-cofx
  [(re-frame/inject-cofx :roll-generated)]
  (fn [ cofx event]
    (let
      [ value (:roll-generated cofx)
        db (:db cofx)]
      {:db (game/roll-done db value)})))

(re-frame/reg-cofx
  :roll-generated
  (fn [coeffects _]
    (assoc coeffects :roll-generated (game/random-dice))))
