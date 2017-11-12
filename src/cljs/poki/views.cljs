(ns poki.views
  (:require [re-frame.core :as rf]
            [poki.subs :as sb]
            [poki.remote-events :as pre]
            [reagent.core  :as reagent]
            [clojure.string :as string]
            [poki.components :refer [<sub evt> input-text]]))



(defn info-section
  []
  [:div.playboard
   [:div.head
    [:div "Goal: " (str (<sub ::sb/goal-definition))]
    [:div "  Players: " (str (<sub ::sb/num-players))]
    [:div "  Round: " (str (<sub ::sb/round))]
    (when
      (and
        (> (<sub ::sb/num-players) 0)
        (<sub ::sb/is-game-over))
      [:h1 "The Winner is " (inc (<sub ::sb/current-player))])
    [:br]
    [:br]]])


(defn game-control
  []
  [:div.controls
     (when (= (<sub ::sb/goal) 0)
       [:div.control
          [:button.control
           {:on-click (evt> ::pre/ini-remote {:goal (<sub ::sb/goal-definition)})}
           "Ini Game"]

          [input-text
           {:id "players"
            :title (<sub ::sb/goal-definition)
            :placeholder "Num of Players"
            :reset-on-save false
            :on-save #((evt> ::pre/keep-goal %))}]])

     [:button.control
       {:on-click (evt> ::pre/add-player-remote)}
       "New player"]])




(defn roll-section []
  (condp = (<sub ::sb/game-state)
    "rolling"   [:img { :src "images/dado.gif"}]
    "showing-luck" [:h4 (str (<sub ::sb/last-roll))]
    "showing-hold" [:h4 "HOLD"]
    "waiting-player"
        [:div.buttons
           [:div.butttonbox
               [:button.button
                  {:on-click (evt> ::pre/roll)}
                  "Roll"]]
           [:div.buttonbox
               [:button.button
                  {:on-click (evt> ::pre/hold)}
                  "Hold!"]]]))


(defn player [n] [:div  "Player " (str (inc n))])
(defn score [n] [:div  "Score:" (str ((<sub ::sb/game-score) n))])

(defn player-actual-box
  [n]

  [:div.player.current {:key (str "player-" n)}
    (player n)
    (score n)
    [:div    (string/join "-" (<sub ::sb/current-score))]
    (roll-section)])

(defn player-box
  [ n]
  [:div.player {:key (str "player-" n)}
    (player n)
    (score n)])

(defn player-section
  []
  [:div.players
   (doall
     (for
       [player (range (<sub ::sb/num-players))]
       (if
         (and
           (not (<sub ::sb/is-game-over))
           (= (<sub ::sb/current-player) player))
         (player-actual-box  player)
         (player-box  player))))])

(defn main-panel
  []
  [:div
   [:h1#title "Pig dice game"]

   (game-control)

   (info-section)

   (player-section)])

