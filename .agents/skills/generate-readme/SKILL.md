---
name: "generate-readme"
description: "This skill generates comprehensive README documentation for Java/Kotlin projects following best practices for structure and content."
---

# Generate README Skill

This skill creates well-structured, professional README documentation for Java/Kotlin projects. The goal is to help users quickly understand the project's purpose, features, and how to get started.

## Key Principles

1. **First-Screen Decision**: The README should enable readers to decide "whether it's worth continuing" within the first screen
2. **Concise Examples**: Code examples should be minimal yet impressive - "minimal but stunning"
3. **External Documentation**: Detailed samples should link to online docs or `/docs` directory; README only keeps 2-3 representative snippets
4. **Visual Communication**: Use tables and charts instead of long paragraphs whenever possible

## README Structure

### 1. Project Name & Badges

Start with the project name followed by status badges:
- Build status
- Test coverage
- License
- Maven Central (or other repository)

### 2. One-Liner Description & Core Selling Points

A concise description of the project, followed by 3-5 key selling points using emojis or bullet points for quick scanning.

### 3. Core Highlights (Differentiation)

Explain what makes this project unique compared to competitors and why users should choose it.

### 4. Quick Start (1-Minute Setup)

Provide minimal working code that users can copy-paste and run within 1 minute.

### 5. Module Overview

A simple table describing each module with one sentence.

### 6. Documentation Links

Links to:
- JavaDoc
- User manual
- Developer documentation
- API reference

### 7. Build & Contribution

- Environment requirements
- Build commands
- How to contribute

### 8. License

Standard license information.

## Process

### Step 1: Gather Project Information

Collect essential details:
- Project name and description
- Current build status
- License type
- Module structure
- Key features
- Documentation URLs

### Step 2: Write the Header Section

Create the project title with badges:
```markdown
# Project Name

[![Build Status](https://img.shields.io/github/actions/workflow/status/username/project/test.yml)](https://github.com/username/project/actions)
[![Coverage](https://img.shields.io/codecov/c/github/username/project)](https://codecov.io/gh/username/project)
[![License](https://img.shields.io/github/license/username/project)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.example/project)](https://search.maven.org/artifact/com.example/project)
```

### Step 3: Craft the Elevator Pitch

Write a compelling one-liner and 3-5 bullet points:
```markdown
A powerful utility library for Java/Kotlin that simplifies common development tasks.

✨ **Core Features:**
- Feature 1: Brief description
- Feature 2: Brief description
- Feature 3: Brief description
- Feature 4: Brief description
```

### Step 4: Highlight Differentiation

Explain what makes this project unique:
```markdown
## Why Choose Us?

- **Reason 1**: What differentiates from competitors
- **Reason 2**: Key advantage
- **Reason 3**: Unique value proposition
```

### Step 5: Create Quick Start Section

Provide minimal working code:
```markdown
## Quick Start

```java
// Add dependency
// Maven: <groupId>com.example</groupId><artifactId>project</artifactId><version>1.0.0</version>

import com.example.project.Fs;

public class App {
    public static void main(String[] args) {
        Fs.init();
        String result = Fs.string().format("Hello, %s!", "World");
        System.out.println(result);
    }
}
```
```

### Step 6: Build Module Overview Table

Create a concise table:
```markdown
## Module Overview

| Module | Description |
|--------|-------------|
| `module-core` | Core utilities and main functionality |
| `module-annotation` | Annotations for code analysis |
| `module-asm` | Bytecode manipulation utilities |
```

### Step 7: Add Documentation Links

Provide easy access to documentation:
```markdown
## Documentation

- [JavaDoc](https://javadoc.io/doc/com.example/project)
- [User Guide](docs/user-guide.adoc)
- [Developer Guide](docs/dev/dev.adoc)
- [API Reference](https://example.com/api)
```

### Step 8: Write Build & Contribution Section

Include practical information:
```markdown
## Build & Contribute

**Requirements:**
- JDK 21+
- Gradle 9+

**Build Command:**
```bash
./gradlew clean build
```

**Run Tests:**
```bash
./gradlew test
```

**Contributing:**
1. Fork the repository
2. Create a feature branch
3. Submit a pull request
```

### Step 9: Add License Information

```markdown
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
```

## Best Practices

1. **Keep it Scannable**: Use headings, emojis, and bullet points
2. **Up-to-Date Badges**: Ensure CI/CD badges reflect current status
3. **Working Examples**: Test all code snippets before publishing
4. **Clear Navigation**: Use table of contents for longer READMEs
5. **Consistent Formatting**: Follow markdown best practices

## Examples

For detailed examples and templates, see [examples.md](./examples.md).
