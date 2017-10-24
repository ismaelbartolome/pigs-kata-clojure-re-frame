(ns poki.events
  (:require [re-frame.core :as re-frame]
            [poki.db :as db]
            [poki.game :as game]))

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))


(re-frame/reg-event-db
  ::roll
  (fn  [db _]
    (game/do-dice db game/random-dice)))

(re-frame/reg-event-db
  ::hold
  (fn  [db _]
    (game/do-hold db)))