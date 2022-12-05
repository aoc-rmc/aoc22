(ns day3
  "Props to @LuisSantos for this answer. This page is based on that work."
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [helper]
    [reagent.core :as r]))

(def data (r/atom nil))

(helper/fetch data "data/day3")

(def part-1-visible? (r/atom false))

(def part-2-visible? (r/atom false))

(defn toggle-visibility
  [state-visible?]
  (reset! state-visible? (not @state-visible?)))

(def alphabet-lower "abcdefghijklmnopqrstuvwxyz")
(def alphabet-upper "ABCDEFGHIJKLMNOPQRSTUVWXYZ")

(defn halve [s]
  "Returns a vector of 2 equal parts of `s`.  Extra chars will go at the end."
  (split-at (quot (count s) 2) s))

(def priority (zipmap (str alphabet-lower alphabet-upper)
                      (range 1 53)))

(defn common-item [sacks]
  (first (apply set/intersection (map set sacks))))

(defn solve [data split-fn]
  (->> (split-fn data)
       (map common-item)
       (map priority)
       (reduce +)))

(defn part-1 [input]
  (solve input (partial map halve)))

(defn part-2 [input]
  (solve input (partial partition 3)))


(defn answers
  [inputs]
  (let [answer-1 (part-1 inputs)
        answer-2 (part-2 inputs)]
    [:div.row
     [:div.col
      [:button.btn.btn-success
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target "#part1"
        :aria-expanded "false" :aria-controls "part1"
        :on-click      (fn []
                         (reset! part-2-visible? false)
                         (toggle-visibility part-1-visible?))}
       "Part 1"]]
     [:div.col
      [:button.btn.btn-danger
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target "#part2"
        :aria-expanded "false" :aria-controls "part2"
        :on-click      (fn []
                         (reset! part-1-visible? false)
                         (toggle-visibility part-2-visible?))}
       "Part 2"]]
     [:div.row.p-2
      [:div.col
       [:div#part1.collapse.multi-collapse
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. What is the sum of the priorities of those item types?"]
          [:br]
          [:p "A. " answer-1]]]]]
      [:div.col
       [:div#part2.collapse.multi-collapse
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. Find the item type that corresponds to the badges of each three-Elf group. What is the sum of the priorities of those item types?"]
          [:br]
          [:p "A. " answer-2]]]]]]]))

(defn viz-solve [data split-fn]
  (->> (split-fn data)
       (map common-item)))

(defn view-part-1 [input]
  (let [common   (viz-solve input (partial map halve))
        priority (map priority common)]
    (->> (map (fn [c p] [c p]) common priority)
         (sort-by last)
         distinct)))

(defn view-part-2 [input]
  (let [common   (viz-solve input (partial partition 3))
        priority (map priority common)]
    (->> (map (fn [c p] [c p]) common priority)
         (sort-by last)
         distinct)))

(defn data-view
  [data]
  (let [[view-data column-count] (cond
                                   @part-1-visible? [(view-part-1 data) 20]
                                   @part-2-visible? [(view-part-2 data) 20]
                                   :else [data 20])
        rows (partition-all column-count view-data)]
    [:table.table-sm
     [:tbody
      (map-indexed
        (fn [row-idx row]
          (let [font-size (cond
                            (or @part-1-visible? @part-2-visible?) 14
                            (= row-idx 0) 8
                            :else 6)]
            (into [:tr]
                  (map
                    (fn [item]
                      [:td {:style {:font-size font-size}}
                       (if (> (count item) 10)
                         (str (subs item 0 10) "...")
                         (if (vector? item)
                           (str (first item) " " (last item))
                           item))])
                    row))))
        rows)]]))

(defn content
  [day#]
  (let [inputs     (str/split-lines @data)
        commentary (cond
                     @part-1-visible? "Distinct common strings and priorities."
                     @part-2-visible? "Distinct common strings and priorities."
                     :else "Compartment puzzle data.")]
    [:div
     [:a {:href (str "https://adventofcode.com/2022/day/" day#)}
      "Link to AOC Challenge for day " day#]
     [:br]
     [:br]
     [:br]
     [:h5 (str "Rucksack Reorganization on " (count inputs) " rucksacks. " commentary)]
     [data-view inputs]
     [:br]
     [answers inputs]]))