# Adding re-frame to the pigs kata

Add re-frame around this implementation of the pigs kata:
[https://github.com/ismaelbartolome/clojure-pig-dice-game](https://github.com/ismaelbartolome/clojure-pig-dice-game)


This branch is multiplayer using websockets
Based in this library:
https://github.com/drapanjanas/pneumatic-tubes

### Run application:

```
lein clean
lein figwheel dev
```

You can open differents browsers and start to play multiplayer.


Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
