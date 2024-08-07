(ns amaso.core
  (:require
   [amaso.browser :as browser :refer [$]]
   [amaso.commands :as commands]))

;; (defonce STATE (atom {::state ::state}))

;; (defn state? [thing]
;;   (-> thing ::state (= ::state)))

;; (defn transact [f]
;;   (assert (fn? f))
;;   (swap! STATE
;;          (fn [state]
;;            (js/console.log "[transact]" f)
;;            (let [new-state (f state)
;;                  _ (assert (state? new-state))]
;;              new-state))))

;; (comment
;;   (transact (fn [state]
;;               :boo))
;;   )

(defn execute-command! [command-key]
  (js/console.log "execute-command!" (str command-key))
  (let [command (commands/command command-key)
        f (-> command :fn)
        _ (assert (fn? f))]
    (f)))

(def e-id--desktop "amaso-desktop")
(def e-id--stack "amaso-stack")

(defn wrap-widget-for-stack [widget {:keys [num]}]
  ($ :div
    {:class "widget-wrapper"}
    ($ :div
      {:class "widget-wrapper-header"}
      (str "#" num
           " "
           (-> (js/Date.) .toLocaleString)))
    widget))

(defn push-widget [widget]
  (let [stack (js/document.getElementById e-id--stack)
        _ (assert stack)
        wrapper (wrap-widget-for-stack
                 widget
                 {:num (-> stack .-childNodes .-length)})]
    (-> stack
        (.insertBefore
         wrapper
         (-> stack .-firstChild)))

    #_(-> wrapper (.scrollIntoView (clj->js
                                    {:behavior "smooth"})))

    (js/window.scrollTo (clj->js {:top 0
                                  :left 0
                                  :behavior "smooth"}))

    ))

(comment
  (push-widget ($ :div "hello x"))

  )

(defn create-ui-stack []
  ($ :div
    {:id e-id--stack
     :class "amaso-ui-stack"}

    ;; dummy element so we can always insert before the first child
    ($ :div {})

    ))

(def e-id--toolbar "amaso-toolbar")

(defn create-toolbar []
  ($ :div
    {:id e-id--toolbar}

    ($ :button
      {:on-click #(execute-command! :amaso/list-commands)}
      "#")

    ($ :button
      "M")
    ))

(defn create-desktop []
  ($ :div
    {:id e-id--desktop}
    (create-ui-stack)
    (create-toolbar)
    ))

(defn ActionItem [{:keys [text text-2 description on-click]}]
  ($ :div
    {:class "ActionItem"
     :on-click on-click}

    ($ :div
      ($ :div
        {:class "ActionItem-text"}
        text)
      (when text-2
        ($ :div
          {:class "ActionItem-text-2"}
          text)))

    ($ :div
      {:class "ActionItem-description"}
      description)
    ))

(defn list-commands! []
  (let [commands (commands/commands)]
    (push-widget
     ($ :div
       "All commands: "
       (for [command commands]
         (ActionItem
          {:on-click #(execute-command! (-> command :key))
           :text (-> command :key name)
           ;; :text-2 (-> command :label)
           :description (-> command :description)})
         )))))

(comment (list-commands!))

(defn restart-application! []
  (js/window.location.reload))

(defn register-default-commands! []

  (commands/register-command!
   {:key :amaso/list-commands
    :description "List all commands"
    :fn #'list-commands!})

  (commands/register-command!
   {:key :amaso/restart
    :description "Restart application"
    :fn #'restart-application!})

  )

(defn install-desktop! [container-id]
  (assert (string? container-id))
  (let [container (js/document.getElementById container-id)
        _ (assert container)
        _ (browser/remove-all-children! container)
        _ (-> container (.appendChild (create-desktop)))

        _ (register-default-commands!)
        ]))

(comment
  (install-desktop! "root")

  (js/console.log
   ($ :div
     {:id "some-id"
      :class "boo"}
     "child text"
     ["a" nil "b"]
     ($ :span)
     ($ :button
       {:on-click #(js/alert "boo")})))

  (macroexpand
   '($ :div
      {:id "some-id"
       :class "boo"}
      "child text"
      ["a" nil "b"]
      ($ :span)
      ($ :button
        {:on-click #(js/alert "boo")})))

  (macroexpand '($ :button
                  {:on-click #(js/alert "boo")}
                  
                  ))

  (js/console.log
   ($ :div
     ($ :span)
     ))

  (macroexpand '($ :div
                  {:id "some-id"}
                  "child text"
                  ($ :span)))
  
  (macroexpand '($ :div
                  ($ :span1)
                  ($ :span2)))
  
  
  )


