# _fs_: a lightweight, high-performance, zero-dependency tool lib for java

### ![](docs/fs.svg) | [JavaDoc](https://fredsuvn.github.io/fs-docs/docs/fs/docs/javadoc/index.html) | [Coverage](https://fredsuvn.github.io/fs-docs/docs/fs/reports/jacoco/test/html/index.html) | [JMH](https://fredsuvn.github.io/fs-docs/tools/jmh-visualizer/jmh-visualizer.html?resultsPath=../../docs/fs/jmh/results.json) | [DevDoc](docs/dev-manual.adoc)

## Overview

_fs_ is a lightweight, multi-version JDK support, high-performance, zero-dependency tool lib for java.

### Quick Start

#### Gradle

```kotlin-dsl
dependencies {
    implementation("space.sunqian.fs:fs-all:0.0.2-SNAPSHOT")
}
```

#### Maven

```xml

<dependency>
  <groupId>space.sunqian.fs</groupId>
  <artifactId>fs-all</artifactId>
  <version>0.0.2-SNAPSHOT</version>
</dependency>
```

### Core Modules

- **fs-core**: Main library with comprehensive utilities and interfaces;
- **fs-annotation**: Main annotations of _fs_ for code analysis and validation;
- **fs-jsr305**: JSR-305 annotations implementation (`@Nonnull`, `@Nullable`, etc.);
- **fs-asm**: Embedded ASM framework for bytecode manipulation;
- **fs-all**: Aggregated jar containing all modules (fs-jsr305, fs-annotation, fs-asm, fs-core);

### Core Features

#### fs-core Module

The main library providing comprehensive utilities and interfaces:

**Base Utilities** (`space.sunqian.fs.base`)

- **Bytes & Chars**: BytesKit, CharsKit, BytesBuilder, CharsBuilder for byte/char manipulation
- **Date & Time**: DateKit, DateFormatter for date/time operations
- **Exception Handling**: Comprehensive exception hierarchy and ThrowKit utilities
- **Logging**: SimpleLogger, LogKit for lightweight logging
- **Math & Numbers**: MathKit, NumKit, NumFormatter for mathematical operations
- **Process Management**: ProcessKit, VirtualProcess for process control
- **String Processing**: StringKit, StringView, NameFormatter, NameMapper for string operations
- **System Utilities**: SystemKit, OSKit, JvmKit, ResKit for system-level operations
- **Thread & Concurrency**: ThreadKit, ThreadGate, LocalKit, TraceKit for threading

**Functional Extensions** (`space.sunqian.fs.base.function`, `space.sunqian.fs.base.value`,
`space.sunqian.fs.base.option`)

- Enhanced functional interfaces (IndexedFunction, IndexedConsumer, IndexedPredicate, etc.)
- Value types (Val, Var, Span) and option handling (Option, OptionKit)

**Collections & Data** (`space.sunqian.fs.collect`, `space.sunqian.fs.data`)

- Array, List, Set, Map utilities with enhanced operations
- JSON and Properties data handling with parsers and formatters
- StreamKit for enhanced stream operations

**I/O Operations** (`space.sunqian.fs.io`)

- Byte/char processing with readers, writers, and transformers
- File operations with FileKit and FileRef interfaces
- Communication interfaces for network and IPC

**Networking** (`space.sunqian.fs.net`)

- HTTP client with JDK version-adaptive implementations (HttpURLConnection/HttpClient)
- TCP client/server with builder patterns
- UDP sender/server implementations

**Object Manipulation** (`space.sunqian.fs.object`)

- Object schema definition and parsing
- Object building with builder patterns
- Object conversion and property copying
- Object pooling interfaces

**Dynamic Programming** (`space.sunqian.fs.dynamic`, `space.sunqian.fs.invoke`)

- Dynamic proxy (JDK proxy and ASM-based)
- Aspect-oriented programming (AOP) support
- Reflection and method-handle based invocation

**Dependency Injection** (`space.sunqian.fs.di`)

- Lightweight DI container implementation
- Component lifecycle management

**Utilities** (`space.sunqian.fs.utils`)

- Codec utilities (Base64, Hex, Crypto, Digest)
- Event bus interfaces
- JDBC and SQL utilities
- Semantic versioning support

#### fs-annotation Module

Provides annotations supporting simple static analyses:

- `@Nonnull`, `@Nullable` for null safety
- `@Immutable`, `@RetainedParam` for code analysis
- Custom annotations for validation and code analysis

#### fs-asm Module

Embedded ASM framework for bytecode manipulation:

- Complete ASM library implementation (copied from ASM framework)
- Used for dynamic proxy and AOP functionality
- No external dependencies on org.objectweb.asm

### Third-party Integration

- Built-in [ASM](https://asm.ow2.io) framework for bytecode manipulation
- [Protobuf](https://github.com/protocolbuffers/protobuf) support for object conversion
- Multi-version JDK compatibility (JDK8 to JDK17+);

## Multi-Version JDK Support

_fs_ provides adaptive implementation loading that automatically selects the optimal class version based on the runtime
JDK environment (from `JDK8` to `JDK17`). Its main code is based on `JDK8`, but some interfaces have multiple
implementation classes of different jdk versions.

For example, the implementation class of `space.sunqian.fs.net.http.HttpCaller` has two versions:
`JDK8` and `JDK11`. The former is based on `java.net.HttpURLConnection`, and the latter is based on
`java.net.http.HttpClient`.

_fs_ automatically selects the optimal implementation based on the runtime JDK environment, such as:

- **JDK8**: Uses `HttpURLConnection` for HTTP calls
- **JDK11+**: Uses `HttpClient` for better performance
- **JDK9+**: Enhanced reflection capabilities
- **JDK16+**: Record type support for schema parsing

This ensures:

- **Backward Compatibility**: Seamlessly runs on older JDK versions (`JDK8+`);
- **Forward Optimization**: Leverages newer JDK features when available (up to `JDK17`);
- **Automatic Detection**: No manual configuration required -- the library automatically loads the appropriate
  implementation at runtime;

## High Performance

_fs_ has higher performance than other common libraries in many places, Here are some examples:

### High-Performance Utilities

- **SimpleCache** (`space.sunqian.fs.cache.SimpleCache`):
  Lightweight caching with excellent performance, focusing on common cache functions.
  Here is the benchmark: [Cache](fs-tests/src/jmh/java/internal/benchmark/CacheJmh.java)

- **Object Conversion** (`Fs.copyProperties / space.sunqian.fs.object.convert.ObjectCopier`):
  Fast property copying and object mapping with better performance and comprehensive support.
  Here is the benchmark: [CopyProperties](fs-tests/src/jmh/java/internal/benchmark/CopyPropertiesJmh.java)

- **TCP Server** (`space.sunqian.fs.net.tcp.TcpServer`):
  High-performance TCP server implementation with slightly better performance than **netty**.
  Here is the benchmark: [TcpServer](fs-tests/src/jmh/java/internal/benchmark/TcpServerJmh.java)

- **JSON Processing**: Efficient JSON parsing and formatting with optimized implementations.

### Benchmark Results

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