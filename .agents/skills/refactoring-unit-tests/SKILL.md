---
name: "refactoring-unit-tests"
description: "Analyze, restructure, and optimize unit test code. Call this skill when you need to refactor, improve, or optimize existing unit tests."
---

# Unit Test Refactoring and Optimization Skill

This skill is specifically designed to analyze existing unit test code, understand test intentions and the functionality of the tested classes, and then systematically restructure and optimize them.

## Skill Execution Flow

### 1. Analyze Original Unit Test Content
- Read and analyze existing unit test classes
- Understand the intention and test scenarios of each test method
- Identify test coverage scope and boundary conditions

### 2. Read the Code of the Tested Class
- Analyze the functionality, methods, and properties of the tested class
- Understand the class's design patterns and architecture
- Identify key business logic and boundary cases

### 3. Analyze Original Test Code
- Analyze the quality of the original test code, such as duplicate code, insufficient comments, inconsistent naming, and code stickiness
- Analyze dependencies in the original test code, such as whether it depends on other methods of the tested class or methods of other classes
- Identify key business logic and boundary cases, such as whether all possible input scenarios are covered and whether exception cases are handled

### 4. Restructure and Optimize Unit Tests
Based on the analysis results from the previous two steps, perform the following optimizations:
- **Extract duplicate code**: Extract repeated test logic into helper methods or separate classes. For repeated test scenarios, consider whether they can be extracted into a common test method
- **Optimize test structure**: Improve the organization structure of test classes to make them clearer and more readable
- **Optimize code structure**: Improve the code quality of test classes, such as organizing code structure, adding comments, and increasing code readability
- **Maintain coverage**: Ensure that optimized tests cover all original test scenarios, do not miss any!
- **Add missing tests**: Discover and add code paths that are not covered
- **Reasonable classification**: Split tests into multiple logically related classes as needed

## Optimization Principles

1. **DRY Principle**: Eliminate duplicate code and extract common logic
2. **Single Responsibility**: Each non-complete scenario test method should only test one specific function. For complex business scenarios, one test method can call multiple single-responsibility methods to test complete scenarios
3. **Readability**: Test code should clearly express test intentions
4. **Maintainability**: Test code should be easy to modify and extend
5. **Coverage Completeness**: After optimization, the coverage of the original test code must be maintained or expanded! Do not miss anything!

## Detailed Considerations
- Optimized test code should maintain the same dependency relationships as the original test code
- Optimized test code should maintain the same exception handling as the original test code
- Optimized test code should maintain the same comment meaning as the original test code
- Original disabled test code should be retained, not deleted, and can be commented with explanations
- Unless necessary or with complete replacement code, try not to miss original test code (can be organized or repositioned)
- For internal classes and other structures with specific testing purposes, they can be renamed to clarify their purpose and testing objectives
- Note that content in sub-packages also needs to be restructured and optimized together
- For code that tests exception classes, remember to throw the exception after creating it!

## Detailed Skill Execution Steps

### Step 1: Test Code Analysis
- Use the `Read` tool or other methods to read test files
- Use `SearchCodebase` or other methods to find related test helper classes
- Analyze the functionality, methods, and properties of the tested class, understand its business logic and boundary cases
- Analyze the naming, structure, code quality, and dependencies of test methods

### Step 2: Tested Class Analysis
- Locate the tested class file
- Analyze the class's public interface and internal implementation
- Identify key methods and boundary conditions that need to be tested

### Step 3: Optimization Implementation
- Extract repeated test preparation logic into `@BeforeEach` or `@BeforeAll` setup methods
- Extract repeated test code or logic into helper methods or separate classes. For repeated test scenarios, consider whether they can be extracted into a common test method
- Organize dependent test classes. For common dependency classes, consider whether they can be extracted into a separate test class
- Create test data builders or factory methods
- Reorganize test methods, grouping them by functional modules
- Add missing boundary condition tests
- Ensure each test method has a clear name and single responsibility
- Unless specifically requested by the user, do not execute after restructuring is complete, let the user operate themselves

## Best Practices

1. **Test Naming**: Use `testXxx` pattern to name test methods, where `Xxx` is a description of the test function. You can append suffixes like `ForXxx`, `WhenXxx`, `WithXxx` to specify additional test scenarios
2. **Test Isolation**: Each test should run independently and not depend on the state of other tests
3. **Clear Assertions**: Use clear assertion messages for easier debugging
4. **Moderate Mocking**: Only use mocking when necessary, avoid over-mocking
5. **Performance Considerations**: Avoid creating unnecessary objects or performing time-consuming operations in tests
6. **Mock Parameters**: If necessary, use mock parameters to test boundary cases and exception cases

## Notes

- Ensure that existing test logic is not broken during the optimization process
- Run tests to verify functional correctness after modification
- If the project has specific test specifications, follow project conventions
- For complex test scenarios, consider creating specialized test utility classes
- Private empty constructors of classes do not need to be covered by tests
- Comments in code should be written in English, not the language of our AI conversation, but the comments in the code
- Test classes in sub-packages within the package also need to be restructured and optimized together
- For code that tests exception classes, remember to throw the exception after creating it!

## Usage Example

### Scenario: Optimize Unit Tests for a User Service Class

**Original Test Code:**
```java
// UserServiceTest.java - Original Version
public class UserServiceTest {

    @Test
    public void testCreateUser() {
        UserService service = new UserService();
        User user = service.createUser("john", "john@example.com");
        assertNotNull(user);
        assertEquals("john", user.getUsername());
    }

    @Test
    public void testCreateUserWithDuplicateEmail() {
        UserService service = new UserService();
        service.createUser("user1", "duplicate@example.com");
        assertThrows(DuplicateEmailException.class, () -> {
            service.createUser("user2", "duplicate@example.com");
        });
    }
}
```

**Original Test Code Analysis:**

- The test methods contain duplicate user creation logic, resulting in code redundancy
- The test methods do not test boundary conditions, such as empty or null usernames
- The test methods do not test whether the email format is correct
- The test method code is sticky together, with no comments and no grouping

**Original Test Code Improvements:**

- Extract duplicate user creation logic into helper methods or helper classes
- Add missing boundary condition tests
- Reorganize test methods, grouping them by functional modules

**Optimized Test Code:**
```java
// UserServiceTest.java - Optimized Version
public class UserServiceTest {

    private UserService service;

    @BeforeEach
    public void setUp() {
        service = new UserService();
    }

    @Test
    public void testCreateUser() {
        User user = createTestUser("john", "john@example.com");

        assertNotNull(user);
        assertEquals("john", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    public void testCreateUserWithDuplicateEmail() {
        createTestUser("user1", "duplicate@example.com");

        assertThrows(DuplicateEmailException.class,
            () -> createTestUser("user2", "duplicate@example.com"));
    }

    @Test
    public void testCreateUserWithInvalidUsernameFormat() {
        assertThrows(InvalidUsernameException.class,
            () -> createTestUser("", "test@example.com"));

        assertThrows(InvalidUsernameException.class,
            () -> createTestUser(null, "test@example.com"));
    }

    // Create test user
    private User createTestUser(String username, String email) {
        return service.createUser(username, email);
    }
}
```

## Conclusion
This skill will help you systematically improve the quality of unit test code, increase test maintainability and readability, while ensuring the completeness of test coverage.
Remember: Old code logic and coverage must be preserved