# _fs_: a lightweight, high-performance, zero-dependency tool lib for java

### ![](docs/fs.svg) | [JavaDoc](https://fredsuvn.github.io/fs-docs/docs/fs/docs/javadoc/index.html) | [Coverage](https://fredsuvn.github.io/fs-docs/docs/fs/reports/jacoco/test/html/index.html) | [JMH](https://fredsuvn.github.io/fs-docs/tools/jmh-visualizer/jmh-visualizer.html?resultsPath=../../docs/fs/jmh/results.json) | [DevDoc](docs/dev/dev.adoc) | [Samples](fs-tests/src/samples)

## Overview

_fs_ is a lightweight, multi-version JDK support, high-performance, zero-dependency tool lib for java.

## Quick Start

### Installation

#### Gradle

```kotlin-dsl
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

#### Import the core class and other required classes

```java
// Core class
import space.sunqian.fs.Fs;

// I/O classes
import space.sunqian.fs.io.ByteReader;
import space.sunqian.fs.io.CharReader;
import space.sunqian.fs.io.ByteProcessor;
import space.sunqian.fs.io.ByteTransformer;

// Networking classes
import space.sunqian.fs.net.tcp.TcpServer;
import space.sunqian.fs.net.tcp.TcpServerHandler;
import space.sunqian.fs.net.tcp.TcpServerHandlerContext;
import space.sunqian.fs.net.tcp.TcpClient;
import space.sunqian.fs.net.udp.UdpSender;
import space.sunqian.fs.net.udp.UdpServer;
import space.sunqian.fs.net.udp.UdpServerHandler;
import space.sunqian.fs.net.udp.UdpServerHandlerContext;

// Invoke classes
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.invoke.InvocationMode;
import space.sunqian.fs.invoke.InvokeKit;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

// Dynamic programming classes
import space.sunqian.fs.dynamic.proxy.ProxyMaker;
import space.sunqian.fs.dynamic.proxy.ProxyHandler;
import space.sunqian.fs.dynamic.proxy.ProxyInvoker;
import space.sunqian.fs.dynamic.aspect.AspectMaker;
import space.sunqian.fs.dynamic.aspect.AspectHandler;
import java.util.Collections;

// Other required classes
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
```

#### Example 1: Lang operations

```java
// Null safety
String value = Fs.nonnull(input, "default");

// Object equality
boolean equals = Fs.equals(obj1, obj2);

// Hash code generation
int hashCode = Fs.hashCode(obj);

// String conversion
String str = Fs.toString(obj);
```

#### Example 2: Collection operations

```java
// Create collections
List<String> list = Fs.list("a", "b", "c");
Set<String> set = Fs.set("x", "y", "z");
Map<String, Integer> map = Fs.map("key1", 1, "key2", 2);

// Stream operations
Stream<String> stream = Fs.stream(list);
```

#### Example 3: Object conversion and property copying

```java
// Object conversion
TargetType target = Fs.convert(source, TargetType.class);

// Property copying
Fs.copyProperties(source, target);
```

#### Example 4: I/O operations (Reader, Processor, Transformer)

```java
// ByteReader usage
ByteReader byteReader = ByteReader.from(inputStream);
byte[] buffer = new byte[1024];
int read = byteReader.readTo(buffer);

// CharReader usage
CharReader charReader = CharReader.from(reader);
char[] charBuffer = new char[1024];
int charRead = charReader.readTo(charBuffer);

// ByteProcessor usage with ByteTransformer
ByteProcessor processor = ByteProcessor.from(inputStream)
    .transformer((data, end) -> {
        // Transform bytes here
        return data;
    });
byte[] processed = processor.toByteArray();

// CharProcessor usage with CharTransformer
CharProcessor charProcessor = CharProcessor.from(reader)
    .transformer((data, end) -> {
        // Transform chars here
        return data;
    });
String processedString = charProcessor.toString();
```

#### Example 5: Networking (TCP, UDP)

```java
// TCP Server
TcpServer tcpServer = TcpServer.builder()
    .address(8080)
    .handler(new TcpServerHandler() {
        @Override
        public void channelRead(TcpServerHandlerContext ctx, ByteBuffer msg) {
            ctx.writeAndFlush(msg);
        }
    })
    .build();
tcpServer.start();

// TCP Client
TcpClient tcpClient = TcpClient.builder()
    .address("localhost", 8080)
    .build();
tcpClient.connect();
tcpClient.send(ByteBuffer.wrap("Hello".getBytes()));

// UDP Sender
UdpSender udpSender = UdpSender.builder().build();
InetSocketAddress remoteAddress = new InetSocketAddress("localhost", 8081);
udpSender.sendData(remoteAddress, "Hello".getBytes());

// UDP Server
UdpServer udpServer = UdpServer.builder()
    .address(8081)
    .handler(new UdpServerHandler() {
        @Override
        public void datagramReceived(UdpServerHandlerContext ctx, ByteBuffer msg, InetSocketAddress remoteAddress) {
            // Handle UDP datagram
        }
    })
    .build();
udpServer.start();
```

#### Example 6: Invoke operations

```java
// Get method via reflection
Method method = obj.getClass().getMethod("methodName", Param1.class, Param2.class);

// Create Invocable with recommended mode (auto-selected based on environment)
Invocable invocable = Invocable.of(method);
Object result = invocable.invoke(obj, param1, param2);

// Create Invocable with specific mode - Reflection
Invocable reflectionInvocable = Invocable.of(method, InvocationMode.REFLECTION);
Object result2 = reflectionInvocable.invoke(obj, param1, param2);

// Create Invocable with specific mode - Method Handle
Invocable methodHandleInvocable = Invocable.of(method, InvocationMode.METHOD_HANDLE);
Object result3 = methodHandleInvocable.invoke(obj, param1, param2);

// Create Invocable with specific mode - ASM (high performance)
Invocable asmInvocable = Invocable.of(method, InvocationMode.ASM);
Object result4 = asmInvocable.invoke(obj, param1, param2);

// Invoke static method
Method staticMethod = SomeClass.class.getMethod("staticMethod", String.class);
Invocable staticInvocable = Invocable.of(staticMethod);
Object staticResult = staticInvocable.invoke(null, "staticParam");

// Invoke constructor
Constructor<?> constructor = SomeClass.class.getConstructor(String.class);
Invocable constructorInvocable = Invocable.of(constructor);
Object newInstance = constructorInvocable.invoke(null, "constructorParam");

// Use MethodHandle directly via InvokeKit
MethodHandle handle = MethodHandles.lookup().unreflect(method);
Object directResult = InvokeKit.invokeInstance(handle, obj, param1, param2);
```

#### Example 7: Dynamic programming (Proxy, Aspect)

```java
// Dynamic Proxy using ASM
Hello proxy = ProxyMaker.byAsm().make(Hello.class, Collections.emptyList(), new ProxyHandler() {
    @Override
    public boolean needsProxy(@Nonnull Method method) {
      return method.getName().equals("hello");
    }

    @Override
    public @Nonnull Object invoke(
      @Nonnull Object proxy, @Nonnull Method method, @Nonnull ProxyInvoker invoker, Object @Nonnull ... args
    ) throws Throwable {
      System.out.println("Proxy: Before invoking hello()");
      Object result = invoker.invokeSuper(proxy, args);
      System.out.println("Proxy: After invoking hello()");
      return result + "[proxy]";
    }
  }).newInstance();

// Call method on proxy
String proxyResult = proxy.hello();

// Aspect-oriented programming using ASM
Hello aspect = AspectMaker.byAsm().make(Hello.class, new AspectHandler() {
  @Override
  public boolean needsAspect(@Nonnull Method method) {
    return method.getName().equals("hello");
  }

  @Override
  public void beforeInvoking(@Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
    System.out.println("Aspect: Before invoking hello()");
  }

  @Override
  public @Nonnull Object afterReturning(@Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
    System.out.println("Aspect: After returning from hello()");
    return result + "[aspect]";
  }

  @Override
  public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) {
    System.out.println("Aspect: After throwing exception");
    return null;
  }
}).newInstance();

// Call method on aspect-enhanced object
String aspectResult = aspect.hello();

// Sample Hello class
class Hello {
  public String hello() {
    System.out.println("Hello.hello() called");
    return "hello";
  }
}
```

### Documents

- For Users:
  * [JavaDoc](https://fredsuvn.github.io/fs-docs/docs/fs/docs/javadoc/index.html)
- For Developers:
  * [DevDoc](docs/dev/dev.adoc)

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

## Samples

### Base Utilities

#### BaseSample

Demonstrates usage of base utility classes including string processing, date/time operations, system utilities, thread utilities, logging, mathematical operations, random number generation, number utilities, option handling, and function utilities.

**Basic usage with string and date utilities:**
```java
StringKit.isEmpty(str);
DateKit.format(now);
SystemKit.getJavaVersion();
ThreadKit.sleep(1000);
```

**More samples:**
[Complete BaseSample](./fs-tests/src/samples/java/internal/samples/BaseSample.java)

#### IOSample

Demonstrates IO utilities including ByteReader, CharReader, ByteIOOperator, CharIOOperator, BytesBuilder, and CharsBuilder for byte/char processing and data building.

**Basic usage with ByteReader and CharReader:**
```java
ByteReader byteReader = ByteReader.from(inputStream);
CharReader charReader = CharReader.from(reader);
byte[] buffer = new byte[1024];
int read = byteReader.readTo(buffer);
```

**Basic usage with BytesBuilder and CharsBuilder:**
```java
BytesBuilder bytesBuilder = new BytesBuilder();
bytesBuilder.append("Hello, ".getBytes(StandardCharsets.UTF_8));
byte[] bytesResult = bytesBuilder.toByteArray();

CharsBuilder charsBuilder = new CharsBuilder();
charsBuilder.append("Hello, ").append("World!");
String result = charsBuilder.toString();
```

**More samples:**
[Complete IOSample](./fs-tests/src/samples/java/internal/samples/IOSample.java)

#### CodecSample

Demonstrates codec utilities for Base64, Hex encoding, and other encoding formats.

**Basic usage with Base64 and Hex:**
```java
String encoded = Base64Kit.encode(data);
byte[] decoded = Base64Kit.decode(encoded);
String hex = HexKit.toHex(bytes);
```

**More samples:**
[Complete CodecSample](./fs-tests/src/samples/java/internal/samples/CodecSample.java)

### Collections & Data

#### CollectionSample

Demonstrates collection utilities including ArrayKit, ListKit, SetKit, MapKit, and StreamKit for enhanced collection operations.

**Basic usage with collection utilities:**
```java
ArrayKit.contains(array, "target");
ListKit.filter(list, e -> e > 0);
Map<String, Object> map = MapKit.newMap();
```

**More samples:**
[Complete CollectionSample](./fs-tests/src/samples/java/internal/samples/CollectionSample.java)

#### DataSample

Demonstrates data handling with DataMap and DataList for nested data structures, JSON parsing, and Properties processing.

**Basic usage with DataMap and DataList:**
```java
DataMap dataMap = DataMap.newMap();
dataMap.put("name", "John");
dataMap.put("age", 30);
String json = JsonKit.toJsonString(dataMap.toMap());
```

**More samples:**
[Complete DataSample](./fs-tests/src/samples/java/internal/samples/DataSample.java)

#### JsonSample

Demonstrates JSON parsing and formatting with JsonKit for JSON data processing.

**Basic usage with JSON:**
```java
Map<String, Object> jsonMap = JsonKit.parseObject(jsonString);
String prettyJson = JsonKit.toJsonString(jsonMap, true);
```

**More samples:**
[Complete JsonSample](./fs-tests/src/samples/java/internal/samples/JsonSample.java)

### Networking

#### TcpSample

Demonstrates TCP server and client implementation using TcpServer and TcpClient for network communication.

**Basic TCP server usage:**
```java
TcpServer tcpServer = TcpServer.builder()
    .address(8080)
    .handler(new TcpServerHandler() {
        @Override
        public void channelRead(TcpServerHandlerContext ctx, ByteBuffer msg) {
            ctx.writeAndFlush(msg);
        }
    })
    .build();
tcpServer.start();
```

**More samples:**
[Complete TcpSample](./fs-tests/src/samples/java/internal/samples/TcpSample.java)

#### UdpSample

Demonstrates UDP server and sender implementation using UdpServer and UdpSender for datagram communication.

**Basic UDP usage:**
```java
UdpSender udpSender = UdpSender.builder().build();
udpSender.sendData(remoteAddress, data);
```

**More samples:**
[Complete UdpSample](./fs-tests/src/samples/java/internal/samples/UdpSample.java)

#### NetSample

Demonstrates HTTP client and other network utilities including HttpCaller for HTTP requests.

**Basic HTTP usage:**
```java
HttpCaller caller = HttpCaller.of("https://api.example.com");
String response = caller.get("/endpoint").execute().body();
```

**More samples:**
[Complete NetSample](./fs-tests/src/samples/java/internal/samples/NetSample.java)

### Object Manipulation

#### ObjectSamples

Demonstrates object utilities including ObjectConverter for type conversion, ObjectCopier for property copying, and Fs.getValue for dynamic property access.

**Basic object conversion:**
```java
DataTo dataTo = ObjectConverter.defaultConverter()
    .convert(dataFrom, DataTo.class);
```

**Basic property copying:**
```java
ObjectCopier.defaultCopier().copyProperties(source, target);
```

**More samples:**
[Complete ObjectSamples](./fs-tests/src/samples/java/internal/samples/ObjectSamples.java)

#### SchemaSample

Demonstrates object schema definition and parsing for type information and validation.

**Basic schema usage:**
```java
ObjectSchema schema = ObjectSchema.parse(Person.class);
ObjectProperty idProperty = schema.getProperty("id");
Class<?> type = idProperty.type();
```

**More samples:**
[Complete SchemaSample](./fs-tests/src/samples/java/internal/samples/SchemaSample.java)

#### CopierSample

Demonstrates advanced object property copying with ObjectCopier including different copy strategies.

**Basic copying usage:**
```java
Copier copier = Copier.of(Source.class, Target.class);
Target copied = copier.copy(source);
```

**More samples:**
[Complete CopierSample](./fs-tests/src/samples/java/internal/samples/CopierSample.java)

#### ConvertSample

Demonstrates object conversion utilities including converting between maps and objects.

**Basic conversion usage:**
```java
Converter converter = Converter.of(Source.class, Target.class);
Target converted = converter.convert(source);
```

**More samples:**
[Complete ConvertSample](./fs-tests/src/samples/java/internal/samples/ConvertSample.java)

### Dynamic Programming

#### ProxySample

Demonstrates dynamic proxy creation using JDK proxy and ASM-based implementations.

**Basic proxy usage:**
```java
ProxyFactory factory = ProxyFactory.of(Interface.class);
Interface proxy = factory.create(invocationHandler);
```

**More samples:**
[Complete ProxySample](./fs-tests/src/samples/java/internal/samples/ProxySample.java)

#### AspectSample

Demonstrates aspect-oriented programming support with aspects and advice definitions.

**Basic aspect usage:**
```java
Aspect aspect = Aspect.of(
    pointcut,
    Advice.before(() -> System.out.println("Before"))
);
```

**More samples:**
[Complete AspectSample](./fs-tests/src/samples/java/internal/samples/AspectSample.java)

### Dependency Injection

#### DISample

Demonstrates lightweight dependency injection container with component lifecycle management.

**Basic DI usage:**
```java
Container container = Container.create();
container.register(Component.class);
Component component = container.get(Component.class);
```

**More samples:**
[Complete DISample](./fs-tests/src/samples/java/internal/samples/DISample.java)

### Utilities

#### CacheSample

Demonstrates SimpleCache for lightweight caching with different reference types and custom settings.

**Basic cache usage:**
```java
SimpleCache<String, String> cache = SimpleCache.ofSoft();
String value = cache.get("key", k -> "computed value");
```

**More samples:**
[Complete CacheSample](./fs-tests/src/samples/java/internal/samples/CacheSample.java)

#### EventBusSample

Demonstrates event bus interfaces for publish-subscribe communication between components.

**Basic event bus usage:**
```java
EventBus eventBus = EventBus.create();
eventBus.subscribe(subscriber);
eventBus.publish(event);
```

**More samples:**
[Complete EventBusSample](./fs-tests/src/samples/java/internal/samples/EventBusSample.java)

#### JdbcSample

Demonstrates JDBC utilities for database operations and SQL handling.

**Basic JDBC usage:**
```java
JdbcTemplate template = JdbcTemplate.of(dataSource);
List<Map<String, Object>> results = template.query("SELECT * FROM table");
```

**More samples:**
[Complete JdbcSample](./fs-tests/src/samples/java/internal/samples/JdbcSample.java)

### Annotations

#### AnnotationSample

Demonstrates fs-annotation module annotations including @Nonnull, @Nullable, @Immutable for null safety and code analysis.

**Basic annotation usage:**
```java
public void process(@Nonnull String input, @Nullable String optional) {
    // Input is guaranteed non-null, optional may be null
}
```

**More samples:**
[Complete AnnotationSample](./fs-tests/src/samples/java/internal/samples/AnnotationSample.java)

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