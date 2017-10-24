(ns poki.db
  (:require [poki.game :as game]))

(def default-db
   (game/initial-state 2 10))
