(ns poki.components
  (:require
    [reagent.core  :as reagent]
    [clojure.string :as string]
    [re-frame.core :as rf]))


(defn <sub [ & s] @(rf/subscribe (vec s)))
(defn evt> [ & e] #(rf/dispatch (vec e)))


(defn input-text [{:keys [title on-save on-stop reset-on-save]}]
  (let [val  (reagent/atom title)
        stop #(do (when reset-on-save (reset! val ""))
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str string/trim)]
                (when (seq v) (on-save v))
                (stop))]
    (fn [props]
      [:input (merge ( apply dissoc props [ :on-save :on-stop :reset-on-save])
                     {:type        "text"
                      :value       @val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(reset! val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      27 (stop)
                                      nil)})])))

