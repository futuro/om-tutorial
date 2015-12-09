(ns user
  (:require
   [figwheel-sidecar.repl-api :as ra]
   [cemerick.piggieback]
    ))

(def figwheel-config
  {:figwheel-options {:server-port 3450
                      :nrepl-port 7888
                      :nrepl-middleware ["cider.nrepl/cider-middleware"
                                         "refactor-nrepl.middleware/wrap-refactor"
                                         "cemerick.piggieback/wrap-cljs-repl"]}
   ;; builds to focus on
   :build-ids        [ "tutorial" ]
   ;; load build configs from project file
   :all-builds       (figwheel-sidecar.config/get-project-builds)
   })


(defn start-dev
  "Start Figwheel and fw repl. You should be running this namespace from PLAIN clojure.main NOT nREPL!

  nREPL support can be had (for server-side code) in parallel, but I've not finished adding it yet (since
  there is no server code yet).
  "
  []
  (ra/start-figwheel! figwheel-config)
  (ra/cljs-repl))

(start-dev)
