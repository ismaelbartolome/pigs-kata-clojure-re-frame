(ns poki.views
  (:require [re-frame.core :as re-frame]
            [poki.subs :as subs]))


(defn main-panel []
  (let [players (re-frame/subscribe [::subs/num-players])
        current-player (re-frame/subscribe [::subs/current-player])
        current-score (re-frame/subscribe [::subs/current-score])
        game-score (re-frame/subscribe [::subs/game-score])
        round (re-frame/subscribe [::subs/round])
        is-game-over (re-frame/subscribe [::subs/is-game-over])
        rolling? (re-frame/subscribe [::subs/rolling?])
        last-roll (re-frame/subscribe [::subs/last-roll])]

    (if-not @is-game-over
     [:div
      [:div "Total players: " @players]
      [:div "current player: " @current-player]
      [:div "current score: " @current-score]
      [:div "score: " @game-score]
      [:div "round: " @round]
      (if
        @rolling?
        [:img {:src "images/dado.gif"}]
        [:div
         [:div @last-roll]
         [:button {:on-click #(re-frame/dispatch [:poki.events/roll-cofx])} "Roll me please"]
         [:button {:on-click #(re-frame/dispatch [:poki.events/hold])} "Hold!"]])]

     [:div "The Winner is " @current-player])))
