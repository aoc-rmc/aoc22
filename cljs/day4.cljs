(ns day4
  "Props to @tylerw for this answer. This page is based on that work.
  ref: https://github.com/tylerw/advent-of-code-2022/blob/master/src/aoc2022/day04.cljc"
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [helper]
    [reagent.core :as r]))

(def data (r/atom nil))

(helper/fetch data "data/day4")

(def part-1-visible? (r/atom false))

(def part-2-visible? (r/atom false))

(defn parse [line]
  (->> line (re-seq #"\d+") (map js/parseInt)))

(defn fully? [[A B X Y]]
  (or (<= A X Y B) (<= X A B Y)))

(defn partially? [[A B X Y]]
  (or (<= X A Y) (<= A X B)))

(defn overlaps [f input]
  (count (filter true? (map f input))))

(defn part-1 [input]
  (->> input
       (map parse)
       (overlaps fully?)))

(defn part-2 [input]
  (->> input
       (map parse)
       (overlaps partially?)))

(defn answers
  [inputs]
  (let [button1-id (str (gensym "day4-"))
        button2-id (str (gensym "day4-"))
        answer-1 (part-1 inputs)
        answer-2 (part-2 inputs)]
    [:div.row
     [:div.col
      [:button.btn.btn-success
       {:type           "button" :data-bs-toggle "collapse"
        :data-bs-target (str "#" button1-id)
        :aria-expanded  "false" :aria-controls "part1"
        :on-click       #(helper/toggle-visibility part-1-visible?)}
       "Part 1"]]
     [:div.col
      [:button.btn.btn-danger
       {:type           "button" :data-bs-toggle "collapse"
        :data-bs-target (str "#" button2-id)
        :aria-expanded  "false" :aria-controls "part2"
        :on-click       #(helper/toggle-visibility part-2-visible?)}
       "Part 2"]]
     [:div.row.p-2
      [:div.col
       [:div.collapse.multi-collapse {:id button1-id}
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. In how many assignment pairs does one range fully contain the other?"]
          [:br]
          [:p "A. " answer-1]]]]]
      [:div.col
       [:div.collapse.multi-collapse {:id button2-id}
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. In how many assignment pairs do the ranges overlap?"]
          [:br]
          [:p "A. " answer-2]]]]]]]))

(defn data-view
  [data]
  (let [[view-data column-count] (cond
                                   @part-1-visible? [(keep #(when (fully? %) %) data) 15]
                                   @part-2-visible? [(keep #(when (partially? %) %) data) 15]
                                   :else [data 15])
        rows (partition-all column-count
                            (concat (take 60 view-data)
                                    (take column-count (repeat "..."))))]
    [:table.table-sm
     [:tbody
      (map-indexed
        (fn [row-idx row]
          (let [font-size (if (= row-idx 0) 12 10)]
            (into [:tr]
                  (map
                    (fn [item]
                      [:td {:style {:font-size font-size}}
                       item])
                    row))))
        rows)]]))

(defn explainer
  [inputs]
  (let [input (->> (map parse inputs)
                   (keep #(when (partially? %) %))
                   (rand-nth))]
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
        [:rect {:width "100%" :height "100%" :fill "#f5f5f5"}]
        [:text {:x "50%" :y "30%" :fill "#aaa" :dy ".3em"} (str (vec input))]]
       [:div.carousel-caption.d-none.d-md-block
        [:h4 "Parse into an array the two ranges the elves are tidying"]
        [:p input]]]

      [:div.carousel-item {:data-bs-interval "5000"}
       [:svg.bd-placeholder-img.bd-placeholder-img-lg.d-block.w-100
        {:width "100%" :height "250" :xmlns "http://www.w3.org/2000/svg" :role "img" :aria-label "Placeholder: Second slide" :preserveAspectRatio "xMidYMid slice" :focusable "false"}
        [:rect {:width "100%" :height "100%" :fill "#eee"}]
        [:text {:x "50%" :y "30%" :fill "#bbb" :dy ".3em"} (str (fully? input))]]
       (let [[A B X Y] (vec input)]
         [:div.carousel-caption.d-none.d-md-block
          [:h5 (str "Let elements be [A " A " B " B " X " X " Y " Y "].
          Overlap check: (<= " A " " X " " Y " " B
                    ") or (<= " X " " A " " B " " Y ")")]
          [:p (str (vec input))]])]

      [:div.carousel-item {:data-bs-interval "5000"}
       [:svg.bd-placeholder-img.bd-placeholder-img-lg.d-block.w-100
        {:width "100%" :height "250" :xmlns "http://www.w3.org/2000/svg" :role "img" :aria-label "Placeholder: Third slide" :preserveAspectRatio "xMidYMid slice" :focusable "false"}
        [:rect {:width "100%" :height "100%" :fill "#e5e5e5"}]
        (if @part-1-visible?
          [:text {:x "50%" :y "30%" :fill "#999" :dy ".3em"} (count (filter true? (map fully? (map parse inputs))))]
          [:text {:x "50%" :y "30%" :fill "#999" :dy ".3em"} (count (filter true? (map partially? (map parse inputs))))])]
       (if @part-1-visible?
         [:div.carousel-caption.d-none.d-md-block
          [:h5 "Keep the ranges which fully overlap"]
          [:p (str (fully? input))]]
         [:div.carousel-caption.d-none.d-md-block
          [:h5 "Keep the ranges which partially overlap"]
          [:p (str (partially? input))]])]

      [:div.carousel-item {:data-bs-interval "5000"}
       [:svg.bd-placeholder-img.bd-placeholder-img-lg.d-block.w-100
        {:width "100%" :height "250" :xmlns "http://www.w3.org/2000/svg" :role "img" :aria-label "Placeholder: Fourth slide" :preserveAspectRatio "xMidYMid slice" :focusable "false"}
        [:rect {:width "100%" :height "100%" :fill "#e5e5e5"}]
        (if @part-1-visible?
          [:text {:x "50%" :y "30%" :fill "#999" :dy ".3em"} (str "Sum up " (count (filter true? (map fully? inputs))) " inputs = " (part-1 inputs))]
          [:text {:x "50%" :y "30%" :fill "#999" :dy ".3em"} (str "Sum up " (count (filter true? (map partially? inputs))) " inputs = " (part-2 inputs))])]
       [:div.carousel-caption.d-none.d-md-block
        [:h5 "Sum them up"]
        (if @part-1-visible?
          [:p (count (filter true? (map fully? inputs)))]
          [:p (count (filter true? (map partially? inputs)))])]]]
     [:button.carousel-control-prev {:type "button" :data-bs-target "#carouselExampleDark" :data-bs-slide "prev"}
      [:span.carousel-control-prev-icon {:aria-hidden "true"}]
      [:span.visually-hidden "Previous"]]
     [:button.carousel-control-next {:type "button" :data-bs-target "#carouselExampleDark" :data-bs-slide "next"}
      [:span.carousel-control-next-icon {:aria-hidden "true"}]
      [:span.visually-hidden "Next"]]]))

(defn content
  [day#]
  (let [inputs (str/split-lines @data)
        commentary (cond
                     @part-1-visible? (str (part-1 inputs) " fully overlapping areas.")
                     @part-2-visible? (str (part-2 inputs) " partially overlapping areas.")
                     :else "Range data for each area...")]
    [:div
     [:a {:href (str "https://adventofcode.com/2022/day/" day#)}
      "Link to AOC Challenge for day " day#]
     [:br]
     [:br]
     (when (and inputs (or @part-1-visible? @part-2-visible?))
       [explainer inputs])
     [:br]
     [:h5 (str "Camp cleanup over " (count inputs) " areas. " commentary)]
     [data-view inputs]
     [:br]
     [answers inputs]]))