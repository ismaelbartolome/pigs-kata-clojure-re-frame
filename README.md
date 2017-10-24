# Adding re-frame to the pigs kata

Add re-frame around this implementation of the pigs kata:
[https://github.com/ismaelbartolome/clojure-pig-dice-game](https://github.com/ismaelbartolome/clojure-pig-dice-game)

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
