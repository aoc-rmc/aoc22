(ns day2
  "Props to @tylerw for this answer. This page is based on that work.
  ref: https://github.com/tylerw/advent-of-code-2022/blob/master/src/aoc2022/day02.cljc"
  (:require
    [clojure.string :as str]
    [helper]
    [reagent.core :as r]))

(def data (r/atom nil))

(helper/fetch data "data/day2")

(def part-1-visible? (r/atom false))

(def part-2-visible? (r/atom false))

(def strategy (r/atom nil))

(defn toggle-visibility
  [state-visible? _result]
  (if @state-visible?
    (reset! state-visible? false)
    (reset! state-visible? true)))

(def rock [:i.bi.bi-heptagon-fill])
(def paper [:i.bi.bi-file-earmark])
(def scissors [:i.bi.bi-scissors])

(def viz-map
  {"A" rock
   "X" rock
   "B" paper
   "Y" paper
   "C" scissors
   "Z" scissors})

(def play->point
  {"X" 1, "Y" 2, "Z" 3})

(def win 6)
(def draw 3)
(def lose 0)

(def score-p1
  {;rock
   ["A" "X"] draw
   ["A" "Y"] win
   ["A" "Z"] lose
   ;paper
   ["B" "X"] lose
   ["B" "Y"] draw
   ["B" "Z"] win
   ;scissors
   ["C" "X"] win
   ["C" "Y"] lose
   ["C" "Z"] draw
   })

(defn turn-decision-part-1
  [[_their-turn my-turn :as turn]]
  [(score-p1 turn) (play->point my-turn)])

(defn part-1-answer
  [turns]
  (->> turns
       (mapcat turn-decision-part-1)
       (reduce + 0)))

(def beat
  {"A" "Y"    ; paper(Y) beats rock(A)
   "B" "Z"    ; scissors(Z) beats paper(B)
   "C" "X"})  ; rock(X) beats scissors(C)

(def draw-with
  {"A" "X"
   "B" "Y"
   "C" "Z"})

(def lose-to
  {"A" "Z"    ; scissors(Z) loses to rock(A)
   "B" "X"    ; rock(X) loses to paper(B)
   "C" "Y"})  ; paper(Y) loses to scissors(C)

(defn turn-choice-part-2
  [[their-turn action]]
  (condp = action
    "X" ;lose
    (lose-to their-turn)
    "Y" ;draw
    (draw-with their-turn)
    "Z" ;win
    (beat their-turn)))

(defn turn-decision-part-2
  [[their-turn action :as turn]]
  (let [my-choice (turn-choice-part-2 turn)]
    (condp = action
      "X"                                                   ;lose
      [(score-p1 [their-turn (lose-to their-turn)]) (play->point (lose-to their-turn))]
      "Y"                                                   ;draw
      [(score-p1 [their-turn (draw-with their-turn)]) (play->point (draw-with their-turn))]
      "Z"                                                   ;win
      [(score-p1 [their-turn (beat their-turn)]) (play->point (beat their-turn))])))

(defn part-2-answer
  [turns]
  (->> turns
       (mapcat turn-decision-part-2)
       (reduce + 0)))
(defn answers
  [turns]
  (let [answer-1 (part-1-answer turns)                      ; 10310
        answer-2 (part-2-answer turns)]                     ; 14859
    [:div.row
     [:div.col
      [:button.btn.btn-success
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target "#part1"
        :aria-expanded "false" :aria-controls "part1"
        :on-click      (fn []
                         (toggle-visibility part-1-visible? answer-1)
                         (when part-1-visible?
                           (reset! strategy :part1)))}
       "Part 1"]]
     [:div.col
      [:button.btn.btn-danger
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target "#part2"
        :aria-expanded "false" :aria-controls "part2"
        :on-click      (fn []
                         (toggle-visibility part-2-visible? answer-2)
                         (when part-2-visible?
                           (reset! strategy :part2)))}
       "Part 2"]]
     [:div.row.p-2
      [:div.col
       [:div#part1.collapse.multi-collapse
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. What would be your total score if you played with your partial understanding of the strategy guide?"]
          [:br]
          [:p "A. " answer-1]]]]]
      [:div.col
       [:div#part2.collapse.multi-collapse
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. What would be your total score if you played with your full understanding of the strategy guide?"]
          [:br]
          [:p "A. " answer-2]]]]]]]))

(defn data-view
  [turns]
  (let [elf-rows (partition-all 100 turns)]
    [:table.table-sm
     [:tbody
      (map-indexed
        (fn [row-idx row]
          (let [font-size (if (= row-idx 0) 8 6)]
            (into [:tr]
                  (map
                    (fn [[their-turn my-turn :as turn]]
                      (let [turn-choice (if (= :part2 @strategy)
                                          (turn-choice-part-2 turn)
                                          my-turn)
                            decision (if (= :part2 @strategy)
                                       (turn-decision-part-2 turn)
                                       (turn-decision-part-1 turn))
                            result (first decision)
                            colour (condp = result
                                     win :green
                                     draw :gray
                                     lose :red)]
                        [:td {:style {:font-size font-size :color colour}}
                         [:div (viz-map their-turn) (viz-map turn-choice)]]))
                    row))))
        elf-rows)]]))

(defn content
  [day#]
  (let [turns (->> @data
                   str/split-lines
                   (map #(str/split % #" ")))]
    [:div
     [:a {:href (str "https://adventofcode.com/2022/day/" day#)}
      "Link to AOC Challenge for day " day#]
     [:br]
     [:br]
     [:div.row
      [:div.col-2 "Rock " rock]
      [:div.col-2 "Paper " paper]
      [:div.col-2 "Scissors " scissors]
      [:div.col-2 {:style {:color :green}} "Win "]
      [:div.col-2 {:style {:color :red}} "Lose "]
      [:div.col-2 {:style {:color :gray}} "Draw "]]
     [:br]
     [:h5 (str "Playing rock, paper, scissors for " (count turns) " times. The turns are...")]
     [data-view turns]
     [:br]
     [answers turns]]))
