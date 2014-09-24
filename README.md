# lein-shorthand

A leiningen plugin to create clojure namespaces with short names, so
you can easily call utility functions in the REPL using fully
qualified symbols.

In the repl, there are functions that you always want to have handy,
no matter which namespace you are in.  One way of achieving this is to
put these functions into a namespace with a short name, like `.`, so
you can refer to them easily.  The original idea came from Gary
Fredericks, who also wrote [dot-slash][dot-slash], which I discovered
after writing this.  Many thanks to Gary for suggesting lein-shorthand
as the name for this plugin.

## Usage

Add `lein-shorthand` to `:plugins` in the `:user` profile of
`~/.lein/profiles.clj`:

```clj
{:user
  {…
   :plugins [[com.palletops/lein-shorthand "0.4.0"]]
   …}}
```

You create shorthand namespaces using the `:shorthand` project key
(again, in the `:user` profile).  For example to define the `.`
namespace with clojure's `pprint` and [alembic][alembic]'s `still`
function and `lein` macro:

```clj
:shorthand {. [clojure.pprint/pprint
               alembic.still/distill
               alembic.still/lein]}
```

In the repl you will then be able to use `(./distill
[[clj-http "1.0.0."]])` to add `clj-http` to your classpath.

You can also rename symbols:

```clj
:shorthand {. {pp clojure.pprint/pprint}}
```

The `:shorthand` value is a map where the keys are namespace symbols,
and the values are either a sequence of fully qualified symbols for
vars you want injected into in the namespace, or a map from
unqualified symbol, for the name of the function in the target
namespace, to the fully qualified symbol of the var it should map to.

By default `:shorthand` namespaces will eagerly load all the
namespaces of vars that you specify, which can increase your REPL
start-up time, and increase the number of namespaces that are loaded
in your REPL but not used in your projects.

You can make the require of the target namespace happen lazily, on
first use, by using the `:shorthand-fns` and `:shorthand-macros` keys
instead of the `:shorthand`.  Since the vars will not be required
until first use, you have to explicitly use the `:shorthand-macros`
key for macros as `lein-shorthand` has no other way of knowing that it
should add `:macro` metadata to these vars.

If you prefer, you can use `:shorthand` and add metadata to the
symbols that you wish to use lazy require with.

```clj
:shorthand {. [clojure.pprint/pprint
               ^:lazy alembic.still/distill
               ^:lazy ^:macro alembic.still/lein]}
```

## How it works

`:shorthand` defines vars in the target namespace.  The vars values
are set to the vars resolved by the specified symbols.  The namespace
of the var is eagerly required when the function is defined.  This
works with functions, macros and protocol functions.

`:shorthand-fns`, `:shorthand-macros` and symbols annotated with
`:lazy` metadata in the `:shorthand` key, define functions and macros
in the target namespace, that resolve the var to inject, and replaces
itself with that var.  This means that the injected function isn't
actually required until it is used.  Since the source var is not
available when the function is defined, you have to explicitly specify
which symbols are macros.

The shothand vars always tracks the target var.

## Differences with dot-slash

`lein-shorthand` does not add anything to your classpath.

`dot-slash` adds a dependency on `potemkin` to your project's
classpath in order to propagate var updates to the injected
vars. `lein-shorthand` achieves the same effect by using the source var
as the value of the injected var, without `deref`'ing it.

`lein-shorthand` uses the `:injections` key and can be composed using
multiple profiles, while `dot-slash` uses `[:repl-options :init]`.

## License

Copyright © 2014 Hugo Duncan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[dot-slash]:https://github.com/gfredericks/dot-slash "Gary Fredericks' dot-slash plugin"
[alembic]: http://github.com/pallet/alembic "Alembic - add to you REPL classpath and run lein tasks"
