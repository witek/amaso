(ns amaso.browser
  (:require-macros [amaso.browser]))

(defn remove-all-children! [container-element]
  (when container-element
    (-> container-element .-textContent (set! nil))))

(defn append-to-element! [container-element content]
  (when content
    (.appendChild
     container-element
     (cond
       (sequential? content)
       (let [fragment (js/document.createDocumentFragment)]
         (doseq [item content]
           (when item
             (append-to-element! fragment item)))
         fragment)
                                               
       (or (string? content) (number? content))
       (js/document.createTextNode content)

       :else content))))
