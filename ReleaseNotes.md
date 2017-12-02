## 0.4.1 (2017-12-02)

- **Change maven group-id to `com.gfredericks`**
- Fixed [clojure 1.9 compatibility](https://github.com/palletops/lein-shorthand/issues/7)

## 0.4.0

- Rename project to lein-shorthand
  Now uses the :shorthand project key for configuration.

- Use create-ns rather than in-ns
  Simplifies the generated code, removing a try…finally form.

- Remove :shorthand-protocol-fns key
  This was incorrect, as the :protocol metadata requires the var of the
  protocol as a value.

## 0.3.1

- Speed up plugin loading
  Avoid the `require` on `leiningen.repl`.  This saves about 1.5s on `lein
  version` when the plugin is active.

## 0.3.0

- Use metadata on :inject-ns symbols to lazy inject
  Use :lazy, and :macro as required, metadata on symbols specified in the
  :inject-ns project key to specify lazy loading.

## 0.2.0

- Add Lazy injection of functions and macros
  Eagerly loading all the namespaces of vars that you inject, can increase
  your REPL start-up time, and increase the number of namespaces that are
  loaded in your REPL but not used in your projects.

  This allows `:inject-ns-fns` and `:inject-ns-macros` to be used to specify
  injections that will be loaded lazily.

- Allow changes in vars to propagate
  When injecting a var, do not deref, it so that changes are propagated.
  This means the value of vars in the injected namespaces are themselves
  vars rather than functions.  This should be transparent in most repl
  usage.

## 0.1.0

- Initial release
