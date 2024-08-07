(ns amaso.commands)

(defonce COMMANDS (atom {}))

(defn commands []
  (->> @COMMANDS vals))

(defn command [command-key]
  (assert (qualified-keyword? command-key))
  (-> @COMMANDS
      (get command-key)))

(defn register-command! [command]
  (assert (map? command))
  (let [key (-> command :key)
        _ (assert (qualified-keyword? key))
        _ (assert (fn? (-> command :fn)))
        label (or (-> command :label)
                  (name key))
        command (-> command
                    (assoc :label label))]
    (swap! COMMANDS assoc key command)))
