---
name: "generate-unit-tests"
description: "Analyze code and generate unit tests. Call this skill when you need to create new unit tests for existing code."
---

# Unit Test Generation Skill

This skill is specifically designed to analyze existing code, understand its functionality and logic, and then generate corresponding unit test code. For code that depends on external resources such as databases, Redis, or third-party interfaces, appropriate mocking solutions will be provided.

## Skill Execution Flow

### 1. Analyze Target Code
- Read and analyze the target class code
- Understand the class's functionality, methods, and properties
- Identify key business logic and boundary cases
- Analyze dependencies and identify external resources that need to be mocked

### 2. Design Test Strategy
- Determine test coverage and boundary conditions
- Design test data and test scenarios
- Identify dependencies that need to be mocked
- Establish naming conventions and organizational structure for test methods

### 3. Generate Unit Test Code
Based on the analysis from the previous two steps, generate the following:
- Basic structure of the test class
- Test methods (for each public method)
- Test data and test scenarios
- Mock implementations for dependencies
- Assertions and validation logic

## Generation Principles

1. **Comprehensive Coverage**: Tests should cover all public methods and key business logic
2. **Boundary Conditions**: Tests should include normal scenarios, boundary cases, and exception cases
3. **Isolation**: Tests should run independently without relying on external resources
4. **Readability**: Test code should clearly express test intent
5. **Maintainability**: Test code should be easy to modify and extend
6. **Overall Business Testing**: For layered code such as Controllers and Services, testing can be performed directly from the Controller layer to cover Service layer business logic

## Detailed Considerations
- For code that depends on databases, use in-memory databases or mocking frameworks
- For code that depends on Redis, use mock Redis clients or in-memory implementations
- For code that depends on third-party interfaces, use mock or stub implementations
- Test methods should have clear names that express the functionality and scenario being tested
- Each test method should test only one specific functionality
- Test data should be representative and cover different scenarios

## Detailed Skill Execution Steps

### Step 1: Code Analysis
- Use the `Read` tool or other methods to read the target class file
- Analyze the class's public interface and internal implementation
- Identify key methods and boundary conditions that need to be tested
- Identify external dependencies that need to be mocked

### Step 2: Test Strategy Design
- Determine test coverage: all public methods, key business logic
- Design test scenarios: normal scenarios, boundary cases, exception cases
- Determine mocking strategy: for external dependencies such as databases, Redis, and third-party interfaces
- Design test data: create representative test data

### Step 3: Test Code Generation
- Create test classes using appropriate testing frameworks (such as JUnit, TestNG, etc.), and explore existing testing frameworks already dependencies in the current project
- Generate test methods, each corresponding to a test scenario
- Implement mocks for dependencies
- Add test data and test logic
- Add assertions and validations

## Best Practices

1. **Test Naming**: Use the `testXxx` pattern to name test methods, where `Xxx` is a description of the test functionality. Suffixes like `ForXxx`, `WhenXxx`, or `WithXxx` can be added to specify additional test scenarios
2. **Test Isolation**: Each test should run independently and not depend on the state of other tests
3. **Clear Assertions**: Use clear assertion messages for easier debugging
4. **Moderate Mocking**: Only use mocks when necessary, avoid over-mocking
5. **Performance Considerations**: Avoid creating unnecessary objects or performing time-consuming operations in tests
6. **Test Data**: Use meaningful test data to facilitate understanding of test intent

## Dependency Handling Strategies

### Database Dependencies
- Use in-memory databases (such as H2) for testing
- Use mocking frameworks (such as Mockito) to mock database access layers
- Example:
  ```java
  // Using Mockito to mock database access
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  public void setUp() {
      MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testCreateUser() {
      // Mock database behavior
      User user = new User("john", "john@example.com");
      when(userRepository.save(any(User.class))).thenReturn(user);

      // Test logic
      User result = userService.createUser("john", "john@example.com");

      // Assertions
      assertNotNull(result);
      assertEquals("john", result.getUsername());
  }
  ```

### Redis Dependencies
- Use mocking frameworks (such as Mockito) to mock Redis clients
- Use in-memory Redis implementations (such as Embedded Redis)
- Example:
  ```java
  // Using Mockito to mock Redis client
  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @InjectMocks
  private CacheService cacheService;

  @BeforeEach
  public void setUp() {
      MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSetCache() {
      // Test logic
      cacheService.setCache("key", "value", 60);

      // Verify Redis call
      verify(redisTemplate).opsForValue().set("key", "value", 60, TimeUnit.SECONDS);
  }
  ```

### Third-Party Interface Dependencies
- Use mocking frameworks (such as Mockito) to mock interface clients
- Use stub implementations to simulate interface behavior
- Example:
  ```java
  // Using Mockito to mock third-party interface
  @Mock
  private PaymentService paymentService;

  @InjectMocks
  private OrderService orderService;

  @BeforeEach
  public void setUp() {
      MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testProcessOrder() {
      // Mock payment service behavior
      when(paymentService.processPayment(anyString(), anyBigDecimal())).thenReturn(new PaymentResult(true, "success"));

      // Test logic
      OrderResult result = orderService.processOrder("order1", new BigDecimal(100));

      // Assertions
      assertTrue(result.isSuccess());
  }
  ```

## Usage Example

### Scenario: Generate Unit Tests for User Service Class

**Target Code Analysis:**
```java
// UserService.java
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String email) {
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException("Username cannot be empty");
        }

        if (email == null || !email.contains("@")) {
            throw new InvalidEmailException("Invalid email format");
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already exists");
        }

        User user = new User(username, email);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
```

**Generated Test Code:**
```java
// UserServiceTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserSuccess() {
        // Mock database behavior
        User user = new User("john", "john@example.com");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Test logic
        User result = userService.createUser("john", "john@example.com");

        // Assertions
        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    public void testCreateUserWithEmptyUsername() {
        assertThrows(InvalidUsernameException.class, () -> {
            userService.createUser("", "john@example.com");
        });
    }

    @Test
    public void testCreateUserWithNullUsername() {
        assertThrows(InvalidUsernameException.class, () -> {
            userService.createUser(null, "john@example.com");
        });
    }

    @Test
    public void testCreateUserWithInvalidEmail() {
        assertThrows(InvalidEmailException.class, () -> {
            userService.createUser("john", "invalid-email");
        });
    }

    @Test
    public void testCreateUserWithNullEmail() {
        assertThrows(InvalidEmailException.class, () -> {
            userService.createUser("john", null);
        });
    }

    @Test
    public void testCreateUserWithDuplicateEmail() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> {
            userService.createUser("john", "john@example.com");
        });
    }

    @Test
    public void testGetUserByIdSuccess() {
        User user = new User("john", "john@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }
}
```

## Conclusion
This skill will help you generate high-quality unit tests for existing code, ensuring code correctness and reliability. Through reasonable mocking strategies, effective unit tests can be generated even for code that depends on external resources.