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
