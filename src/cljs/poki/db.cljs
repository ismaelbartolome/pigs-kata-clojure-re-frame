(ns poki.db
  (:require [poki.game :as game]))

(def default-db
   (merge
     (game/initial-state 0 0)
     {  :goal-definition 100}))

;; States
;;  :waiting-player
;;  :rolling
;;  :showing-luck
