# Generate Developer Documentation Examples

This document provides practical examples for creating developer documentation using the Generate Developer Documentation skill.

---

## Example 1: Complete Developer Manual Template

```asciidoc
= fs Developer Manual
:doctype: book
:toc: macro
:toclevels: 3
:last-update-label!:

== Quick Start

=== Environment Requirements

* JDK 8+ (for compilation)
* Gradle 9+
* Git 2.30+

=== Clone Repository

[source,bash]
----
git clone https://github.com/fredsuvn/fs.git
cd fs
----

=== First Build

[source,bash]
----
./gradlew clean build
----

=== Run Tests

[source,bash]
----
./gradlew test
----

=== Common Issues

==== JDK Configuration

Ensure multiple JDK versions are configured:

[source,properties]
----
org.gradle.java.installations.paths=\
  ~/.jdks/azul-1.8.0_462,\
  ~/.jdks/azul-17.0.17,\
  ~/.jdks/openjdk-21
----

==== Proxy Configuration

Set proxy for Gradle in `gradle.properties`:

[source,properties]
----
systemProp.http.proxyHost=proxy.example.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.example.com
systemProp.https.proxyPort=8080
----

== Project Architecture Overview

=== Module Dependency Graph

[graphviz]
----
digraph G {
    rankdir=TB;
    node [shape=box, style="rounded,filled", fillcolor="#f0f8ff"];

    "fs-jsr305" [fillcolor="#e6f7ff"];
    "fs-annotation" [fillcolor="#fff7e6"];
    "fs-asm" [fillcolor="#f6ffed"];
    "fs-core" [fillcolor="#fff1b8"];
    "fs-all" [fillcolor="#ffecd2"];

    "fs-jsr305" -> "fs-annotation";
    "fs-annotation" -> "fs-core";
    "fs-asm" -> "fs-core";
    "fs-core" -> "fs-all";
    "fs-jsr305" -> "fs-all";
    "fs-annotation" -> "fs-all";
    "fs-asm" -> "fs-all";
}
----

=== Multi-Version JDK Support Principle

The project dynamically loads implementations based on runtime JDK version:

[source,java]
----
public interface HttpCallerService {

    @Nonnull
    HttpCallerService INST = FsLoader.loadImplByJvm(HttpCallerService.class, 11);

    @Nonnull
    HttpCaller newCaller(
        int bufSize,
        @Nonnull Proxy proxy
    ) throws HttpNetException;
}
----

== Development Workflow

=== Branch Strategy

* `main`: Main development branch (protected)
* `release/x.y.z`: Release preparation branches
* `feature/xxx`: Feature development branches
* `fix/xxx`: Bug fix branches
* `docs/xxx`: Documentation updates

=== Commit Standards

[source]
----
<type>(<scope>): <short description>

[optional detailed description]

[optional footer with issue references]
----

| Type | Description |
|------|-------------|
| `feat` | New feature implementation |
| `fix` | Bug fix |
| `docs` | Documentation update |
| `refactor` | Code refactoring |
| `test` | Test additions/updates |
| `build` | Build system changes |
| `chore` | Maintenance tasks |

=== Code Review Process

1. Fork the repository
2. Create feature branch: `git checkout -b feature/my-feature`
3. Implement changes with unit tests
4. Run `./gradlew clean build test`
5. Submit pull request with clear description
6. Request review from at least one maintainer
7. Address review feedback
8. Maintainer merges when all checks pass

== Coding Standards

=== Naming Conventions

* **Classes/Interfaces**: PascalCase
  * `StringKit`, `ObjectMeta`, `ProxyMaker`
* **Methods**: camelCase
  * `formatDate()`, `createProxy()`
* **Variables**: camelCase
  * `byteReader`, `objectMeta`
* **Constants**: UPPER_SNAKE_CASE
  * `MAX_BUFFER_SIZE`, `DEFAULT_TIMEOUT`
* **Packages**: lowercase with dots
  * `space.sunqian.fs.core.io`

=== Annotation Usage

* `@Nonnull`: Required for parameters/returns that cannot be null
* `@Nullable`: Required for parameters/returns that can be null
* `@Immutable`: For immutable value objects
* `@ThreadSafe`: For thread-safe classes
* `@NonExported`: For non-exported packages

[source,java]
----
public class ExampleService {

    @Nonnull
    public String process(
        @Nonnull String requiredParam,
        @Nullable String optionalParam
    ) {
        // Implementation
        return result;
    }
}
----

=== Exception Handling

* Use specific exception types from `fs-core`
* Provide meaningful error messages
* Wrap checked exceptions with `FsRuntimeException` when appropriate

[source,java]
----
public void readFile(@Nonnull String path) {
    try {
        // IO operation
    } catch (IOException e) {
        throw new FsRuntimeException("Failed to read file: " + path, e);
    }
}
----

== Testing Guidelines

=== Unit Testing

* Use JUnit 5 with AssertJ
* Place tests in `src/test/java/tests/`
* Test all public APIs

[source,java]
----
@ExtendWith(FsExtension.class)
class StringKitTest {

    @Test
    void isEmpty_shouldReturnTrue_forNull() {
        assertTrue(StringKit.isEmpty(null));
    }

    @Test
    void isEmpty_shouldReturnFalse_forNonEmpty() {
        assertFalse(StringKit.isEmpty("test"));
    }
}
----

=== Integration Testing

* Test cross-module interactions
* Use `@Tag("integration")` for integration tests
* Run separately from unit tests

[source,bash]
----
./gradlew test --tests "*IntegrationTest"
----

=== Benchmark Testing

* Use JMH for performance benchmarks
* Place in `fs-tests` module
* Include baseline comparisons

[source,java]
----
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class StringBenchmark {

    @Benchmark
    public void benchmarkStringConcat() {
        String result = "Hello" + "World";
    }
}
----

=== Coverage Requirements

* Maintain 100% line coverage
* Exclude only non-testable code with `@Generated`
* Run coverage check before PR

[source,bash]
----
./gradlew jacocoTestReport
----

== Build & Release

=== Gradle Tasks

| Task | Description |
|------|-------------|
| `build` | Build all modules |
| `test` | Run unit tests |
| `jmh` | Run JMH benchmarks |
| `javadoc` | Generate aggregated Javadoc |
| `jacocoTestReport` | Generate coverage report |
| `publishToMavenLocal` | Publish to local Maven repo |
| `publish` | Publish to Maven Central |

=== Multi-JDK Build

Add new JDK version:

1. Install JDK locally
2. Update `gradle.properties`:
   [source,properties]
   ----
   org.gradle.java.installations.paths=\
     ~/.jdks/azul-1.8.0_462,\
     ~/.jdks/azul-17.0.17,\
     ~/.jdks/openjdk-21,\
     ~/.jdks/openjdk-25
   ----
3. Add version-specific implementation using `FsLoader`

=== Local Release

[source,bash]
----
./gradlew clean build publishToMavenLocal
----

Verify in local Maven repo: `~/.m2/repository/space/sunqian/fs/`

=== Maven Central Release

1. Update version in `gradle.properties`:
   [source,properties]
   ----
   version=1.0.0
   ----
2. Configure signing credentials in `~/.gradle/gradle.properties`:
   [source,properties]
   ----
   signing.keyId=ABC123
   signing.password=your-password
   signing.secretKeyRingFile=/path/to/secring.gpg
   ----
3. Run: `./gradlew publish`
4. Close and release in Sonatype Nexus Repository Manager

== FAQ

=== How to add a new utility class?

1. Place in appropriate package under `fs-core/src/main/java`
2. Follow naming convention (e.g., `XxxKit`)
3. Add Javadoc for all public methods
4. Write unit tests covering all cases
5. Update this documentation if needed

=== How to support a new JDK version?

1. Install the new JDK version
2. Add to `gradle.properties`
3. Create version-specific implementation class
4. Use `FsLoader.loadImplByJvm()` to select at runtime
5. Add tests for the new version
6. Update CI workflow if needed

=== Why some classes are in fs-core but not in fs-all?

`fs-all` aggregates only stable, production-ready modules.
- Internal utilities may be excluded
- Experimental features may be excluded
- ASM classes are included but under isolated package

=== How to contribute to documentation?

1. Fork the repository
2. Create `docs/xxx` branch
3. Update `docs/dev/dev.adoc`
4. Submit pull request
5. Get review and merge
```

---

## Example 2: Minimal Developer Guide (Small Project)

```asciidoc
= MyProject Developer Guide
:doctype: book
:toc: macro

== Quick Start

[source,bash]
----
git clone https://github.com/user/myproject.git
cd myproject
./mvnw clean install
----

== Coding Standards

* Classes: PascalCase
* Methods: camelCase
* Variables: camelCase
* Constants: UPPER_SNAKE_CASE

== Testing

[source,bash]
----
./mvnw test
./mvnw jacoco:report
----

== Release

[source,bash]
----
./mvnw release:prepare
./mvnw release:perform
----
```

---

## Example 3: CI/CD Integration Documentation

```asciidoc
== CI/CD Workflow

=== GitHub Actions

[source,yaml]
----
name: CI

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: ./gradlew clean build test
      - uses: codecov/codecov-action@v4
        with:
          files: build/reports/jacoco/test/jacocoTestReport.xml
----

=== Build Matrix

[source,yaml]
----
strategy:
  matrix:
    java: [8, 11, 17, 21]
----
```
