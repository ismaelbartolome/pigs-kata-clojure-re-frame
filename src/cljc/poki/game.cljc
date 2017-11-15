(ns poki.game)

(defn initial-state
  ([num-players win-points]
   {:turn        []
    :player      0
    :points      (vec (repeat num-players 0))
    :round       0
    :num-players num-players
    :win-points  win-points})

  ([num-players]
   (initial-state num-players 100)))

(defn add-player
  [{:keys [num-players points] :as state}]
  (-> state
      (assoc :num-players (inc num-players))
      (assoc :points (if (empty? points) [0] (conj points (apply max points))))))

(defn next-player
  [ {:keys  [player num-players round] :as state}]
  (let
    [next-player (mod (inc player)  num-players)
     next-round (if (zero? next-player) (inc round) round)]
    (merge
      state
      {:player (mod (inc player)  num-players)
       :round next-round})))

(defn accumulate-points
  [{:keys [turn points player] :as state}]
  (let [acc-points
         (reduce
           #(+ %1 %2)
           (points player)
           turn)]
     (merge
        state
        {:turn   []
         :points (assoc points player acc-points)})))

(defn hold [state]
  ((comp next-player accumulate-points ) state))

(defn one [state]
  (next-player
    (merge state {:turn []})))

(defn dice
  [ { :keys [turn] :as state}  num]
  (do
    (merge state {:turn (conj turn num)})))

(defn winner?
  [ {:keys  [points player win-points] :as state}]

  (>= (points player) win-points))

(defn do-hold
  "Hold decision"
  [state]
  (let
    [acc-state (accumulate-points state)]
    (if
      (winner? acc-state)
      acc-state
      (next-player acc-state))))




(defn roll-done
  "Roll efect"
  [state value]
  (if
    (= value 1)
    (one state)
    (dice state value)))

(defn do-dice
  "Dice decision"
  [state roller]
  (roll-done state (roller)))

;; Function for dice
(defn random-dice []
     (->
       (rand-int 6)
       inc))


(defn play [state roller get-move]
  (let

    [option (get-move state)]
    (cond
     (= option "h") (do-hold state)
     (= option "d") (do-dice state roller)
     :else (do
             ;(println "OPTION" option)))))
             (play state roller get-move)))))


; ENTRY FUNCTION
(defn pig-loop
  "Game main loop"
  [players target roller get-move]
  (loop
    [state (initial-state players target)]
    (let
      [
       new-state (play state roller get-move)]
      (if
        (winner? new-state)
        new-state
        (recur new-state)))))