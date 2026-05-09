# ![fs](docs/logo/fs.svg) fs: A Lightweight, High-Performance, Zero-Dependency Tool Library for Java

[![Build Status](https://img.shields.io/github/actions/workflow/status/fredsuvn/fs/test.yml)](https://github.com/fredsuvn/fs/actions)
[![Coverage](https://img.shields.io/codecov/c/github/fredsuvn/fs)](https://codecov.io/gh/fredsuvn/fs)
[![License](https://img.shields.io/github/license/fredsuvn/fs)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/space.sunqian.fs/fs-all)](https://search.maven.org/artifact/space.sunqian.fs/fs-all)

---

## Overview

**fs** is a lightweight, multi-version JDK support, high-performance, zero-dependency tool library for Java. It provides comprehensive utilities for common development tasks with a focus on simplicity and performance.

✨ **Core Features:**
- **Zero Dependency**: No external dependencies, pure JDK implementation
- **Multi-Version JDK Support**: Automatic adaptation from JDK 8 to JDK 17+
- **High Performance**: Optimized implementations for critical operations
- **Comprehensive Utilities**: Covers I/O, networking, reflection, object manipulation, and more
- **Modern API**: Clean, fluent interfaces with null-safety annotations

---

## Why Choose fs?

- **No Bloat**: Minimal footprint, focused on essential utilities
- **Smart Adaptation**: Automatically selects optimal implementations based on runtime JDK version
- **Battle-Tested**: 100% test coverage across all modules
- **Production Ready**: Used in enterprise applications with strict quality requirements

---

## Quick Start

### Installation

#### Gradle
```kotlin
dependencies {
    implementation("space.sunqian.fs:fs-all:0.0.4-SNAPSHOT")
}
```

#### Maven
```xml
<dependency>
  <groupId>space.sunqian.fs</groupId>
  <artifactId>fs-all</artifactId>
  <version>0.0.4-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import space.sunqian.fs.Fs;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;
import space.sunqian.fs.object.meta.ObjectMeta;

public class App {
    public static void main(String[] args) {
        // Null safety
        String value = Fs.nonnull(input, "default");

        // Create collections
        var list = Fs.list("a", "b", "c");
        var map = Fs.map("key1", 1, "key2", 2);

        // Object conversion
        TargetType target = Fs.convert(source, TargetType.class);

        // Property copying
        Fs.copyProperties(source, target);

        // Object meta introspection
        ObjectMeta meta = ObjectMeta.of(Person.class);
        System.out.println("Properties: " + meta.properties().keySet());
    }
}
```

---

## Module Overview

| Module | Description |
|--------|-------------|
| `fs-core` | Core utilities including I/O, networking, reflection, object manipulation, DI |
| `fs-annotation` | Custom annotations for code analysis and null safety |
| `fs-jsr305` | JSR-305 annotations implementation (`@Nonnull`, `@Nullable`) |
| `fs-asm` | Embedded ASM framework for bytecode manipulation |
| `fs-all` | Aggregated JAR containing all modules |

---

## Core Functionality

### Base Utilities
```java
// String operations
StringKit.isEmpty(str);
StringView view = StringView.of("Hello");

// Date/time operations
DateKit.format(LocalDateTime.now());
DateKit.parse("2024-01-01", "yyyy-MM-dd");

// System utilities
SystemKit.getJavaVersion();
OSKit.isWindows();
```

### Object Manipulation
```java
// Object conversion
DataTo dataTo = ObjectConverter.defaultConverter()
    .convert(dataFrom, DataTo.class);

// Property copying
ObjectCopier.defaultCopier().copyProperties(source, target);

// Object meta introspection
ObjectMeta meta = ObjectMeta.of(Person.class);
meta.properties().forEach((name, prop) -> {
    System.out.println(name + ": " + prop.type());
});
```

### Dynamic Programming
```java
// Dynamic proxy using ASM
Hello proxy = ProxyMaker.byAsm().make(Hello.class, proxyHandler).newInstance();

// Aspect-oriented programming
Hello aspect = AspectMaker.byAsm().make(Hello.class, aspectHandler).newInstance();
```

### Networking
```java
// TCP Server
TcpServer tcpServer = TcpServer.builder()
    .address(8080)
    .handler(ctx, msg -> ctx.writeAndFlush(msg))
    .build();
tcpServer.start();

// HTTP client
HttpCaller caller = HttpCaller.of("https://api.example.com");
String response = caller.get("/endpoint").execute().body();
```

### I/O Operations
```java
// Byte/Char processing
ByteReader byteReader = ByteReader.from(inputStream);
CharReader charReader = CharReader.from(reader);

// Building data
BytesBuilder bytesBuilder = new BytesBuilder();
bytesBuilder.append("Hello".getBytes(StandardCharsets.UTF_8));
```

### Dependency Injection
```java
import space.sunqian.fs.di.DIContainer;
import space.sunqian.fs.di.DIAspectHandler;

// Create and configure DI container
DIContainer container = DIContainer.newBuilder()
    .componentTypes(MyController.class, MyServiceImpl.class, MyAspect.class)
    .componentAnnotation(MyResource.class)
    .postConstructAnnotation(MyPostConstruct.class)
    .preDestroyAnnotation(MyPreDestroy.class)
    .build()
    .initialize();

// Get component from container
MyService service = container.getObject(MyService.class);
String result = service.doService();

// Parent-child container relationship
DIContainer childContainer = DIContainer.newBuilder()
    .parentContainers(container)
    .componentTypes(AdditionalService.class)
    .build()
    .initialize();

// Shutdown containers
childContainer.shutdown();
container.shutdown();
```

---

## Documentation

- [JavaDoc](https://fredsuvn.github.io/fs-docs/docs/fs/docs/javadoc/index.html)
- [Developer Guide](docs/dev/DEVELOPMENT.adoc)
- [JMH Benchmarks](https://fredsuvn.github.io/fs-docs/tools/jmh-visualizer/jmh-visualizer.html?resultsPath=../../docs/fs/jmh/results.json)
- [Test Coverage](https://fredsuvn.github.io/fs-docs/docs/fs/reports/jacoco/test/html/index.html)

---

## Samples

Explore the [samples directory](fs-tests/src/samples) for detailed usage examples:

| Sample | Description |
|--------|-------------|
| `BaseSample` | Base utilities (string, date, system, thread) |
| `IOSample` | I/O operations (ByteReader, CharReader, builders) |
| `ObjectSamples` | Object conversion and property copying |
| `MetaSample` | Object meta introspection |
| `ProxySample` | Dynamic proxy creation |
| `AspectSample` | Aspect-oriented programming |
| `DISample` | Dependency injection container |
| `TcpSample` / `UdpSample` | Network communication |
| `JsonSample` | JSON parsing and formatting |
| `CacheSample` | Lightweight caching utilities |
| `EventBusSample` | Event-driven communication |
| `JdbcSample` | JDBC utilities |
| `CodecSample` | Base64, Hex encoding utilities |

---

## Build & Contribute

**Requirements:**
- JDK 8+ for compilation
- Gradle 8.x

**Build Command:**
```bash
./gradlew clean build
```

**Run Tests:**
```bash
./gradlew test
```

**Run Benchmarks:**
```bash
./gradlew jmh
```

**Contributing:**
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

---

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.

---

![fs](docs/logo/fs.svg)
