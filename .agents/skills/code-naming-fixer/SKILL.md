---
name: "code-naming-fixer"
description: "Fix incorrect naming in code by ensuring variable/parameter names are semantically related to their declared types."
---

# Code Naming Fixer Skill

This skill is designed to fix incorrect naming in code by ensuring variable and parameter names are semantically related to their declared types. The goal is to improve code readability and maintainability by making names more descriptive and consistent.

**Important Note**: Examples like `ObjectMetaIntrospector`, `schema`, or `manager` used throughout this document are for illustration purposes only and should not be treated as fixed rules. The actual naming decisions depend on the specific context and codebase conventions.

## Core Principles

1. **Semantic Consistency**: Variable/parameter names should reflect their type's purpose and semantics
2. **CamelCase Convention**: Follow language-specific naming conventions (e.g., camelCase for Java/Python)
3. **Context Awareness**: Consider the broader context when evaluating naming relevance - adjust name specificity based on what other variables exist in the same scope
4. **Preserve Intent**: When a name is genuinely relevant to the context, preserve it

## Process

### Step 1: Identify Type Declarations

First, scan the code to identify all type declarations including:
- Classes
- Interfaces
- Enums
- Type aliases
- Generic type parameters

Extract the base semantic meaning from the type name. For example:
- `ObjectMetaIntrospector` → core meanings: "object", "meta", "introspector"
- `UserRepository` → core meanings: "user", "repository"
- `DataProcessor` → core meanings: "data", "processor"

### Step 2: Extract Variable/Parameter Declarations

For each type declaration, find all variables and parameters declared with that type. Record:
- The declared type
- The current name
- The context (method, class, usage pattern)
- Any existing naming patterns in the codebase

### Step 3: Analyze Name-Type Relationship

Evaluate each variable/parameter name against its declared type using the following criteria:

#### Criteria A: Direct Semantic Match
- Check if the name contains any part of the type's semantic components
- Example: Type `ObjectMetaIntrospector` with name `introspector` → MATCH

#### Criteria B: Contextual Relevance
- Determine if the current name has independent semantic value in the given context
- Example: Name `schema` in a method dealing with schema operations → RELEVANT
- Example: Name `manager` in a class managing resources → RELEVANT

#### Criteria C: Generic Naming Check
- Identify overly generic names like `obj`, `data`, `item`, `value`
- These should be replaced with more specific names derived from the type

### Step 4: Generate Recommended Names

Based on the analysis, generate potential replacement names following these patterns. The choice depends on context, particularly whether there are other similar types in the same scope.

#### Pattern 1: Full Type Name (camelCase)
- Type: `ObjectMetaIntrospector` → Name: `objectMetaIntrospector`
- Type: `MapMetaIntrospector` → Name: `mapMetaIntrospector`
- Best for: When multiple similar types exist in the same scope and need differentiation

#### Pattern 2: Partial Type Name (Semantic Core)
- Type: `ObjectMetaIntrospector` → Name: `objectIntrospector` or `metaIntrospector`
- Best for: When partial differentiation is needed (e.g., distinguishing `objectIntrospector` from `mapIntrospector`)

#### Pattern 3: Minimal Semantic Name
- Type: `ObjectMetaIntrospector` → Name: `introspector`
- Best for: When only one variable of this type (or related types) exists in the scope - no need for additional qualifiers

#### Pattern 4: Role-Based Name
- Type: `ObjectMetaIntrospector` in a validation context → Name: `validator`
- Best for: When the variable serves a specific role that's clearer than the type name

### Step 5: Evaluate Edge Cases

Special considerations for ambiguous situations:

#### Case 1: Generic Container Types
- `List<User>` → Could be `userList`, `users`, or domain-specific like `activeUsers`
- Consider the usage pattern and existing codebase conventions

#### Case 2: Multiple Instances of Same Type
- Use distinguishing suffixes: `primaryIntrospector`, `secondaryIntrospector`
- Or use descriptive prefixes: `inputIntrospector`, `outputIntrospector`

#### Case 3: Framework/Pattern Specific Names
- Spring: `@Autowired UserRepository userRepository` → Keep as-is (convention)
- Builder pattern: `UserBuilder userBuilder` → Keep as-is (pattern convention)

### Step 6: Apply Fixes

Apply the recommended naming changes while preserving:
- Language-specific naming conventions
- Existing codebase style patterns
- Semantic correctness
- Backwards compatibility (when applicable)

#### Step 6.1: Update Variable/Parameter Names
Replace the old names with the recommended new names throughout the code.

#### Step 6.2: Synchronize Related Comments
When renaming a variable or parameter, check all related comments and update them accordingly:
- Javadoc comments
- Line comments
- Block comments
- Documentation strings

Example: If `ObjectMeta objSchema` is renamed to `objMeta`, update comments:
- `/** The schema info */` → `/** The meta info */`
- `// process schema data` → `// process meta data`

#### Step 6.3: Update References
Ensure all references to the renamed variable/parameter are updated:
- Method calls
- Field accesses
- Return statements
- Any other usages

### Step 7: Review and Validate

After applying fixes:
1. Check for naming consistency across the codebase
2. Verify no compilation errors were introduced
3. Ensure the new names improve readability
4. Get peer review for subjective naming decisions

## Decision Flowchart

```
Is the current name semantically related to the type?
    ↓ Yes → Keep the name (no change needed)
    ↓ No
        Is the name contextually relevant (e.g., schema, manager)?
            ↓ Yes → Keep the name if genuinely meaningful
            ↓ No
                Generate replacement names from type semantics
                Apply the most appropriate name
```

## Best Practices

1. **Be Consistent**: Follow existing naming patterns in the codebase
2. **Avoid Abbreviations**: Prefer `introspector` over `introspt`
3. **Use Domain Language**: When appropriate, use domain-specific terms
4. **Keep It Concise**: Prefer `introspector` over `objectMetaIntrospector` when context is clear
5. **Consider Scope**: Shorter names are acceptable in small scopes (e.g., loop variables)

## Examples

For detailed examples covering various scenarios, see [examples.md](./examples.md).
