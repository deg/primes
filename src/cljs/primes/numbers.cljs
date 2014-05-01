;; Copyright (c) 2014 David Goldfarb. All rights reserved.
;;; Contact info: deg@degel.com
;;;
;;; The use and distribution terms for this software are covered by the Eclipse
;;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
;;; be found in the file epl-v10.html at the root of this distribution.
;;; By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.
;;;
;;; You must not remove this notice, or any other, from this software.


(ns degel.primes.numbers
  ;; [TODO] Until reader version collision fixed.
  #_
  (:require [degel.cljutil.utils :as utils]))


;; Taken from http://clj-me.cgrand.net/index.php?s=Primes. There, called lazy-primes3
(defn primes []
  (letfn [(enqueue [sieve n step]
            (let [m (+ n step)]
              (if (sieve m)
                (recur sieve m step)
                (assoc sieve m step))))
          (next-sieve [sieve candidate]
            (if-let [step (sieve candidate)]
              (-> sieve
                  (dissoc candidate)
                  (enqueue candidate step))
              (enqueue sieve candidate (+ candidate candidate))))
          (next-primes [sieve candidate]
            (if (sieve candidate)
              (recur (next-sieve sieve candidate) (+ candidate 2))
              (cons candidate
                    (lazy-seq (next-primes (next-sieve sieve candidate)
                                           (+ candidate 2))))))]
    (cons 2 (lazy-seq (next-primes {} 3)))))


;; Based on code from
;; http://angeleah.com/blog/2012/11/07/finding-prime-factors-using-clojure.html
(defn factors
  ([n] (factors n 2))
  ([n candidate]
     (cond (< n candidate)           []
           (zero? (rem n candidate)) (cons candidate (factors (/ n candidate) candidate))
           :else                     (recur n (+ 1 candidate)))))



;;;; ================================================================

;;; [TODO] This is copied from degel-utils until I get the reader
;;; dependency fixed.  Update changes from here back to utils too.

(defn group-results
  "Group a set of results by clustering and counting adjacent results with matching key.
  That is (group-results identity [:a :a :a :b :b :a :a :a :b :b :b :b]) =>
          ([:a 3] [:b 2] [:a 3] [:b 4])"
  [keyfn results]
  (map (juxt first count)
       (partition-by identity
                     (map keyfn results))))


(defn group-values-by-keys
  "Organize a sequence of items, each of which contains a key and a value.
   All the values with a common key are grouped together into a sequence that is
   the value of that key in the returned map.

   Perhaps best explained by an example:

   ex: `(group-values-by-keys [[:o 1] [:e 2] [:o 3] [:e 4] [:o 5] [:e 6]] first second)`

   -> ` {:e (2 4 6), :o (1 3 5)}`"
  [l f-key f-val]
  (into {} (map (juxt (comp f-key first) #(map f-val %))
                (partition-by f-key (sort-by f-key l)))))

;;;; ================================================================

;;; [TOOD] Functions to move into degel-utils once that stabilizes

(def sqrt (.-sqrt js/Math))
(def ceil (.-ceil js/Math))

(defn vector-magnitude [vec]
  (sqrt (reduce + (map #(* % %) vec))))

;;; ================================================================


(defn signature
  "Generate the signature (group 'name') for one number."[n]
  (let [by-prime (group-results identity (factors n))]
    (sort > (map second by-prime))))

(defn signature-and-value-nocache [n]
  [(signature n) n])

(def signature-and-value (memoize signature-and-value-nocache))

(defn signatures [thru-n]
  (map signature-and-value (range 2 thru-n)))

(defn sort-groups
  "Sort groups based on distance from origin in 'primes space'"
  [number-groups]
  (sort-by (comp vector-magnitude first) < number-groups))

(defn group-signatures [thru-n]
  (sort-groups (group-values-by-keys (signatures thru-n) first second)))
