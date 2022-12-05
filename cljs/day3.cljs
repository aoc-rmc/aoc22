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

(defn explainer
  [inputs]
  (let [input (->> inputs (filter #(< (count %) 20)) rand-nth)]
    [:div#carouselExampleDark.carousel.carousel-dark.slide {:data-bs-ride "carousel"}
     [:div.carousel-indicators
      [:button.active {:type "button" :data-bs-target "#carouselExampleDark" :data-bs-slide-to "0" :class "" :aria-label "Slide 1"}]
      [:button {:type "button" :data-bs-target "#carouselExampleDark" :data-bs-slide-to "1" :aria-label "Slide 2" :class ""}]
      [:button {:type "button" :data-bs-target "#carouselExampleDark" :data-bs-slide-to "2" :aria-label "Slide 3" :aria-current "true"}]
      [:button {:type "button" :data-bs-target "#carouselExampleDark" :data-bs-slide-to "3" :aria-label "Slide 4" :aria-current "true"}]]
     [:div.carousel-inner
      [:div.carousel-item.active {:data-bs-interval "5000"}
       [:svg.bd-placeholder-img.bd-placeholder-img-lg.d-block.w-100
        {:width "100%" :height "250" :xmlns "http://www.w3.org/2000/svg"
         :role  "img" :aria-label "Placeholder: First slide" :preserveAspectRatio "xMidYMid slice" :focusable "false"}
        [:title "Placeholder"]
        [:rect {:width "100%" :height "100%" :fill "#f5f5f5"}]
        [:text {:x "50%" :y "30%" :fill "#aaa" :dy ".3em"} (str (halve input))]]
       [:div.carousel-caption.d-none.d-md-block
        [:h4 "Take the rucksack definition and split it in half"]
        [:p input]]]

      [:div.carousel-item {:data-bs-interval "5000"}
       [:svg.bd-placeholder-img.bd-placeholder-img-lg.d-block.w-100
        {:width "100%" :height "250" :xmlns "http://www.w3.org/2000/svg" :role "img" :aria-label "Placeholder: Second slide" :preserveAspectRatio "xMidYMid slice" :focusable "false"}
        [:title "Placeholder"]
        [:rect {:width "100%" :height "100%" :fill "#eee"}]
        [:text {:x "50%" :y "30%" :fill "#bbb" :dy ".3em"} (-> input halve common-item)]]
       [:div.carousel-caption.d-none.d-md-block
        [:h5 "Find the common item"]
        [:p (str (halve input))]]]

      [:div.carousel-item {:data-bs-interval "5000"}
       [:svg.bd-placeholder-img.bd-placeholder-img-lg.d-block.w-100
        {:width "100%" :height "250" :xmlns "http://www.w3.org/2000/svg" :role "img" :aria-label "Placeholder: Third slide" :preserveAspectRatio "xMidYMid slice" :focusable "false"}
        [:title "Placeholder"]
        [:rect {:width "100%" :height "100%" :fill "#e5e5e5"}]
        [:text {:x "50%" :y "30%" :fill "#999" :dy ".3em"} (-> input halve common-item priority)]]
       [:div.carousel-caption.d-none.d-md-block
        [:h5 "Find the item's priority - its alphabetic code"]
        [:p (-> input halve common-item)]]]

      [:div.carousel-item {:data-bs-interval "5000"}
       [:svg.bd-placeholder-img.bd-placeholder-img-lg.d-block.w-100
        {:width "100%" :height "250" :xmlns "http://www.w3.org/2000/svg" :role "img" :aria-label "Placeholder: Fourth slide" :preserveAspectRatio "xMidYMid slice" :focusable "false"}
        [:title "Placeholder"]
        [:rect {:width "100%" :height "100%" :fill "#e5e5e5"}]
        ;; Todo - part 1 vs part 2 explainer
        (if @part-1-visible?
          [:text {:x "50%" :y "30%" :fill "#999" :dy ".3em"} (str "Perform the process over all " (count inputs) " inputs and sum up = " (part-1 inputs))]
          [:text {:x "50%" :y "30%" :fill "#999" :dy ".3em"} (str "Identify common items per three elf group and sum up = " (part-2 inputs))])]
       [:div.carousel-caption.d-none.d-md-block
        [:h5 "Sum them up"]
        (if @part-1-visible?
          [:p {:style {:font-size 8}} (str (->> (map halve inputs) (map common-item) (map priority)))]
          [:p {:style {:font-size 11}} (str (->> (partition 3 inputs) (map common-item) (map priority)))])]]]
     [:button.carousel-control-prev {:type "button" :data-bs-target "#carouselExampleDark" :data-bs-slide "prev"}
      [:span.carousel-control-prev-icon {:aria-hidden "true"}]
      [:span.visually-hidden "Previous"]]
     [:button.carousel-control-next {:type "button" :data-bs-target "#carouselExampleDark" :data-bs-slide "next"}
      [:span.carousel-control-next-icon {:aria-hidden "true"}]
      [:span.visually-hidden "Next"]]]))

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
     (when (or @part-1-visible? @part-2-visible?)
       [explainer inputs])
     [:br]
     [:h5 (str "Rucksack Reorganization on " (count inputs) " rucksacks. " commentary)]
     [data-view inputs]
     [:br]
     [answers inputs]]))