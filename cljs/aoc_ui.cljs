(ns aoc-ui
  (:require
    [clojure.edn :as edn]
    [day1]                                                  ;; All solved nses must be here!!
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(def solutions (r/atom nil))

(helper/fetch solutions "data/solutions.edn")

(defn days
  [solved-list]
  [:div
   [:h2 "Advent of Code 2022 - Made with scittle"
    [:small.text-muted " a simple way to run Clojure in your browser"]]
   (into [:ul#pills-tab.nav.nav-pills.mb-3 {:role "tablist"}]
         (map (fn [[day solved?]]
                (if solved?
                  [:li.nav-item {:role "presentation"}
                   [:button#pills-home-tab.nav-link.active
                    {:data-bs-toggle "pill" :data-bs-target (str "#pills-" day) :type "button"
                     :role           "tab" :aria-controls (str "pills-" day) :aria-selected "true"}
                    (str "Day " day)]]
                  [:li.nav-item {:role "presentation"}
                   [:button#pills-disabled-tab.nav-link
                    {:data-bs-toggle "pill" :data-bs-target (str "#pills-" day) :type "button"
                     :role           "tab" :aria-controls (str "pills-" day) :aria-selected "false" :disabled "true"}
                    (str "Day " day)]]))
              solved-list))
   (into [:div#pills-tabContent.tab-content]
         (map (fn [[day solved?]]
                (if solved?
                  [:div.tab-pane.fade.show.active
                   {:id (str "pills-" day) :role "tabpanel"
                    :aria-labelledby "pills-home-tab" :tabindex "0"}
                   (eval [(symbol (str "day" day "/content")) day])]
                  [:div.tab-pane.fade
                   {:id (str "pills-" day) :role "tabpanel"
                    :aria-labelledby "pills-disabled-tab" :tabindex "0"}
                   "Empty"]))
              solved-list))])

(defn home-page
  []
  (let [solution-data (edn/read-string @solutions)]
    [:div.m-3.p-3.border-0
     [days solution-data]]))


(rdom/render [home-page] (js/document.getElementById "app"))
