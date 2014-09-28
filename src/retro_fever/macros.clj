(ns retro-fever.macros)

(defmacro game
  "Macro to wrap the creation of a game loop. This is done to enable live evaluation
of the update and render function."
  ([loop-fn update-fn render-fn ups]
     `(~loop-fn (/ 1000 ~ups) #(~update-fn) #(~render-fn %)))
  ([loop-fn update-fn render-fn]
     `(game ~loop-fn ~update-fn ~render-fn 60)))
