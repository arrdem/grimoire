(ns grimoire.web.service
  (:require [compojure.handler :as handler]
            [ring.middleware.session :as session]
            [grimoire.web.routes :refer [app]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as json]))

(defonce service (atom nil))

(defn start-web-server! [& [port?]]
  (println "starting!")
  (let [cfg {:port (or port? 3000)
             :join? false}]
    (reset! service
            (-> app
              handler/site
              session/wrap-session
              json/wrap-json-response
              (jetty/run-jetty cfg)))))

(defn stop-web-server! []
  (when-let [service* @service]
    (.stop service*)
    (reset! service nil)))

(defn restart-web-server! [& [port?]]
  (require 'grimoire.web.routes
           'grimoire.web.service
           :reload)
  (stop-web-server!)
  (start-web-server! port?))

(defn -main [& [port?]]
  (start-web-server!
   (if (string? port?)
     (Long. port?)
     3000)))
