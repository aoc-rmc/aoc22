(ns aoc-ui
  (:require
    [day1]
    [reagent.dom :as rdom]))

(defn days
  []
  [:div
   [:h2 "Advent of Code 2022 - Made with scittle"
    [:small.text-muted " a simple way to run Clojure in your browser"]]
   [:ul#pills-tab.nav.nav-pills.mb-3 {:role "tablist"}
         [:li.nav-item {:role "presentation"}
          [:button#pills-home-tab.nav-link.active
           {:data-bs-toggle "pill" :data-bs-target "#pills-home" :type "button"
            :role "tab" :aria-controls "pills-home" :aria-selected "true"}
           "Day 1"]]
         [:li.nav-item {:role "presentation"}
          [:button#pills-disabled-tab.nav-link
           {:data-bs-toggle "pill" :data-bs-target "#pills-disabled" :type "button"
            :role "tab" :aria-controls "pills-disabled" :aria-selected "false" :disabled "true"}
           "Day 2"]]]
   [:div#pills-tabContent.tab-content
    [:div#pills-home.tab-pane.fade.show.active {:role "tabpanel" :aria-labelledby "pills-home-tab" :tabindex "0"}
     [day1/content 1]]
    [:div#pills-disabled.tab-pane.fade {:role "tabpanel" :aria-labelledby "pills-disabled-tab" :tabindex "0"}
     "Empty"]]])

(defn home-page
  []
  [:div.m-3.p-3.border-0
   [days]])


(rdom/render [home-page] (js/document.getElementById "app"))
