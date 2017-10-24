(ns poki.subs
  (:require [re-frame.core :as re-frame]
            [poki.game :as game]))

(re-frame/reg-sub
 ::num-players
 (fn [db]
   (:num-players db)))

(re-frame/reg-sub
  ::current-player
  (fn [db]
    (:player db)))

(re-frame/reg-sub
  ::current-score
  (fn [db]
    (str (:turn db))))

(re-frame/reg-sub
  ::is-game-over
  (fn [db]
    (game/winner? db)))

(re-frame/reg-sub
  ::game-score
  (fn [db]
    (str (:points db))))

(re-frame/reg-sub
  ::round
  (fn [db]
    (:round db)))

