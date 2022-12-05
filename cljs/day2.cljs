(ns day2
  "Props to @tylerw for this answer. This page is based on that work.
  ref: https://github.com/tylerw/advent-of-code-2022/blob/master/src/aoc2022/day02.cljc"
  (:require
    [clojure.edn :as edn]
    [clojure.string :as str]
    [helper]
    [reagent.core :as r]))

(def data (r/atom nil))

(helper/fetch data "data/day2")

(def part-1-visible? (r/atom false))

(def part-2-visible? (r/atom false))

(def strategy (r/atom :none))

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
  [turn [(score-p1 turn) (play->point my-turn)]])

(defn part-1-answer
  [turns]
  (->> turns
       (mapcat (fn [turn]
                 (last (turn-decision-part-1 turn))))
       (reduce + 0)))

(def beat
  {"A" "Y"                                                  ; paper(Y) beats rock(A)
   "B" "Z"                                                  ; scissors(Z) beats paper(B)
   "C" "X"})                                                ; rock(X) beats scissors(C)

(def draw-with
  {"A" "X"
   "B" "Y"
   "C" "Z"})

(def lose-to
  {"A" "Z"                                                  ; scissors(Z) loses to rock(A)
   "B" "X"                                                  ; rock(X) loses to paper(B)
   "C" "Y"})                                                ; paper(Y) loses to scissors(C)

(defn turn-choice-part-2
  [[their-turn action]]
  (condp = action
    "X"                                                     ;lose
    (lose-to their-turn)
    "Y"                                                     ;draw
    (draw-with their-turn)
    "Z"                                                     ;win
    (beat their-turn)))

(defn turn-decision-part-2
  [[their-turn action :as turn]]
  [turn
   (condp = action
     "X"                                                    ;lose
     [(score-p1 [their-turn (lose-to their-turn)]) (play->point (lose-to their-turn))]
     "Y"                                                    ;draw
     [(score-p1 [their-turn (draw-with their-turn)]) (play->point (draw-with their-turn))]
     "Z"                                                    ;win
     [(score-p1 [their-turn (beat their-turn)]) (play->point (beat their-turn))])])

(defn part-2-answer
  [turns]
  (->> turns
       (mapcat (fn [turn]
                 (last (turn-decision-part-2 turn))))
       (reduce + 0)))

(defn answers
  [turns]
  (let [button1-id (str (gensym "day2-"))
        button2-id (str (gensym "day2-"))
        answer-1   (part-1-answer turns)                    ; 10310
        answer-2   (part-2-answer turns)]                   ; 14859
    [:div.row
     [:div.col
      [:button.btn.btn-success
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target (str "#" button1-id)
        :aria-expanded "false" :aria-controls button1-id
        :on-click      (fn [] (if (helper/toggle-visibility part-1-visible?)
                                (reset! strategy :part1)
                                (reset! strategy :none)))}
       "Part 1"]]
     [:div.col
      [:button.btn.btn-danger
       {:type          "button" :data-bs-toggle "collapse" :data-bs-target (str "#" button2-id)
        :aria-expanded "false" :aria-controls button2-id
        :on-click      (fn [] (if (helper/toggle-visibility part-2-visible?)
                                (reset! strategy :part2)
                                (reset! strategy :none)))}
       "Part 2"]]
     [:div.row.p-2
      [:div.col
       [:div.collapse.multi-collapse {:id button1-id}
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. What would be your total score if you played with your partial understanding of the strategy guide?"]
          [:br]
          [:p "A. " answer-1]]]]]
      [:div.col
       [:div.collapse.multi-collapse {:id button2-id}
        [:div.card
         [:div.card-body
          [:h5.card-title "Q. What would be your total score if you played with your full understanding of the strategy guide?"]
          [:br]
          [:p "A. " answer-2]]]]]]]))

(defn data-view
  [turns]
  (let [rows (partition-all 100 turns)]
    [:table.table-sm
     [:tbody
      (map-indexed
        (fn [row-idx row]
          (let [font-size (if (= row-idx 0) 8 6)]
            (into [:tr]
                  (map
                    (fn [[turn result-vec]]
                      (let [[their-turn my-turn] turn
                            [result _choice-worth] result-vec
                            colour (condp = result
                                     win :green
                                     draw :gray
                                     lose :red)]
                        [:td {:style {:font-size font-size :color colour}}
                         [:div (viz-map their-turn) (viz-map my-turn)]]))
                    row))))
        rows)]]))

(defn sort-by-strategy
  [turns game-strategy]
  (condp = game-strategy
    :part1 (->> turns
                (map turn-decision-part-1)
                (sort-by (juxt last first))
                (reverse))
    :part2 (->> turns
                (map turn-decision-part-2)
                (sort-by (juxt last first))
                (reverse))
    :none (->> turns
               (map turn-decision-part-1))))

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
     [data-view (sort-by-strategy turns @strategy)]
     [:br]
     [answers turns]]))
