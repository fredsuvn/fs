# Code Naming Fixer Skill - Examples

This document provides detailed examples demonstrating various naming scenarios and their recommended fixes.

**Important Note**: All examples use `ObjectMetaIntrospector`, `schema`, `manager`, etc. as illustrative examples only. The actual naming decisions depend on the specific context and codebase conventions.

---

## Example 1: Basic Type-Name Mismatch

### Problem
```java
// Type declaration
public class ObjectMetaIntrospector {
    // ...
}

// Usage with incorrect naming
public class Service {
    private ObjectMetaIntrospector schema;  // ❌ "schema" doesn't relate to introspection
    private ObjectMetaIntrospector manager; // ❌ "manager" doesn't relate to introspection
    
    public void process() {
        ObjectMetaIntrospector obj = new ObjectMetaIntrospector(); // ❌ Too generic
    }
}
```

### Solution (Single type context)
```java
public class Service {
    // Only one introspector type in scope - use minimal name
    private ObjectMetaIntrospector introspector;  // ✅ Uses core semantic component
    
    public void process() {
        ObjectMetaIntrospector introspector = new ObjectMetaIntrospector(); // ✅ Minimal name suffices
    }
}
```

### Rationale
- `schema` → `introspector`: The type is about introspection, not schemas
- `manager` → `introspector`: The type doesn't manage anything
- `obj` → `introspector`: Generic names should be replaced with meaningful ones
- Since only one introspector type exists in this context, `introspector` is sufficient

---

## Example 2: Multi-Type Context (Multiple Introspectors)

### Problem
```java
// Multiple similar types exist
public class ObjectMetaIntrospector { /* ... */ }
public class MapMetaIntrospector { /* ... */ }

// Usage with insufficient differentiation
public class Service {
    private ObjectMetaIntrospector introspector;  // ❌ Ambiguous - which type?
    private MapMetaIntrospector introspector;     // ❌ Conflict - same name for different types
    
    public void process() {
        ObjectMetaIntrospector obj = new ObjectMetaIntrospector(); // ❌ Too generic
        MapMetaIntrospector map = new MapMetaIntrospector();       // ❌ Not descriptive enough
    }
}
```

### Solution
```java
public class Service {
    // Multiple introspector types exist - need differentiation
    private ObjectMetaIntrospector objectIntrospector;  // ✅ Clear differentiation
    private MapMetaIntrospector mapIntrospector;        // ✅ Clear differentiation
    
    public void process() {
        ObjectMetaIntrospector objectIntrospector = new ObjectMetaIntrospector(); // ✅ Clear
        MapMetaIntrospector mapIntrospector = new MapMetaIntrospector();         // ✅ Clear
    }
}
```

### Alternative Solution (Using full type name for maximum clarity)
```java
public class Service {
    private ObjectMetaIntrospector objectMetaIntrospector;  // ✅ Full type name
    private MapMetaIntrospector mapMetaIntrospector;        // ✅ Full type name
}
```

### Rationale
- When multiple similar types exist in the same context, minimal names like `introspector` become ambiguous
- Use qualifying prefixes (`object`, `map`) to differentiate between similar types
- The level of qualification depends on context: `objectIntrospector` vs `objectMetaIntrospector`

---

## Example 3: Contextually Relevant Names

### Scenario: Schema is actually relevant
```java
public class ObjectMetaIntrospector {
    private Schema schema;  // ✅ "schema" is relevant - this class uses a schema
    
    public Schema getSchema() {
        return schema;
    }
    
    public void analyze(Schema schema) {  // ✅ Parameter name matches property
        this.schema = schema;
    }
}
```

### Rationale
- `schema` is kept because the class actually deals with schemas as part of its function
- The name has independent semantic value in this context

### Scenario: Manager is actually relevant
```java
public class ObjectMetaIntrospectorManager {
    private List<ObjectMetaIntrospector> introspectors;
    
    public ObjectMetaIntrospector getIntrospector(String name) {
        // ...
    }
    
    public void register(ObjectMetaIntrospector introspector) {
        introspectors.add(introspector);
    }
}

// Usage
public class Service {
    private ObjectMetaIntrospectorManager manager;  // ✅ "manager" is relevant - it manages introspectors
}
```

---

## Example 4: Multiple Instances of Same Type

### Problem
```java
public class Processor {
    private ObjectMetaIntrospector introspector1;  // ❌ Unclear numbering
    private ObjectMetaIntrospector introspector2;  // ❌ Unclear numbering
    
    public void process(Data input, Data output) {
        introspector1.analyze(input);
        introspector2.analyze(output);
    }
}
```

### Solution
```java
public class Processor {
    private ObjectMetaIntrospector inputIntrospector;   // ✅ Descriptive prefix
    private ObjectMetaIntrospector outputIntrospector;  // ✅ Descriptive prefix
    
    public void process(Data input, Data output) {
        inputIntrospector.analyze(input);
        outputIntrospector.analyze(output);
    }
}
```

---

## Example 5: Generic Types

### Problem
```java
public class DataService {
    private List<User> data;           // ❌ "data" is too generic
    private Map<String, Order> map;    // ❌ "map" is too generic
    
    public void process(List<Product> items) {  // ❌ "items" is too generic
        // ...
    }
}
```

### Solution
```java
public class DataService {
    private List<User> users;                    // ✅ Descriptive plural
    private Map<String, Order> orderMap;         // ✅ Type + container
    
    public void process(List<Product> products) {  // ✅ Descriptive plural
        // ...
    }
}
```

---

## Example 6: Framework-Specific Conventions

### Spring Framework
```java
// Keep these as-is due to Spring conventions
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // ✅ Spring convention
}

// Builder Pattern
public class UserBuilder {
    private String name;
    private int age;
    
    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public UserBuilder age(int age) {
        this.age = age;
        return this;
    }
    
    public User build() {
        return new User(name, age);
    }
}

// Usage follows pattern convention
User user = new UserBuilder()  // ✅ Builder convention
    .name("John")
    .age(30)
    .build();
```

---

## Example 6: Method Parameters

### Problem
```java
public class ReportGenerator {
    public void generate(ObjectMetaIntrospector util, String info) {  // ❌ "util" and "info" are generic
        // ...
    }
}
```

### Solution
```java
public class ReportGenerator {
    public void generate(ObjectMetaIntrospector introspector, String reportTitle) {  // ✅ Specific names
        // ...
    }
}
```

---

## Example 7: Method Parameters

### Problem
```java
public class ReportGenerator {
    public void generate(ObjectMetaIntrospector util, String info) {  // ❌ "util" and "info" are generic
        // ...
    }
}
```

### Solution
```java
public class ReportGenerator {
    public void generate(ObjectMetaIntrospector introspector, String reportTitle) {  // ✅ Specific names
        // ...
    }
}
```

---

## Example 8: Comment Synchronization

### Problem
When renaming variables/parameters, comments often contain references to the old names that need to be updated.

```java
public class DataProcessor {
    
    /** The schema info for processing */  // ❌ "schema" doesn't match actual usage
    private ObjectMeta objSchema;  // ❌ Should be objMeta
    
    /**
     * Process the schema data
     * @param schema the schema object to process  // ❌ Incorrect parameter name and description
     */
    public void process(ObjectMeta schema) {  // ❌ Should be objMeta
        // process schema info  // ❌ Comment references old name
        objSchema = schema;
    }
}
```

### Solution
```java
public class DataProcessor {
    
    /** The meta info for processing */  // ✅ Updated to match new name
    private ObjectMeta objMeta;  // ✅ Fixed naming
    
    /**
     * Process the meta data
     * @param objMeta the meta object to process  // ✅ Updated parameter name and description
     */
    public void process(ObjectMeta objMeta) {  // ✅ Fixed naming
        // process meta info  // ✅ Comment updated
        this.objMeta = objMeta;
    }
}
```

### Rationale
- When renaming `objSchema` to `objMeta`, all related comments must be updated
- Javadoc comments need to reflect the new naming
- Line comments referencing the old name must be updated
- Parameter descriptions in Javadoc must be synchronized

---

## Example 9: Edge Case - Generic Utility Classes

### Special Consideration
```java
public class StringUtils {
    public static String trim(String str) {  // ✅ Acceptable: "str" is standard for string parameters
        return str.trim();
    }
    
    public static boolean isEmpty(String string) {  // ✅ Also acceptable
        return string == null || string.isEmpty();
    }
}
```

### Rationale
- In utility classes with very short methods, abbreviated names like `str` are widely accepted conventions
- The context is clear and the scope is small

---

## Example 9: Domain-Specific Naming

### Problem
```java
public class PaymentProcessor {
    private PaymentGateway gateway;
    private TransactionManager manager;  // ❌ Could be more specific
    
    public void process(Payment payment) {
        gateway.authorize(payment);
        manager.execute(payment);  // "manager" is vague
    }
}
```

### Solution
```java
public class PaymentProcessor {
    private PaymentGateway paymentGateway;
    private TransactionManager transactionManager;  // ✅ More explicit
    
    public void process(Payment payment) {
        paymentGateway.authorize(payment);
        transactionManager.execute(payment);
    }
}
```

---

## Example 11: Iteration Variables

### Problem
```java
List<User> users = getUsers();
for (User u : users) {  // ❌ Too abbreviated
    process(u);
}

for (int i = 0; i < items.size(); i++) {  // ❌ Generic index variable
    Item item = items.get(i);
    // ...
}
```

### Solution
```java
List<User> users = getUsers();
for (User user : users) {  // ✅ Clear and readable
    process(user);
}

for (int index = 0; index < products.size(); index++) {  // ✅ Descriptive index
    Product product = products.get(index);
    // ...
}
```

---

## Decision Summary Table

| Current Name | Type | Context | Action | Recommended Name |
|--------------|------|---------|--------|------------------|
| `schema` | `ObjectMetaIntrospector` | No schema operations | Fix | `introspector` |
| `schema` | `ObjectMetaIntrospector` | Handles schema internally | Keep | `schema` |
| `manager` | `ObjectMetaIntrospector` | No management role | Fix | `introspector` |
| `manager` | `ObjectMetaIntrospectorManager` | Manages introspectors | Keep | `manager` |
| `obj` | Any type | General usage | Fix | Type-derived name |
| `introspector` | `ObjectMetaIntrospector` | Only one introspector in scope | Keep | `introspector` |
| `introspector` | `ObjectMetaIntrospector` | Also has `MapMetaIntrospector` | Fix | `objectIntrospector` |
| `items` | `List<Product>` | Method parameter | Fix | `products` |
| `objSchema` | `ObjectMeta` | Incorrect naming | Fix + Update comments | `objMeta` |

---

## Key Takeaways

1. **Semantic relationship first**: Always check if the name relates to the type's purpose
2. **Context determines specificity**: 
   - Use minimal names (`introspector`) when only one type exists in scope
   - Use qualified names (`objectIntrospector`, `mapIntrospector`) when multiple similar types exist
3. **Contextual relevance matters**: Names like `schema` or `manager` are valid only if genuinely meaningful in context
4. **Prefer clarity over brevity**: `introspector` is better than `int`
5. **Follow conventions**: Framework and pattern conventions take precedence
6. **Be consistent**: Maintain naming patterns within the codebase
7. **Synchronize comments**: When renaming variables/parameters, update all related comments (Javadoc, line comments, documentation) to reflect the new names
