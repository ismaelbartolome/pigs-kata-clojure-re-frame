(ns poki.views
  (:require [re-frame.core :as re-frame]
            [poki.subs :as subs]
            [clojure.string :as string]))


(defn roll-section [ n game-state last-roll]
  (condp = game-state
    :rolling   [:img { :src "images/dado.gif"}]
    :showing-luck [:h4 (str last-roll)]
    :waiting-player
        [:div.buttons
           [:div.butttonbox
               [:button.button
                  {:on-click #(re-frame/dispatch [:poki.events/roll-remote])}
                  "Roll"]]
           [:div.buttonbox
               [:button.button
                  {:on-click #(re-frame/dispatch [:poki.events/hold])}
                  "Hold!"]]]))


(defn player-actual-box
  [n score round-score rolling? last-roll]

  [:div.player.current {:key (str "player-" n)}
    [:div  "Player " (str (inc n))]
    [:div  "Score:" score]
    [:div    (string/join "-" round-score)]
    (roll-section n rolling? last-roll)])

(defn player-box
  [ n score]
  [:div.player {:key (str "player-" n)}
    [:div  "Player :" (str (inc n))]
    [:div  "Score  :" score]])




(defn main-panel []
  (let [players (re-frame/subscribe [::subs/num-players])
        current-player (re-frame/subscribe [::subs/current-player])
        current-score (re-frame/subscribe [::subs/current-score])
        game-score (re-frame/subscribe [::subs/game-score])
        round (re-frame/subscribe [::subs/round])
        is-game-over (re-frame/subscribe [::subs/is-game-over])
        game-state (re-frame/subscribe [::subs/game-state])
        last-roll (re-frame/subscribe [::subs/last-roll])
        goal (re-frame/subscribe [::subs/goal])]


    [:div.playboard
     [:div.head
        [:h1 "Pig dice game"]
        [:div "Goal:" (str @goal)]
        (if @is-game-over
          [:h1 "The Winner is " (inc @current-player)])
        [:br]
        [:br]]

     (let
        [current @current-player
         players-score @game-score
         v-current-score @current-score
         v-game-state @game-state
         v-last-roll @last-roll
         game-over? @is-game-over]
        [:div.players
         (for
           [player (range @players)]
           (if
             (and (not game-over?) (= current player))
             (player-actual-box
               player
               (players-score player)
               v-current-score
               v-game-state
               v-last-roll)
             (player-box
               player
               (players-score player))))])]))
     ;[:div
     ; [:div "Total players: " (str @players)]
     ; [:div "current player: " (str @current-player)]
     ; [:div "current score: " (str @current-score)]
     ; [:div "score: " (str @game-score)]
     ; [:div "round: " (str @round)]]]))