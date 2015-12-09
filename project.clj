(defproject om-tutorial "0.1.0-SNAPSHOT"
  :description "A Tutorial for Om 1.0.0 (next)"
  :dependencies [[org.clojure/clojure "1.7.0" :scope "provided"]
                 [org.clojure/clojurescript "1.7.170" :scope "provided"]
                 [devcards "0.2.1" :exclusions [org.omcljs/om]]
                 [datascript "0.13.3"]
                 [org.omcljs/om "1.0.0-alpha25"]
                 [figwheel-sidecar "0.5.0-2" :scope "test"]
                 [cljsjs/codemirror "5.8.0-0"]
                 ]

  :source-paths ["src/main" "src/cards" "src/tutorial"]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]
            [com.cemerick/piggieback "0.2.1"]
            [cider/cider-nrepl "0.10.0"]
            [refactor-nrepl "1.1.0"]]

  :clean-targets ^{:protect false} ["resources/public/js" "resources/public/cards" "resources/public/tutorial" "target"]

  :figwheel {:build-ids   ["dev" "cards" "tutorial"]
             :server-port 3450}

  :cljsbuild {
              :builds
              [
               {:id           "dev"
                :figwheel     true
                :source-paths ["src/main"]
                :dependencies [[org.clojure/tools.nrepl "0.2.12"]
                               [com.cemerick/piggieback "0.2.1"]
                               ]
                :compiler     {:main                 om-tutorial.core
                               :asset-path           "js"
                               :output-to            "resources/public/js/main.js"
                               :output-dir           "resources/public/js"
                               :recompile-dependents true
                               :parallel-build       true
                               :verbose              false}}
               {:id           "cards"
                :figwheel     {:devcards true}
                :source-paths ["src/main" "src/cards"]
                :dependencies [[org.clojure/tools.nrepl "0.2.12"]
                               [com.cemerick/piggieback "0.2.1"]
                               ]
                :compiler     {
                               :main                 om-tutorial.cards
                               :source-map-timestamp true
                               :asset-path           "cards"
                               :output-to            "resources/public/cards/cards.js"
                               :output-dir           "resources/public/cards"
                               :recompile-dependents true
                               :parallel-build       true
                               :verbose              false}}
               {:id           "tutorial"
                :figwheel     {:devcards true}
                :source-paths ["src/main" "src/tutorial"]
                :dependencies [[org.clojure/tools.nrepl "0.2.12"]
                               [com.cemerick/piggieback "0.2.1"]
                               ]
                :compiler     {
                               :main                 om-tutorial.tutorial
                               :source-map-timestamp true
                               :asset-path           "tutorial"
                               :output-to            "resources/public/tutorial/tutorial.js"
                               :output-dir           "resources/public/tutorial"
                               :parallel-build       true
                               :recompile-dependents true
                               :verbose              false
                               :foreign-libs         [{:provides ["cljsjs.codemirror.addons.closebrackets"]
                                                       :requires ["cljsjs.codemirror"]
                                                       :file     "resources/public/codemirror/closebrackets-min.js"}
                                                      {:provides ["cljsjs.codemirror.addons.matchbrackets"]
                                                       :requires ["cljsjs.codemirror"]
                                                       :file     "resources/public/codemirror/matchbrackets-min.js"}]}}
               {:id           "pages"
                :source-paths ["src/main" "src/tutorial" "src/prod"]
                :compiler     {
                               :main                 core
                               :devcards             true
                               :asset-path           "pages"
                               :output-to            "resources/public/pages/tutorial.js"
                               :output-dir           "resources/public/pages"
                               :parallel-build       false
                               :verbose              true
                               :optimizations        :advanced
                               :foreign-libs         [{:provides ["cljsjs.codemirror.addons.closebrackets"]
                                                       :requires ["cljsjs.codemirror"]
                                                       :file     "resources/public/codemirror/closebrackets-min.js"}
                                                      {:provides ["cljsjs.codemirror.addons.matchbrackets"]
                                                       :requires ["cljsjs.codemirror"]
                                                       :file     "resources/public/codemirror/matchbrackets-min.js"}]}}]}

  :profiles {
             :dev {:source-paths ["src/dev"]
                   :repl-options {:init-ns user
                                  :port    7001}}
             :tutorial {:source-paths ["src/main" "src/tutorial"]
                        :dependencies [[org.clojure/tools.nrepl "0.2.12"]
                                       [com.cemerick/piggieback "0.2.1"]]}})
