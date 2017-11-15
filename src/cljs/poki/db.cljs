(ns poki.db
  (:require [poki.game :as game]
            [poki.websocket :as ws]))

(def default-db
   (merge
     (game/initial-state 0 0)
     {
      :goal-definition 100
      :local-player nil
      :ws (ws/create-tube "ws://localhost:3449/ws")}))

;; States
;;  :waiting-player
;;  :rolling
;;  :showing-luck
;;  :showing-hold
