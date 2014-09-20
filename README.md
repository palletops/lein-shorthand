# lein-inject

A leiningen plugin to inject vars into clojure namespaces for use at
the repl.

In the repl, there are functions that you always want to have handy,
no matter which namespace you are in.  One way of achieving this is to
put these functions into a namespace with a short name, like `.`, so
you can refer to them easily.  The original idea came from Gary
Frederick, who also wrote [dot-slash][dot-slash], which I discovered
after writing this.

## Usage

Add `lein-inject` to `:plugins`:

```clj
:plugins [[com.palletops/lein-inject "0.1.0"]]
```

Configure using the `:inject-ns` key.  For example to define the `.`
namespace with clojure's `pprint` and alembic's `still` function:

```clj
:inject-ns {. [clojure.pprint/pprint
               alembic.still/distill]}
```

In the repl you will then be able to use `(./distill
[[clj-http "1.0.0."]])` to add `clj-http` to your classpath.

You can also rename symbols:

```clj
:inject-ns {. {pp clojure.pprint/pprint}}
```

## Differences with dot-slash

`lein-inject` does not add anything to your classpath.  `dot-slash`
adds `potemkin` in order to propagate var updates to the injected
vars, which `lein-inject` does not do.

`lein-inject` uses the `:injections` key, while `dot-slash` uses
`[:repl-options :init]`.

## License

Copyright © 2014 Hugo Duncan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.


[dot-slash]:https://github.com/gfredericks/dot-slash "Gary Frederick's dot-slash plugin"
