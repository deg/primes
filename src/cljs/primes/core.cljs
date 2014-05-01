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
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state (atom {:groups ()}))

(defn group-view [_ owner]
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

(defn working [all owner]
  (group-view (first all) owner))

(defn failing [primary owner]
  (group-view primary owner))

(defn groups-view
  "Render all the groups"
  [app owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               "Working case: "
               (om/build working (:groups app))
                (dom/br nil)
               "Failing case: "
               (om/build failing (first (:groups app)))))))


(om/root groups-view
         app-state
         {:target (. js/document (getElementById "app"))})
