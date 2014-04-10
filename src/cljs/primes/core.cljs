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
            [cljs.core.async :refer [put! chan <!]]
            [degel.primes.numbers :as nums]))

(enable-console-print!)

(def app-state (atom {:title "Integer composite groups"
                      :up-to 10}))

(defn group-view [[signature numbers] owner]
  (reify
    om/IRender
    (render [this]
      (dom/li nil
              (dom/b nil (str/join ", " signature))
              ": "
              (dom/small nil (dom/i nil (str/join ", " numbers)))))))

(defn add-more [app]
  (om/transact! app :up-to (partial + 100)))

(defn groups-view [app owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/h2 nil (:title app))
               (dom/button #js {:onClick #(add-more app)} "show more")
               (apply dom/ul nil
                      (om/build-all group-view (nums/group-signatures (:up-to app))))))))

(om/root groups-view
         app-state
         {:target (. js/document (getElementById "app"))})
