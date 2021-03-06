;;; Copyright (c) 2014 David Goldfarb. All rights reserved.
;;; Contact info: deg@degel.com
;;;
;;; The use and distribution terms for this software are covered by the Eclipse
;;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
;;; be found in the file epl-v10.html at the root of this distribution.
;;; By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.
;;;
;;; You must not remove this notice, or any other, from this software.


;;; Toy program to play with the om library and reactive ClojureScript
;;; programming.
;;;
;;; This is playing with an idea I had the other day of a new way to
;;; visualize the natural numbers. Imagine that each number is
;;; expressed as a vector in a multi-dimensional space where each
;;; dimension corresponds with a prime number and the number's
;;; magnitude in each dimension how many times that prime appears in
;;; the numbers's prime factorization.
;;;
;;; So, 1 is at the origin, and higher powers of primes are further
;;; aways from the origin. Expressed as vectors in this multi-dimensional
;;; space, some small numbers look like:
;;;
;;;   2 - [1, 0, 0, 0, 0, ...]
;;;   3 - [0, 1, 0, 0, 0, ...]
;;;   4 - [2, 0, 0, 0, 0, ...]
;;;   5 - [0, 0, 1, 0, 0, ...]
;;;   6 - [1, 1, 0, 0, 0, ...]
;;;   8 - [3, 0, 0, 0, 0, ...]
;;;  10 - [1, 0, 1, 0, 0, ...]
;;;  30 - [1, 1, 1, 0, 0, ...]
;;;  36 - [2, 2, 0, 0, 0, ...]
;;; 360 - [3, 2, 1, 0, 0, ...]
;;;
;;;
;;; This is possibly interesting, but pretty much impossible to
;;; visualize in any useful way. The number of dimensions just grows
;;; way too fast to draw on two-dimensional paper; certainly way too
;;; fast for my limited artistic abilities.
;;;
;;; But, let's look at things a different way. For each number, let's
;;; look at each of the dimensions that have a non-zero magnitude
;;; (that is, each of the prime factors), ignore order or value of the
;;; primes, and use these "signatures" as buckets to group all the
;;; natural numbers into distinct sets.
;;;
;;; So, prime numbers are in the set {1}; squares of primes are in
;;; {2}; products of two distint primes are {1, 1}; etc.
;;;
;;; For clarity, let's look again at the numbers above:
;;;
;;;   2 - {1}
;;;   3 - {1}
;;;   4 - {2}
;;;   5 - {1}
;;;   6 - {1, 1}
;;;   8 - {3}
;;;  10 - {1, 1}
;;;  30 - {1, 1, 1}
;;;  36 - {2, 2}
;;; 360 - {3, 2, 1}
;;;
;;; Now, an interesting question, and one that is relatively easy to
;;; visualize, is how fast each of these sets grow. We know that the
;;; number of primes less than N is approximately N/log(N), so that
;;; gives us the growth rate of set {1}. But, I know very little about
;;; the properites of the other sets. So, the goal of this exercise it
;;; to create a web page with some useful visualizations.


(ns degel.primes.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.data :as data]
            [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [degel.primes.numbers :as nums]))

(enable-console-print!)


(def app-state (atom {:title "Integer composite groups"
                      :up-to 10
                      :groups (nums/group-signatures 10)}))


(defn title [app]
  (om/component (dom/h2 #js {:className "title"} (:title app))))


(defn add-more
  "Extend the range of numbers we are looking at"
  [app]
  (om/transact! app :up-to #(nums/ceil (* % 1.5)))
  (om/update! app :groups (nums/group-signatures (:up-to @app))))


(defn show-more [app]
  (om/component
   (dom/div #js {:className "show-more"}
            (dom/span #js {:className "guidance"} "Up to: " (:up-to app) "  ")
            (dom/button #js {:className "standalone-button" :onClick #(add-more app)}
                        "show more"))))


(defn show-graph [app owner]
  "Render the visualization graph"
  (reify
    om/IInitState
    (init-state [_]
      {:graphs nil})
    om/IRenderState
    (render-state [_ _]
      (dom/div #js {}
               "before"
               (dom/div #js {:id "graphbert" :className "jxgbox"
                                      :style #js {:width "400px" :height "400px"}})
               "after"))
    om/IDidMount
    (did-mount [graph-div]
      (println "hello world")
      (println (.-initBoard (.-JSXGraph js/JXG)))
      (println "Do you like my hat")
      )))

(defn group-view
  "Render the contents of one group (set) of numbers."
  [[signature numbers] owner]
  (reify
    om/IInitState
    (init-state [_]
      {:expanded false})
    om/IRenderState
    (render-state [_ {:keys [expanded]}]
      (dom/li #js {:className "group"}
              (dom/span #js {:className "signature"} (str/join ", " signature))
              "["
              (dom/span
               #js {:className "magnitude"}
               (nums/vector-magnitude signature))
              "]"
              (dom/br nil)
              (count numbers) ": "
              (dom/span
               #js {:onClick (fn [e] (om/set-state! owner :expanded (not expanded)))}
               (dom/span
                #js {:className "numbers"}
                (if (or expanded (< (count numbers) 5))
                  (str/join ", " numbers)
                  "{Click to expand}")))))))

(defn groups-view
  "Render all the groups"
  [app owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (om/build title app)
               (om/build show-graph app)
               (om/build show-more app)
               (apply dom/ul nil
                      (om/build-all group-view (:groups app)))))))


(om/root groups-view
         app-state
         {:target (. js/document (getElementById "app"))})
