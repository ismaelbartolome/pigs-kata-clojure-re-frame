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
    (:turn db)))

(re-frame/reg-sub
  ::is-game-over
  (fn [db]
    (game/winner? db)))

(re-frame/reg-sub
  ::game-score
  (fn [db]
    (:points db)))

(re-frame/reg-sub
  ::round
  (fn [db]
    (:round db)))

(re-frame/reg-sub
  ::game-state
  (fn [db]
    (:game-state db)))

(re-frame/reg-sub
  ::last-roll
  (fn [db]
    (:last-roll db)))


(re-frame/reg-sub
  ::goal
  (fn [db]
    (:win-points db)))

(re-frame/reg-sub
  ::goal-definition
  (fn [db]
    (:goal-definition db)))