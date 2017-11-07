(ns poki.db
  (:require [poki.game :as game]))

(def default-db
   (merge
     (game/initial-state 2 10)
     {:game-state :waiting-player  ;; :rolling  :showing-luck
      :last-roll ""}))