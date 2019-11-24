# tabl

Make tables from data in your terminal.

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

# Installation

## Clojure CLI

```
clojure -m app.main
```

## GraalVM build

Download the [Community Edition of GraalVM](https://www.graalvm.org/downloads/)
and unpack it on your disk, then install native-image.

```
wget .../graalvm-ce-java8-linux-amd64-xx.y.z.tar.gz
tar -xzvf graalvm-ce-java8-linux-amd64-xx.y.z.tar.gz
cd graalvm-ce-java8-xx.y.z
./bin/gu install native-image
```

Then build using the compile script:

```
NATIVE_IMAGE=graalvm-ce-java8-xx.y.z/bin/native-image ./script/compile
```

There will then be a `tabl` binary in the current directory.

Thank you to [Michiel Borkent](https://github.com/borkdude) and
[Lee Read](https://github.com/lread) for spearheading the GraalVM efforts,
documented [here](https://github.com/lread/clj-graal-docs).

# License

Copyright © 2019 Nate Jones

Distributed under the EPL License. See LICENSE.

This project contains code from:

[babashka](https://github.com/borkdude/babashka), which is licensed under the same EPL License.
