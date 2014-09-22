# lein-inject

A leiningen plugin to inject vars into clojure namespaces for use at
the repl.

In the repl, there are functions that you always want to have handy,
no matter which namespace you are in.  One way of achieving this is to
put these functions into a namespace with a short name, like `.`, so
you can refer to them easily.  The original idea came from Gary
Fredericks, who also wrote [dot-slash][dot-slash], which I discovered
after writing this.

## Usage

Add `lein-inject` to `:plugins`:

```clj
:plugins [[com.palletops/lein-inject "0.3.0"]]
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

The `:inject-ns` will eagerly load all the namespaces of vars that you
inject, which can increase your REPL start-up time, and increase the
number of naamespaces that are loaded in your REPL but not used in
your projects.

You can make the injection happen lazily, on first use, by using the
`:inject-ns-fns` and `:inject-ns-macros` keys.  Since the vars will
not be required until first use, you have to explicitly use the
`:inject-ns-macros` key for macros.

## How it works

`:inject-ns` defines vars in the target namespace that are set to the
vars resolved by the specified symbols.  The namespace of the var is
eagerly required when the function is defined.  This works with
functions, macros and protocol functions.

`:inject-ns-fns` and `:inject-ns-macros` define functions and macros
 respectively in the target namespace, that resolve the var to inject,
 and replaces itself with that var.  This means that the injected
 function isn't actually required until it is used, and that it always
 tracks the injected var.  Since the source var is not available when
 the function is defined, you have to explicitly specify which symbols
 are macros.

If you prefer, you can use `:inject-ns` and add metadata to the
symbols that you wish to inject lazily.

```clj
:inject-ns {. [clojure.pprint/pprint
               ^:lazy alembic.still/distill
               ^:lazy ^:macro alembic.still/lein]}
```

## Differences with dot-slash

`lein-inject` does not add anything to your classpath.

`dot-slash` adds a dependency on `potemkin` to your project's
classpath in order to propagate var updates to the injected
vars. `lein-inject` achieves the same effect by using the source var
as the value of the injected var, without `deref`'ing it.

`lein-inject` uses the `:injections` key and can be composed using
multiple profiles, while `dot-slash` uses `[:repl-options :init]`.

## License

Copyright © 2014 Hugo Duncan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.


[dot-slash]:https://github.com/gfredericks/dot-slash "Gary Fredericks' dot-slash plugin"
