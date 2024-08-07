(ns amaso.demo-app
  (:require
   [amaso.browser :refer [$]]
   [amaso.core :as amaso]))

(defn init []
  (amaso/install-desktop! "root")
  (amaso/push-widget ($ :div "Hello"))
  )

(comment
  (init)
  )
