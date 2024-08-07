(ns amaso.browser)

(defmacro $-attr [e k v]
  (when nil nil))

(defmacro $ [tag-name & attrs-and-children]
  (let [tag-name (cond
                   (string? tag-name) tag-name
                   (keyword? tag-name) (name tag-name)
                   :else (throw (ex-info (str "Invalid tag-name: " tag-name) {})))

        [attrs children] (when (seq attrs-and-children)
                           (let [first-thing (first attrs-and-children)]
                             (if (map? first-thing)
                               [first-thing (rest attrs-and-children)]
                               [nil attrs-and-children])))

        [attrs on-click] (if-let [on-click (-> attrs :on-click)]
                           [(dissoc attrs :on-click) on-click]
                           [attrs nil])

        e (gensym 'e)

        event-adds [(when on-click
                      `(when-let [on-click# ~on-click]
                         (assert (fn? on-click#))
                         (-> ~e (.addEventListener
                                 "click" on-click#))))]

        attr-adds (->> attrs
                       (map (fn [[k v]]
                              (when (and k v)
                                (let [k (if (keyword? k) (name k) (str k))
                                      v (if (keyword? v) (name v) v)]
                                  `(-> ~e (.setAttribute ~k ~v)))))))
        children-adds (->> children
                           (remove nil?)
                           (map (fn [child]
                                  (cond
                                    (or (string? child) (number? child))
                                    `(-> ~e (.appendChild (js/document.createTextNode ~child)))

                                    :else
                                    `(when-let [c# ~child]
                                       (-> ~e (append-to-element! c#)))))))


        ops (->> (concat attr-adds event-adds children-adds)
                 (remove nil?)
                 seq)
        
        form (if-not ops
               `(js/document.createElement ~tag-name)

               `(let [~e (js/document.createElement ~tag-name)]
                  ~@ops
                  ~e))
        
        ]
    form))


