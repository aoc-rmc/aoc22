(ns day1
  (:require
    [clojure.edn :as edn]
    [clojure.string :as str]
    [helper]
    [reagent.core :as r]))

(def data (r/atom nil))

(helper/fetch data "data/day1")

(def answer (r/atom nil))

(def part-1-visible? (r/atom false))

(def part-2-visible? (r/atom false))

(defn answers
  [elves]
  (let [button1-id   (str (gensym "day1-"))
        button2-id   (str (gensym "day1-"))
        sorted-elves (sort elves)
        answer-1     (set [(last sorted-elves)])
        answer-2     (set (take 3 (reverse sorted-elves)))]
    [:div.row
     [:div.col
      [:button.btn.btn-success
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target (str "#" button1-id)
        :aria-expanded "false" :aria-controls "part1"
        :on-click      #(if (helper/toggle-visibility part-1-visible?)
                          (reset! answer answer-1)
                          (reset! answer nil))}
       "Part 1"]]
     [:div.col
      [:button.btn.btn-danger
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target (str "#" button2-id)
        :aria-expanded "false" :aria-controls "part2"
        :on-click      #(if (helper/toggle-visibility part-2-visible?)
                          (reset! answer answer-2)
                          (reset! answer nil))}
       "Part 2"]]
     [:div.row.p-2
      [:div.col
       [:div.collapse.multi-collapse {:id button1-id}
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. Find the amount of calories held by the elf carrying the most calories"]
          [:br]
          [:p "A. " (first answer-1)]]]]]
      [:div.col
       [:div.collapse.multi-collapse {:id button2-id}
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. Find the amount of calories held by the three elves carrying the most calories"]
          [:br]
          [:p {:style {:color :green}} "The three: " (interpose " " answer-2)]
          [:br]
          [:p "A. " (reduce + answer-2)]]]]]]]))

(defn data-view
  [data]
  (let [rows (partition-all 35 data)]
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
           rows)]]))

(defn content
  [day#]
  (let [elves (->> @data
                   str/split-lines
                   (map edn/read-string)
                   (partition-by nil?)
                   (keep #(reduce + %)))]
    [:div
     [:a {:href (str "https://adventofcode.com/2022/day/" day#)}
      "Link to AOC Challenge for day " day#]
     [:br]
     [:br]
     [:h5 "Calorie counting for " (count elves) " elves. They each have this many calories..."]
     [data-view elves]
     [:br]
     [answers elves]]))
