(ns poki.websocket
  (:require
    [re-frame.core :as rf]
    [pneumatic-tubes.core :as tubes]))

(defn on-receive [event-v]
  (.log js/console "received from server:" (str event-v))
  (rf/dispatch event-v))


(defn create-tube
  [ path]
  (let
    [tub (tubes/tube (str path) on-receive)]
    (tubes/create! tub)
    tub))


(rf/reg-fx
  :send-ws
  (fn [[ tub event]]
     (tubes/dispatch tub event)))


(defn reg-event
  [event tub]
  (rf/reg-event-fx
    event
    (fn
      [{db :db :as cofx} event-command]
      {:db db
       :send-ws  [(tub db) event-command]})))


