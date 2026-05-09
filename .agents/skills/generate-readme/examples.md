# Generate README Examples

This document provides practical examples for creating README files using the Generate README skill.

---

## Example 1: Complete README Template

```markdown
# fs

[![Build Status](https://img.shields.io/github/actions/workflow/status/sunqian/fs/test.yml)](https://github.com/sunqian/fs/actions)
[![Coverage](https://img.shields.io/codecov/c/github/sunqian/fs)](https://codecov.io/gh/sunqian/fs)
[![License](https://img.shields.io/github/license/sunqian/fs)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/space.sunqian/fs-all)](https://search.maven.org/artifact/space.sunqian/fs-all)

A comprehensive utility library for Java 21+ that simplifies common development tasks with modern APIs.

✨ **Core Features:**
- 🚀 **Modern API**: Built for Java 21 with virtual threads support
- 📦 **Zero Dependencies**: Standalone library with no external dependencies
- ⚡ **High Performance**: Optimized for throughput and low latency
- 🔧 **Rich Utilities**: Covers I/O, networking, reflection, and more
- 🧩 **Modular Design**: Pick only what you need

---

## Why fs?

Unlike traditional utility libraries, fs embraces modern Java features:

- **Virtual Threads Ready**: Native support for Project Loom
- **Value Types**: Leverages Java 21 value classes for memory efficiency
- **Pattern Matching**: Uses sealed classes and pattern matching
- **Clean Architecture**: No static singletons, fully injectable

---

## Quick Start

```java
// Add dependency
// Maven: <groupId>space.sunqian</groupId><artifactId>fs-all</artifactId><version>0.1.0</version>

import space.sunqian.fs.Fs;

public class QuickStart {
    public static void main(String[] args) {
        // Initialize fs
        Fs.init();
        
        // Simple file operations
        String content = Fs.io().file().readString("data.txt");
        System.out.println(content);
        
        // HTTP request in one line
        String response = Fs.net().http().get("https://api.example.com/data");
        System.out.println(response);
    }
}
```

---

## Module Overview

| Module | Description |
|--------|-------------|
| `fs-core` | Core utilities: collections, io, net, reflect, etc. |
| `fs-annotation` | Annotations for null safety and code validation |
| `fs-asm` | Bytecode manipulation (internal use) |
| `fs-all` | Aggregator module with all dependencies |

---

## Documentation

- [JavaDoc](https://javadoc.io/doc/space.sunqian/fs-core)
- [User Guide](docs/user-guide.adoc)
- [Developer Guide](docs/dev/dev.adoc)
- [API Reference](https://sunqian.space/fs/api)

---

## Build & Contribute

**Requirements:**
- JDK 21 or later
- Gradle 8.5+

**Build:**
```bash
./gradlew clean build
```

**Run Tests:**
```bash
./gradlew test
```

**Generate Javadoc:**
```bash
./gradlew javadoc
```

**Contributing:**
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Make changes and add tests
4. Submit a pull request

---

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.
```

---

## Example 2: Minimal README (Small Project)

```markdown
# my-tool

[![Build](https://img.shields.io/github/actions/workflow/status/user/my-tool/test.yml)](https://github.com/user/my-tool/actions)
[![License](https://img.shields.io/github/license/user/my-tool)](LICENSE)

A lightweight command-line tool for processing CSV files.

**Features:**
- Fast CSV parsing
- Memory-efficient processing
- Support for large files (>1GB)

## Quick Start

```bash
# Install
brew install user/tap/my-tool

# Usage
my-tool input.csv --output result.json
```

## Usage

```java
// Programmatic usage
MyTool.process("input.csv", "result.json");
```

## License

MIT License
```

---

## Example 3: README with Code Snippets

```markdown
# json-utils

[![Build](https://img.shields.io/github/actions/workflow/status/user/json-utils/test.yml)](https://github.com/user/json-utils/actions)
[![Maven Central](https://img.shields.io/maven-central/v/com.example/json-utils)](https://search.maven.org)

Fast and simple JSON processing library for Java.

**Key Features:**
- 🚀 Blazing fast parsing
- 📊 Stream-based processing
- 🔒 Type-safe APIs

## Quick Start

```java
import com.example.json.Json;

// Parse JSON
JsonData data = Json.parse("{\"name\": \"test\"}");
String name = data.get("name").asString();

// Generate JSON
String json = Json.object()
    .put("key", "value")
    .put("number", 42)
    .toString();
```

## Performance

| Operation | json-utils | Gson | Jackson |
|-----------|------------|------|---------|
| Parse 1MB | 23ms | 45ms | 38ms |
| Stringify | 18ms | 32ms | 28ms |

## License

Apache 2.0
```

---

## Example 4: README for Library with Extensions

```markdown
# web-framework

[![Build](https://img.shields.io/github/actions/workflow/status/user/web-framework/test.yml)](https://github.com/user/web-framework/actions)
[![Coverage](https://img.shields.io/codecov/c/github/user/web-framework)](https://codecov.io)
[![License](https://img.shields.io/github/license/user/web-framework)](LICENSE)

A modern web framework built for Java 21+.

**Features:**
- 🌐 HTTP/2 support
- 🧵 Virtual threads
- 🔌 Extensible architecture
- 📱 RESTful APIs made easy

## Quick Start

```java
import com.example.web.WebApp;

public class App {
    public static void main(String[] args) {
        WebApp.create()
            .get("/hello", ctx -> ctx.send("Hello World!"))
            .get("/users/{id}", ctx -> {
                String id = ctx.pathParam("id");
                return ctx.json(findUser(id));
            })
            .start(8080);
    }
}
```

## Extensions

| Extension | Description |
|-----------|-------------|
| `web-jackson` | JSON serialization with Jackson |
| `web-mustache` | Mustache template support |
| `web-security` | Authentication and authorization |

## Documentation

- [Getting Started](docs/getting-started.md)
- [API Reference](https://user.github.io/web-framework/api)
- [Examples](examples/)

## Build

```bash
mvn clean install
```

## License

MIT
```
