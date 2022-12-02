(ns day1
  (:require
    [clojure.string :as str]
    [helper]
    [reagent.core :as r]))

(def data (r/atom nil))

(helper/fetch data "data/day1")

(def answer (r/atom nil))

(defn parts
  [elves]
  [:div.row
   [:div.col
    [:button.btn.btn-success
     {:type          "button" :data-bs-toggle "collapse" :data-bs-target "#part1"
      :aria-expanded "false" :aria-controls "part1"
      :on-click      (fn [] (reset! answer (set [(last (sort elves))])))}
     "Part 1"]]
   [:div.col
    [:button.btn.btn-danger
     {:type          "button" :data-bs-toggle "collapse" :data-bs-target "#part2"
      :aria-expanded "false" :aria-controls "part2"
      :on-click      (fn [] (reset! answer (set (take 3 (reverse (sort elves))))))}
     "Part 2"]]
   [:div.row.p-2
    [:div.col
     [:div#part1.collapse.multi-collapse
      [:div.card
       [:div.card-body
        [:h5.card-title "Q. Find the amount of calories held by the elf carrying the most calories"]
        [:br]
        [:p "A. " (last (sort elves))]]]]]
    [:div.col
     [:div#part2.collapse.multi-collapse
      [:div.card
       [:div.card-body
        [:h5.card-title "Q. Find the amount of calories held by the three elves carrying the most calories"]
        [:br]
        [:p "The three: " (interpose " " (take 3 (reverse (sort elves))))]
        [:br]
        [:p "A. " (reduce + (take 3 (reverse (sort elves))))]]]]]]])

(defn elf-table
  [elves]
  (let [elf-rows (partition-all 35 elves)]
    [:table.table-sm
     [:tbody
      (map (fn [row]
             (let [answer-in-row? (when @answer (some @answer row))
                   font-size      (if answer-in-row? 12 9)]
               (into [:tr]
                     (map (fn [elf-calories]
                            [:td {:style {:font-size font-size
                                          :color     (cond
                                                       (and answer-in-row?
                                                            (not (contains? @answer elf-calories))) "lightgray"
                                                       (contains? @answer elf-calories) "green"
                                                       :else "black")}} elf-calories])
                          row))))
           elf-rows)]]))

(defn content
  [day#]
  (let [elves (->> @data
                   str/split-lines
                   (map (fn [s]
                          (let [n (js/parseInt s)]
                            (when-not (js/isNaN n) n))))
                   (partition-by nil?)
                   (keep #(reduce + %)))]
    [:div
     [:a {:href (str "https://adventofcode.com/2022/day/" day#)}
      "Link to AOC Challenge for day " day#]
     [:br]
     [:br]
     [:h3 "Calorie counting for " (count elves) " elves. They each have this many calories..."]
     [elf-table elves]
     [:br]
     [parts elves]]))
