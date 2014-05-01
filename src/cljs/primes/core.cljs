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

;;; This branch exists only to identify a bug, either in this code or the om library.

(ns primes.core.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)


(def app-state (atom {:groups '([1 2])}))


(defn group-view [[signature numbers] owner]
  (reify
    om/IInitState
    (init-state [_]
      {:expanded false})
    om/IRenderState
    (render-state [_ {:keys [expanded]}]
      (dom/span #js {:onClick (fn [e] (om/set-state! owner :expanded (not expanded)))}
                (if expanded
                  "{Click to restore}"
                  "{Click to expand}")))))

(defn group-view-wrapper2 [app-group owner]
  (let [[signature numbers] (first app-group)]
    (group-view [signature numbers] owner)))

(defn group-view-wrapper3 [app-group owner]
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
