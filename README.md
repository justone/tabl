# tabl

Make tables from data in your terminal.

# Install

Download the latest from the [releases page](https://github.com/justone/tabl/releases).

# Usage

This small utility takes JSON or EDN files with multiple maps and creates a
table for ease of viewing. For example:

```
$ cat test.json
{"foo":"bar","baz":4}
{"foo":"oof","baz":4}
$ cat test.json | tabl -j
 :baz | :foo
------|------
 4    | bar
 4    | oof
```

There are several table rendering modes available:

| library                                            | modes               |
|----------------------------------------------------|---------------------|
| [fancy](https://github.com/chbrown/fancy-clojure/) | fancy               |
| [doric](https://github.com/joegallo/doric)         | org, csv, html, raw |


```
$ cat test.json | tabl -j -m org
|-----+-----|
| Foo | Baz |
|-----+-----|
| bar | 4   |
| oof | 4   |
|-----+-----|
```

# [Babashka pod](https://github.com/babashka/babashka.pods) support

The following namespaces and functions are exposed via the pod interface:

* `pod.tabl.fancy` (see [here](https://cljdoc.org/d/fancy/fancy/0.2.3/api/fancy.table) for more information)
    * `render-table` - returns table as a list of strings
    * `print-table` - prints a table based on data
* `pod.tabl.doric`
    * `table` - returns table as a list of strings  (see [here](https://github.com/joegallo/doric) for more information)
    * `print-table` - prints a table based on data

Example:

```
#!/usr/bin/env bb

(require '[babashka.pods :as pods])
(pods/load-pod "tabl")

(require '[pod.tabl.fancy :as fancy])
(require '[pod.tabl.doric :as doric])

(fancy/print-table [{:foo 1 :bar 2} {:foo 2 :bar 3}])
(doric/print-table [{:foo 1 :bar 2} {:foo 2 :bar 3}])
```

# Development

Not quite ready yet. This depends on a soon-to-be-released library.

Thank you to [Michiel Borkent](https://github.com/borkdude) and
[Lee Read](https://github.com/lread) for spearheading the GraalVM efforts,
documented [here](https://github.com/lread/clj-graal-docs).

# License

Copyright © 2019-2020 Nate Jones

Distributed under the EPL License. See LICENSE.

This project contains code from:

[babashka](https://github.com/borkdude/babashka), which is licensed under the same EPL License.
