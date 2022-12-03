(ns day1
  (:require
    [clojure.string :as str]
    [helper]
    [reagent.core :as r]))

(def data (r/atom nil))

(helper/fetch data "data/day1")

(def answer (r/atom nil))

(def part-1-visible? (atom false))

(def part-2-visible? (atom false))

(defn toggle-visibility
  [state-visible? result]
  (if @state-visible?
    (do
      (reset! state-visible? false)
      (reset! answer nil))
    (do
      (reset! state-visible? true)
      (reset! answer result))))

(defn parts
  [elves]
  (let [sorted-elves (sort elves)
        answer-1 (set [(last sorted-elves)])
        answer-2 (set (take 3 (reverse sorted-elves)))]
    [:div.row
     [:div.col
      [:button.btn.btn-success
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target "#part1"
        :aria-expanded "false" :aria-controls "part1"
        :on-click      #(toggle-visibility part-1-visible? answer-1)}
       "Part 1"]]
     [:div.col
      [:button.btn.btn-danger
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target "#part2"
        :aria-expanded "false" :aria-controls "part2"
        :on-click      #(toggle-visibility part-2-visible? answer-2)}
       "Part 2"]]
     [:div.row.p-2
      [:div.col
       [:div#part1.collapse.multi-collapse
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. Find the amount of calories held by the elf carrying the most calories"]
          [:br]
          [:p "A. " (first answer-1)]]]]]
      [:div.col
       [:div#part2.collapse.multi-collapse
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. Find the amount of calories held by the three elves carrying the most calories"]
          [:br]
          [:p {:style {:color :green}} "The three: " (interpose " " answer-2)]
          [:br]
          [:p "A. " (reduce + answer-2)]]]]]]]))

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
     [:h5 "Calorie counting for " (count elves) " elves. They each have this many calories..."]
     [elf-table elves]
     [:br]
     [parts elves]]))
