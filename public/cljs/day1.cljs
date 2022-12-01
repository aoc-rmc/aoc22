(ns day1
  (:require
    [clojure.string :as str]
    [helper]
    [reagent.core :as r]))

(def data (r/atom nil))

(helper/fetch data "inputs/day1")


(defn parts
  [elves]
  [:div.row.p-3
   [:div.col
    [:button.btn.btn-success {:type "button" :data-bs-toggle "collapse" :data-bs-target "#multiCollapseExample1" :aria-expanded "false" :aria-controls "multiCollapseExample2"} "Part 1"]]
   [:div.col
    [:button.btn.btn-danger {:type "button" :data-bs-toggle "collapse" :data-bs-target "#multiCollapseExample2" :aria-expanded "false" :aria-controls "multiCollapseExample2"} "Part 2"]]
   [:div.row
    [:div.col
     [:div#multiCollapseExample1.collapse.multi-collapse
      [:div.card.card-body
       [:p "Q. Find the amount of calories held by the elf carrying the most calories"]
       [:p "A. " (last (sort elves))]]]]
    [:div.col
     [:div#multiCollapseExample2.collapse.multi-collapse
      [:div.card.card-body
       [:p "Find the amount of calories held by the three elves carrying the most calories"]
       [:p "The three: " (interpose " " (take 3 (reverse (sort elves))))]
       [:p "A. " (reduce + (take 3 (reverse (sort elves))))]]]]]])

(defn elf-table
  [elves]
  (let [elf-rows (partition-all 35 elves)]
    [:table.table-sm
     [:tbody
      (map (fn [row]
             (into [:tr]
                   (map (fn [elf]
                          [:td {:style {:font-size 8}} elf])
                        row)))
           elf-rows)]]))

(defn content
  [day#]
  (let [elves (->> @data
                   str/split-lines
                   (map js/parseInt)
                   (partition-by js/isNaN)
                   (keep (fn [xs]
                           (when-not (js/isNaN (first xs))
                             (reduce + xs)))))]
    [:div
     [:a {:href (str "https://adventofcode.com/2022/day/" day#)}
      "Link to AOC Challenge for day " day#]
     [:h3 "Calorie counting for " (count elves) " elves"]
     [elf-table elves]
     [parts elves]]))
