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
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [degel.primes.numbers :as nums]))

(enable-console-print!)

(def app-state (atom {:title "Integer sets"
                      :integer-sets (nums/group-signatures 1000)}))

(om/root
  (fn [app owner]
    (apply dom/ul nil
           (map #(dom/li nil (str %))
                (:integer-sets app))))
  app-state
  {:target (. js/document (getElementById "app"))})
