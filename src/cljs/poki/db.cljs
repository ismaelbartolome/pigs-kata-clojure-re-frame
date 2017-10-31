(ns poki.db
  (:require [poki.game :as game]))

(def default-db
   (merge
     (game/initial-state 2 50)
     {:rolling? false :last-roll ""}))