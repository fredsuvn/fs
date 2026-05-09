---
name: "generate-dev-doc"
description: "This skill generates comprehensive developer documentation for Java/Kotlin projects, providing guidelines for code contributors and developers."
---

# Generate Developer Documentation Skill

This skill creates well-structured, professional developer documentation for Java/Kotlin projects. The goal is to help code contributors understand the project's architecture, development workflows, and coding standards.

## Key Principles

1. **Comprehensive Coverage**: Cover all essential aspects of project development
2. **Clear Structure**: Use hierarchical organization with proper headings
3. **Practical Guidance**: Include actionable instructions and examples
4. **Consistency**: Follow the project's existing conventions and patterns
5. **Up-to-Date**: Keep documentation in sync with codebase changes

## Documentation Structure

### 1. Document Header

Start with proper AsciiDoc header:
```asciidoc
= Project Development Manual
:doctype: book
:toc: macro
:toclevels: 3
```

### 2. Quick Start Section

Help new contributors get started quickly:
- Environment requirements
- Code cloning instructions
- First build commands
- Test execution
- Common issues (JDK config, proxy, etc.)

### 3. Project Architecture Overview

Provide high-level understanding of the project:
- Module dependency graph (Mermaid diagram)
- Key module relationships
- Multi-version JDK support principles

### 4. Development Workflow

Define collaboration practices:
- Branch strategy
- Commit message format
- Code review process

### 5. Coding Standards

Establish code quality guidelines:
- Naming conventions
- Annotation usage rules (@Nonnull, @Nullable, etc.)
- Exception handling best practices

### 6. Testing Guidelines

Define testing requirements:
- Unit testing standards
- Integration testing practices
- Benchmark testing procedures
- Coverage requirements

### 7. Build & Release

Document build and deployment processes:
- Gradle task reference
- Multi-JDK build configuration
- Local release and verification
- Maven Central release workflow

### 8. FAQ Section

Address common developer questions:
- How to add new utilities
- How to support new JDK versions
- Module inclusion/exclusion rules

## Process

### Step 1: Gather Project Information

Collect essential project details:
- Project name and description
- Module structure
- Build system configuration
- Existing documentation
- Coding conventions
- CI/CD setup

### Step 2: Define Document Structure

Create the outline based on the standard sections:
```asciidoc
= Project Development Manual
:doctype: book
:toc: macro

== Quick Start
=== Environment Requirements
=== Clone Repository
=== First Build
=== Run Tests
=== Common Issues

== Project Architecture Overview
=== Module Dependency Graph
=== Multi-Version JDK Support

== Development Workflow
=== Branch Strategy
=== Commit Standards
=== Code Review Process

== Coding Standards
=== Naming Conventions
=== Annotation Usage
=== Exception Handling

== Testing Guidelines
=== Unit Testing
=== Integration Testing
=== Benchmark Testing
=== Coverage Requirements

== Build & Release
=== Gradle Tasks
=== Multi-JDK Build
=== Local Release
=== Maven Central Release

== FAQ
```

### Step 3: Write Quick Start Section

Provide clear getting started instructions:
```asciidoc
== Quick Start

=== Environment Requirements

* JDK 8+ (for compilation)
* Gradle 9+
* Git

=== Clone Repository

[source,bash]
----
git clone https://github.com/username/project.git
cd project
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

Ensure JDK versions are properly configured in `gradle.properties`:

[source,properties]
----
org.gradle.java.installations.paths=\
  ~/.jdks/azul-1.8.0_462,\
  ~/.jdks/azul-17.0.17
----

==== Proxy Configuration

Set proxy environment variables if needed:

[source,bash]
----
export HTTP_PROXY=http://proxy.example.com:8080
export HTTPS_PROXY=http://proxy.example.com:8080
----
```

### Step 4: Document Architecture

Describe the module structure:
```asciidoc
== Project Architecture Overview

=== Module Dependency Graph

[graphviz]
----
digraph G {
    rankdir=TB;
    node [shape=box];

    "fs-jsr305" -> "fs-annotation";
    "fs-annotation" -> "fs-core";
    "fs-asm" -> "fs-core";
    "fs-core" -> "fs-all";
}
----

=== Multi-Version JDK Support

The project uses `FsLoader` to load appropriate implementations based on runtime JDK version:

[source,java]
----
interface Service {
    @Nonnull
    Service INST = FsLoader.loadImplByJvm(Service.class, 11);
    // Implementation selected based on JDK version
}
----
```

### Step 5: Define Development Workflow

Document collaboration practices:
```asciidoc
== Development Workflow

=== Branch Strategy

* `main`: Main development branch
* `release/*`: Release preparation branches
* `feature/*`: Feature development branches
* `fix/*`: Bug fix branches

=== Commit Standards

Follow the conventional commit format:

[source]
----
<type>(<scope>): <description>

[optional body]

[optional footer]
----

Types:
* `feat`: New feature
* `fix`: Bug fix
* `docs`: Documentation update
* `refactor`: Code refactoring
* `test`: Test updates

=== Code Review Process

1. Create feature branch from `main`
2. Implement changes with tests
3. Submit pull request
4. Request review from at least one maintainer
5. Address feedback
6. Maintainer merges when approved
```

### Step 6: Specify Coding Standards

Define code quality requirements:
```asciidoc
== Coding Standards

=== Naming Conventions

* Classes: PascalCase
* Methods: camelCase
* Variables: camelCase
* Constants: UPPER_SNAKE_CASE
* Packages: lowercase

=== Annotation Usage

* `@Nonnull`: Required for non-null return values and parameters
* `@Nullable`: Required for nullable return values and parameters
* `@Immutable`: For immutable value classes
* `@ThreadSafe`: For thread-safe classes

[source,java]
----
public void process(
    @Nonnull String required,
    @Nullable String optional
) {
    // Implementation
}
----

=== Exception Handling

* Use specific exception types
* Provide meaningful error messages
* Handle checked exceptions appropriately
```

### Step 7: Document Testing Guidelines

Define testing requirements:
```asciidoc
== Testing Guidelines

=== Unit Testing

* Use JUnit 5 for unit tests
* Place tests in `src/test/java/tests/`
* Test all public methods

=== Integration Testing

* Test cross-module interactions
* Use separate test resources when needed

=== Benchmark Testing

* Use JMH for performance benchmarks
* Place benchmarks in dedicated module
* Include before/after performance metrics

=== Coverage Requirements

* Maintain 100% test coverage
* Use JaCoCo for coverage reporting
* Fix coverage gaps before merging
```

### Step 8: Document Build & Release

Describe build processes:
```asciidoc
== Build & Release

=== Gradle Tasks

| Task | Description |
|------|-------------|
| `build` | Build all modules |
| `test` | Run unit tests |
| `jmh` | Run benchmarks |
| `javadoc` | Generate documentation |
| `publishToMavenLocal` | Publish to local Maven |

=== Multi-JDK Build

Add new JDK versions in `gradle.properties`:

[source,properties]
----
org.gradle.java.installations.paths=\
  ~/.jdks/azul-1.8.0_462,\
  ~/.jdks/azul-17.0.17,\
  ~/.jdks/openjdk-21
----

=== Local Release

[source,bash]
----
./gradlew clean build publishToMavenLocal
----

=== Maven Central Release

1. Update version in `gradle.properties`
2. Run `./gradlew publish`
3. Close and release in Sonatype Nexus
```

### Step 9: Create FAQ Section

Address common questions:
```asciidoc
== FAQ

=== How to add a new utility class?

1. Place in appropriate package under `fs-core/src/main/java`
2. Follow naming conventions (e.g., `XxxKit`)
3. Add unit tests
4. Update documentation

=== How to support a new JDK version?

1. Add JDK path to `gradle.properties`
2. Create version-specific implementation using `FsLoader`
3. Add tests for the new version
4. Update build configuration

=== Why some classes are in fs-core but not in fs-all?

`fs-all` contains only essential modules. Internal utilities or experimental features may be excluded.
```

## Best Practices

1. **Keep It Updated**: Regularly update documentation when code changes
2. **Be Specific**: Provide concrete examples where applicable
3. **Use Visuals**: Include diagrams for complex relationships
4. **Link References**: Cross-reference related documentation
5. **Review Regularly**: Conduct periodic documentation reviews

## Examples

For detailed examples and templates, see [examples.md](./examples.md).
