(ns helper
  (:require [promesa.core :as p]))

(defn fetch
  [state url]
  (-> (p/let [result (js/fetch url)
              text (.text result)]
             (reset! state text))
      (p/catch (fn [error]
                 (js/console.log :error error)))))

