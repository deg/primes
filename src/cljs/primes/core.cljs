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


(ns primes.core.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.data :as data]
            [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]))

(enable-console-print!)


(def app-state (atom {:title "Integer composite groups"
                      :up-to 10
                      :groups '([(1) (2 3 5 7 11 13)]
                                [(1 1) (6 10 14)]
                                [(2) (4 9)]
                                [(2 1) (12)]
                                [(3) (8)])}))


(defn group-view
  "Render the contents of one group (set) of numbers."
  [[signature numbers] owner]
  (reify
    om/IInitState
    (init-state [_]
      {:expanded false})
    om/IRenderState
    (render-state [_ {:keys [expanded]}]
      (dom/li nil
              (dom/span #js {:onClick (fn [e] (om/set-state! owner :expanded (not expanded)))}
                        (if expanded
                          "{Click to restore}"
                          "{Click to expand}"))))))

(defn group-view-wrapper2
  "Render the contents of one group (set) of numbers."
  [app-group owner]
  (let [[signature numbers] (first app-group)]
    (group-view [signature numbers] owner)))

(defn group-view-wrapper3
  "Render the contents of one group (set) of numbers."
  [app-group owner]
  (let [[signature numbers] app-group]
    (group-view [signature numbers] owner)))


(defn groups-view
  "Render all the groups"
  [app owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/div nil "Uses wrapper2, works")
               (dom/ul nil
                       (om/build group-view-wrapper2 (:groups app)))
               (dom/div nil "Uses wrapper3, fails")
               (dom/ul nil
                       (om/build group-view-wrapper3 (first (:groups app))))))))


(om/root groups-view
         app-state
         {:target (. js/document (getElementById "app"))})
