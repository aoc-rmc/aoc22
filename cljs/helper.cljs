(ns helper
  (:require [clojure.edn :as edn]
            [promesa.core :as p]))

(defn fetch
  [state url]
  (-> (p/let [text (p/-> url js/fetch .text)]
             (reset! state text))
      (p/catch (fn [error]
                 (js/console.log :error error)))))
