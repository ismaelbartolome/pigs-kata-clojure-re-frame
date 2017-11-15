(ns poki.remote-events
  (:require
    [re-frame.core :as rf]
    [poki.websocket :as ws]))

(defn only-dispach-if-local
  [gs]
  (if
    (=
      (get-in gs [:db :player])
      (get-in gs [:db :local-player]))
    gs
    (dissoc gs :dispatch-later)))

(ws/reg-event ::ini-remote :ws)

(ws/reg-event ::add-player-remote :ws)


(rf/reg-event-db
  ::player-assigned
  (fn
    [db [_ player]]
    (assoc db :local-player player)))



;; ROLL
;; -----------------------------
(ws/reg-event ::roll :ws)

(rf/reg-event-fx
  ::roll-response
  (fn
    [{db :db} _]
    (only-dispach-if-local
      {
       :db db
       :dispatch-later [{:ms 3000 :dispatch [::roll-done]}]})))

(ws/reg-event ::roll-done :ws)

(rf/reg-event-fx
  ::roll-done-response
  (fn
    [{db :db} _]
    (only-dispach-if-local
      {
       :db db
       :dispatch-later [{:ms 1000 :dispatch [::roll-shown]}]})))

(ws/reg-event ::roll-shown :ws)

;; -------------------------------


;; HOLD
;; -------------------------------
(ws/reg-event ::hold :ws)

(rf/reg-event-fx
  ::hold-response
  (fn
    [{db :db} _]
    (only-dispach-if-local
      {
       :db db
       :dispatch-later [{:ms 1000 :dispatch [::hold-shown]}]})))


(ws/reg-event ::hold-shown :ws)

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
