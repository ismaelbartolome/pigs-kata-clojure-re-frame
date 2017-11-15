(defproject poki "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.2"]
                 [compojure "1.5.2"]
                 [ring "1.5.1"]
                 [pneumatic-tubes "0.2.0"]]




  :plugins
    [
     [lein-cljsbuild "1.1.5"]
     [lein-figwheel "0.5.10"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/cljc" "src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler poki.random-service/handler}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.4"]
                   [compojure "1.1.8"]
                   [day8.re-frame/http-fx "0.1.4"]
                   [javax.servlet/servlet-api "2.5"]
                   [ring/ring-mock "0.3.0"]]


    :plugins      [
                   [lein-figwheel "0.5.13"]
                   [lein-auto "0.1.3"]]}}


  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs" "src/cljc"]
     :figwheel     {:on-jsload "poki.core/mount-root"}
     :compiler     {:main                 poki.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}


    {:id           "min"
     :source-paths ["src/cljs" "src/cljc"]
     :compiler     {:main            poki.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})





