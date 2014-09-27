(ns retro-fever.macros)

(defmacro game
  ([loop-fn update-fn render-fn ups]
     `(~loop-fn (/ 1000 ~ups) #(~update-fn) #(~render-fn)))
  ([loop-fn update-fn render-fn]
     `(game ~loop-fn ~update-fn ~render-fn 60)))
