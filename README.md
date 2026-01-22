# _fs_: a lightweight, high-performance, zero-dependency tool lib for java

### ![](docs/fs.svg) | [JavaDoc](https://fredsuvn.github.io/fs-docs/docs/fs/docs/javadoc/index.html) | [Coverage](https://fredsuvn.github.io/fs-docs/docs/fs/reports/jacoco/test/html/index.html) | [JMH](https://fredsuvn.github.io/fs-docs/tools/jmh-visualizer/jmh-visualizer.html?resultsPath=../../docs/fs/jmh/results.json) | [DevDoc](docs/dev-manual.adoc)

## Overview

_fs_ is a lightweight, multi-version JDK support, high-performance, zero-dependency tool lib for java. It provides:

- Annotations that can be used for code analysis;
- Core class `space.sunqian.fs.Fs`;
- Base and core utilities and interfaces for bytes, chars, coding, date, enum, exception, logging, math, number,
  process, random, resource, string, system, thread and checker;
- Extensions for functions, values, and options;
- I/O kits and interfaces;
- More light and fast cache interfaces and implementations;
- Kits for collections, maps, arrays, etc.;
- Concurrent supports;
- Network kits and interfaces for http, tcp, udp, etc.;
- Object parsing, creating, and conversion;
- Dynamic invocation, supporting reflection, method-handle and [asm](https://asm.ow2.io);
- Dynamic proxy and aspect, supporting JDK dynamic proxy and [asm](https://asm.ow2.io);
- Reflection;
- Utilities for codec, dependency injection, eventbus, jdbc, semantic version, etc.;
- Third-party supporting: [asm](https://asm.ow2.io), [protobuf](https://github.com/protocolbuffers/protobuf);

## Multi-Version JDK Support

_fs_ provides adaptive implementation loading that automatically selects the optimal class version based on the runtime
JDK environment (from `JDK8` to `JDK17`). Its main code is based on `JDK8`, but some interfaces have multiple
implementation classes of different jdk versions.

For example, the implementation class of `space.sunqian.fs.net.http.HttpCaller` has two versions:
`JDK8` and `JDK11`. The former is based on `java.net.HttpURLConnection`, and the latter is based on
`java.net.http.HttpClient`.

This ensures:

- **Backward Compatibility**: Seamlessly runs on older JDK versions (`JDK8+`);
- **Forward Optimization**: Leverages newer JDK features when available (up to `JDK17`);
- **Automatic Detection**: No manual configuration required -- the library automatically loads the appropriate
  implementation at runtime;

## High Performance

_fs_ has higher performance than other common libraries in many places, Here are some examples:

- **Simple Cache** (`space.sunqian.fs.cache.SimpleCache`):
  SimpleCache only considers common cache functions, so it has higher performance in common functions.
  Here is the benchmark: [CacheBenchmark](fs-tests/src/jmh/java/internal/tests/benchmarks/CacheBenchmark.java)

- **CopyProperties** (`Fs.copyProperties / space.sunqian.fs.object.convert.PropertyCopier`):
  DataMapper has better performance and more comprehensive support.
  Here is the
  benchmark: [CopyPropertiesBenchmark](fs-tests/src/jmh/java/internal/tests/benchmarks/CopyPropertiesBenchmark.java)

- **TCP Server** (`space.sunqian.fs.net.tcp.TcpServer`):
  Rare interface server implementation with slightly better performance than **netty**.
  Here is the benchmark: [TcpServerBenchmark](fs-tests/src/jmh/java/internal/tests/benchmarks/TcpServerBenchmark.java)

Here is the full benchmark result: [results.json](docs/jmh/results.json).

And here is the benchmark result
chart: [JMH Visualizer](https://fredsuvn.github.io/fs-docs/tools/jmh-visualizer/jmh-visualizer.html?resultsPath=../../docs/fs/jmh/results.json).

## Zero Dependency

_fs_ has no strong dependency on any other libraries (except for JDK and its own modules, and _fs-all_ aggregates all
the classes of all modules without module dependencies).
It implements some of the classes from the `javax` package in jsr305, such as `javax.annotation.Nonnull`, and based on
this, it also implements its own annotations, such as:
`space.sunqian.annotatations.Nonnull` and `space.sunqian.annotatations.Nullable`.

Some functions of _fs_ are based on
[ASM](https://asm.ow2.io) and [protobuf](https://github.com/protocolbuffers/protobuf), such as bytecode proxy, aspect,
and object conversion about protobuf. The *ASM* uses built-in package `space.sunqian.fs.asm`, so this lib has no
dependencies for package `org.objectweb.asm`.

For protobuf, although it depends on `com.google.protobuf`, the protobuf classes will not be loaded unless necessary.

## Test passing rate and coverage: 100%

The test passing rate and JaCoCo coverage for this library are both 100%:

- [Aggregate Test Report](https://fredsuvn.github.io/fs-docs/docs/fs/reports/tests/test/aggregated-results/index.html)
- [JaCoCo Coverage](https://fredsuvn.github.io/fs-docs/docs/fs/reports/jacoco/test/html/index.html)

> _fs-asm_ is excluded from the test and coverage report because it is total copied from [ASM](https://asm.ow2.io)
> framework.

## Documents and Samples

- For Users:
  * [JavaDoc](https://fredsuvn.github.io/fs-docs/docs/fs/docs/javadoc/index.html)
- For Developers:
  * [DevDoc](docs/dev-manual.adoc)
- Samples:
  * [Annotation](./fs-tests/src/main/java/internal/samples/AnnotationSample.java)
  * [Simple Cache](./fs-tests/src/main/java/internal/samples/CacheSample.java)
  * [Dependency Injection](./fs-tests/src/main/java/internal/samples/DISample.java)
  * [Proxy and Aspect](./fs-tests/src/main/java/internal/samples/ProxyAndAspectSample.java)
  * [Object Conversion](./fs-tests/src/main/java/internal/samples/ObjectSamples.java)
  * [Net](./fs-tests/src/main/java/internal/samples/NetSample.java)
  * [Other samples](./fs-tests/src/main/java/internal/samples/OtherSamples.java)

## Clone and Build

_fs_ uses `gradle` as the build tool:

```shell
# clone and build
git clone -b master https://github.com/fredsuvn/fs.git
cd fs && gradle clean build
```

_fs_ needs multi JDK versions to build: from `1.8` to `17`.

> The root `gradle` properties file ([gradle.properties](gradle.properties)) can configure the multi JDK versions like:
> ```
> org.gradle.java.installations.paths=\
>   ~/.jdks/azul-1.8.0_462,\
>   ~/.jdks/azul-17.0.17,\
>   ~/.jdks/openjdk-25,
> ```
> Add/Change this property to your local JDK paths, or let `gradle` automatically download required JDK versions without
> manual configuration.

## Contact

* [https://github.com/fredsuvn/fs](https://github.com/fredsuvn/fs/)
* [QQ group: 566185308](https://qm.qq.com/q/wlkc2tmOaG)

## License

[Apache 2.0 license][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0.html

![](docs/fs.svg)