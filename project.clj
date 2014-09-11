(defproject liberator-util "0.1.0-SNAPSHOT"
  :description "A library to coordinate custom rendering in liberator."
  :url "https://github.com/aperiodic/liberator-util"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [cheshire "5.3.1"]
                 [liberator "0.12.1"]]
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]]}})
