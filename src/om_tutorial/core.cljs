(ns om-tutorial.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(defui Person
       static om/IQuery
       (query [this] [:db/id :person/name])
       static om/Ident
       (ident [this {:keys [db/id]}] [:db/id id])

       Object
       (render [this]
               (let [{:keys [db/id person/name]} (om/props this)]
                 (dom/li nil (str name " id: " id)))))

(def person (om/factory Person {:keyfn :db/id}))

(defui Root
       static om/IQuery
       (query [this] [{:people (om/get-query Person)}])
       Object
       (render [this]
               (let [people (-> (om/props this) :people)]
                 (let []
                   (dom/div nil
                            (dom/ul nil (map person people))
                            ;IMPORTANT: THIS IS NOT A REAL ADD...SIMULATE AN ADD to the server. It will look like it works ONCE
                            (dom/button #js {:onClick #(om/transact! this `[(app/add-person) :people (quote ~(cljs.core/first (om/get-query this)))])} "Add Person")
                            ;IMPORTANT: THIS IS NOT A REAL DELETE...SIMULATE a DELETE...ONLY WORKS IF YOU'VE DONE the Add already, since it is faking removing the one you added
                            (dom/button #js {:onClick #(om/transact! this '[(app/delete-person) :people])} "Delete Person")
                            ))
                 )
               )
       )

(defmulti read om/dispatch)
(defmethod read :people [{:keys [state] :as env} k p] {:value (mapv #(get-in @state %) (get @state k))})
(defmethod read :db/id [{:keys [state] :as env} k p] (println "db/id " env) {:value (get @state k)})

(defmulti mutate om/dispatch)
(defmethod mutate 'app/add-person [{:keys [state] :as env} k p]
  {
   :remote true
   ; Action here is an optimistic update. If you have send in "deny add" simulation mode, you'll temporarily see the change
   :action (fn []
             ;; I can play with normalized tables, or use db->tree and tree->db.
             (swap! state update-in [:db/id] assoc 3 {:db/id 3 :person/name "Joey"})
             (swap! state update-in [:people] conj [:db/id 3])
             )
   })
(defmethod mutate 'app/delete-person [{:keys [state] :as env} k {:keys [db/id]}]
  {:remote true
   :action (fn [] (swap! state update-in [:people] butlast)) ; optimistic delete...always denied by server unless "Joey"
   })

(def initial-state {:people [{:db/id 1 :person/name "Sam"} {:db/id 2 :person/name "Tammy"}]})

; These are the fake data responses I'm simulating from the server.

; PRETEND thing that the server returns if add is OK
(def pretend-added-person-state {:people [{:db/id 1 :person/name "Sam"} {:db/id 2 :person/name "Tammy"} {:db/id 3 :person/name "Joey"}]})
; PRETEND thing that the server returns if add is not OK, OR delete has run
(def pretend-deleted-person-state {:people [{:db/id 1 :person/name "Sam"} {:db/id 2 :person/name "Tammy"}]})

(def parser (om/parser {:read read :mutate mutate}))
(defonce reconciler (om/reconciler {:state  initial-state
                                    :parser parser
                                    :send   (fn [query cb]
                                              (println "REMOTE" query)
                                              (js/setTimeout
                                                (fn []
                                                  (let [is-add? (= 'app/add-person (-> query :remote ffirst))
                                                        add-allowed? true] ; change to alter simulation of server being ok with add
                                                    (if is-add?
                                                      (if add-allowed?
                                                        (let [people-read-response pretend-added-person-state] (cb people-read-response)) ; simulate add ok
                                                        (let [people-read-response pretend-deleted-person-state] (cb people-read-response))) ; simulate add denied
                                                      (let [people-read-response pretend-deleted-person-state]
                                                        (println "DELETE") ; delete
                                                        ; call the callback, which will SIMULATE what I get back from the server (e.g. the original list)
                                                        (cb people-read-response)) ; delete
                                                      ))) 1000)
                                              )}))

(om/add-root! reconciler Root (gdom/getElement "app"))

