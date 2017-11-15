(ns poki.remote-events
  (:require
    [re-frame.core :as rf]
    [pneumatic-tubes.core :as tubes]))

(defn on-receive [event-v]
  (.log js/console "received from server:" (str event-v))
  (rf/dispatch event-v))

(def tube (tubes/tube (str "ws://localhost:3449/ws") on-receive))
(def send-to-server (rf/after (fn [_ v] (tubes/dispatch tube v))))

(defn only-dispach-if-local
  [gs]
  (if
    (=
      (get-in gs [:db :player])
      (get-in gs [:db :local-player]))
    gs
    (dissoc gs :dispatch-later)))

(rf/reg-event-db
  ::ini-remote
  send-to-server
  (fn
    [ db [_ goal]]
    db))


(rf/reg-event-db
  ::add-player-remote
  send-to-server
  (fn
    [db _]
    db))

(rf/reg-event-db
  ::player-assigned
  (fn
    [db [_ player]]
    (assoc db :local-player player)))



;; ROLL
;; -----------------------------
(rf/reg-event-db
  ::roll
  send-to-server
  (fn
    [db _]
    db))

(rf/reg-event-fx
  ::roll-response
  (fn
    [{db :db} _]
    (only-dispach-if-local
      {
       :db db
       :dispatch-later [{:ms 3000 :dispatch [::roll-done]}]})))


(rf/reg-event-db
  ::roll-done
  send-to-server
  (fn
    [db _]
    db))

(rf/reg-event-fx
  ::roll-done-response
  (fn
    [{db :db} _]
    (only-dispach-if-local
      {
       :db db
       :dispatch-later [{:ms 1000 :dispatch [::roll-shown]}]})))


(rf/reg-event-db
  ::roll-shown
  send-to-server
  (fn
    [db _]
    db))

;; -------------------------------


;; HOLD
;; -------------------------------
(rf/reg-event-db
  ::hold
  send-to-server
  (fn
    [db _]
    db))

(rf/reg-event-fx
  ::hold-response
  (fn
    [{db :db} _]
    (only-dispach-if-local
      {
       :db db
       :dispatch-later [{:ms 1000 :dispatch [::hold-shown]}]})))



(rf/reg-event-db
  ::hold-shown
  send-to-server
  (fn
    [db _]
    db))

;; --------------------------------------


(rf/reg-event-db
  ::refresh-game
  (fn
    [db [ _ state-back ori]]
    (println "get-response" (str state-back))
    (println "MERGESTATE" ori (merge db state-back))
    (merge db state-back)))



(rf/reg-event-db
  ::keep-goal
  (fn
    [ db [_ value]]
    (-> db
        (assoc :goal-definition value))))


((tubes/create! tube))