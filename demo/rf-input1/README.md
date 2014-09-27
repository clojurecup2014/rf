# Retro Fever demo - Input 1

This demo shows how the input module works.

# Usage

Start by compiling the ClojureScript into Javascript:

    lein cljsbuild auto

and then open `resources/index.html` and while watching the framed
area (a pre-tag) press different keys on the keyboard. Make sure the
window have focus before pressing :)
The contents of the atom containing which keyboard keys are currently
being hold will be show here.