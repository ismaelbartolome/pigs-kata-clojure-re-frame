(ns poki.remote-events
  (:require
    [re-frame.core :as rf]
    [ajax.core     :as ajax]
    [day8.re-frame.http-fx]))

(defn remote-event
  [ db event params get-event]
  (println "remote-event"  (str get-event))
  {
   :http-xhrio {:method          :get
                :uri             (str "http://localhost:3000/" event)
                :params          params
                :format          (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success       [ get-event]
                :on-failure      [::bad-response]}
   :db db})

(rf/reg-event-fx
  ::ini-remote
  (fn
    [{db :db} [_ params]]
    (remote-event db "ini" params  ::get-response)))

(rf/reg-event-fx
  ::add-player-remote
  (fn
    [{db :db} _]
    (remote-event db "add" {} ::get-response)))


;; ROLL
;; -----------------------------
(rf/reg-event-fx
  ::roll
  (fn
    [{db :db} _]
    (remote-event db "roll" {}  ::roll-response)))

(rf/reg-event-fx
  ::roll-response
  (fn
    [{db :db} [ _ state-back]]
    {
     :db (merge db state-back)
     :dispatch-later [{:ms 3000 :dispatch [::roll-done]}]}))


(rf/reg-event-fx
  ::roll-done
  (fn
    [{db :db} _]
    (remote-event db "roll-done" {}  ::roll-done-response)))

(rf/reg-event-fx
  ::roll-done-response
  (fn
    [{db :db} [ _ state-back]]
    {
     :db (merge db state-back)
     :dispatch-later [{:ms 1000 :dispatch [::roll-shown]}]}))


(rf/reg-event-fx
  ::roll-shown
  (fn
    [{db :db} _]
    (remote-event db "roll-shown" {}  ::get-response)))

;; -------------------------------


;; HOLD
;; -------------------------------
(rf/reg-event-fx
  ::hold
  (fn
    [{db :db} _]
    (remote-event db "hold" {}  ::hold-response)))

(rf/reg-event-fx
  ::hold-response
  (fn
    [{db :db} [ _ state-back]]
    {
     :db (merge db state-back)
     :dispatch-later [{:ms 1000 :dispatch [::hold-shown]}]}))

(rf/reg-event-fx
  ::hold-shown
  (fn
    [{db :db} _]
    (remote-event db "hold-done" {}  ::get-response)))

;; --------------------------------------


(rf/reg-event-db
  ::get-response
  (fn
    [db [ _ state-back]]
    (println "get-response" (str state-back))
    (merge db state-back)))



(rf/reg-event-db
  ::keep-goal
  (fn
    [ db [_ value]]
    (-> db
        (assoc :goal-definition value))))


(rf/reg-event-db
  ::bad-response
  (fn
    [db [_ response]]           ;; destructure the response from the event vector
    (-> db
        (assoc :game-state :waiting-player) ;; take away that "Loading ..." UI
        (assoc :error (js->clj response)))))  ;; fairly lame processing
