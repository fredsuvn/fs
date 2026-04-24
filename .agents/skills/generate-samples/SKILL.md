---
name: "generate-samples"
description: "This skill is designed to generate and maintain examples for using the fs library."
---

# Generate Samples Skill

This skill is designed to generate and maintain examples for using the fs library. This skill helps users understand how to effectively use the fs library by providing clear, concise, and well-structured sample code.

## Key Modules

The fs library consists of several core modules:

1. **fs-core**: The main library with comprehensive utilities and interfaces, including almost all tool classes and interfaces of fs.
2. **fs-annotation**: Contains almost all auxiliary annotations of fs for code analysis and validation.
3. **fs-asm**: Built-in ASM framework for bytecode manipulation, only used for bytecode programming, and already encapsulated by the ASM implementation in fs-core, so it is rarely used directly.

## Process

### 1. Study the Documentation

Before creating samples, carefully read the documentation and comments of the above modules to understand the basic usage of fs. This includes:

- Javadoc comments in the source code, especially the comments of package-infos;
- Interface comments and structures;
- Any other relevant documentation and codes in the project.

### 2. Sample Location

Sample code should be placed in the source set: `fs-tests/src/samples`. This source set already contains some existing samples that need to be refactored, supplemented, and improved.

### 3. Refactor and Enhance Existing Samples

Review the existing sample files and:

- Improve code readability and documentation
- Ensure samples cover the most common use cases
- Fix any outdated or incorrect code
- Add missing examples for important features

### 4. Add New Samples

Add new sample files as needed to demonstrate:

- Core functionality of fs-core
- Usage of fs-annotation
- Advanced features and best practices

### 5. Update Documentation Links

Ensure the samples are referenced in the "Documents and Samples" section of the project's README.md file. This section can show snippets of sample code, with links to more detailed code in the repository.

## Sample Categories

Samples should cover the following categories:

1. **Base Utilities** (bytes, chars, date, string, system, etc.)
2. **Collections & Data** (arrays, lists, maps, JSON, properties)
3. **I/O Operations** (file operations, byte/char processing)
4. **Networking** (HTTP, TCP, UDP)
5. **Object Manipulation** (conversion, copying, schema)
6. **Dynamic Programming** (proxy, aspect, reflection)
7. **Dependency Injection**
8. **Utilities** (codec, event bus, JDBC)
9. **Annotations** (null safety, immutability, etc.)
10. **Caching**

## Best Practices

- Keep samples simple and flexible
- Include clear comments explaining the code
- Provide both basic and advanced usage examples
- Ensure samples are well-tested and functional
- Each sample class only mainly demonstrates usages and features for one class/interface/package (or related classes/interfaces)
- Using English comments for the samples

## Output Format

When creating or updating samples, ensure they follow this structure:

1. Package declaration
2. Imports
3. Class definition with descriptive name
4. Well-commented methods demonstrating specific features
5. Clear examples of usage patterns

## Linking with Documentation

The README.md's "Documents and Samples" section should include:

- Brief descriptions of each sample
- Links to the sample files
- Code snippets for key examples with explanations

## Sample Code Templates

When creating a new sample, follow this structure to ensure consistency and clarity. See [examples.md](./examples.md) for detailed examples.

### Step 1: Define the Sample Purpose

At the beginning of each sample file, include a comment block that clearly states:

- What this sample demonstrates
- Why this feature is useful
- Key use cases

### Step 2: Provide Multiple Implementation Variations

Show different ways to generate/use/depende the feature, from simple to complex:

### Step 3: Update README.md

When updating the README.md "Documents and Samples" section, add short snippets of the sample code to the README.md section, and add links to the complete sample files.

## Example: Complete Sample Creation Flow

For a complete example demonstrating the sample creation flow including:

1. Creating the Sample File
2. Updating README.md with snippets and links

Please refer to [examples.md](./examples.md).

## Conclusion

The GenerateSamples skill aims to provide high-quality, well-documented examples that demonstrate the power and versatility of the fs library. By following this guide, you can generate samples that help users quickly get started with fs and make the most of its features.