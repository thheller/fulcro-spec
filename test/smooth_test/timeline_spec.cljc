(ns smooth-test.timeline-spec
  #?(:clj
     (:require [smooth-test.provided :as p]
               [clojure.test :as t :refer (are is deftest with-test run-tests testing)]
               [smooth-test.timeline :as timeline]
               ))
  #?(:cljs (:require-macros [cljs.test :refer (are is deftest run-tests testing)]
             [smooth-test.timeline :as timeline]
             [smooth-test.provided :as p]
             ))
  #?(:cljs (:require [cljs.test :as t]
             [smooth-test.async :as a :include-macros true]
             )
     )
  #?(:clj
     (:import clojure.lang.ExceptionInfo))
  )


#?(:cljs
   (deftest time-line-macros
     (testing "Timeline macros for async processing"
       (timeline/with-timeline
         (let [detector (atom [])]
           (p/provided
             (js/setTimeout f n) =3x=> (timeline/async n (f))

             (js/setTimeout (fn [] (js/setTimeout (fn [] (swap! detector conj "LAST")) 300) (swap! detector conj "FIRST")) 100)

             (testing "nothing called until timer moves past first specified event is to occur"
               (is (= 0 (count @detector)))
               )

             (testing "after first tick only the callbacks that satisfy the"
               (timeline/tick 101)
               (is (= 1 (count @detector)))
               )

             (testing "more functions can run before next callback is called"
               (swap! detector conj "SECOND")
               (is (= 2 (count @detector)))
               )

             (testing "after all time is passed all callback timers are fired"
               (timeline/tick 401)
               (is (= 3 (count @detector)))
               (is (= "FIRST" (first @detector)))
               (is (= "SECOND" (second @detector)))
               (is (= "LAST" (last @detector)))
               )
             )
           )
         )
       )
     )
   )