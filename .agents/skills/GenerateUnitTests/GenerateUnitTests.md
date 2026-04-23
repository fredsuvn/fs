---
name: "GenerateUnitTests"
description: "分析代码并生成单元测试。当需要为现有代码创建新的单元测试时调用此技能。"
---

# 单元测试生成技能

这个技能专门用于分析现有的代码，理解其功能和逻辑，然后生成相应的单元测试代码。对于依赖数据库、Redis、第三方接口等外部资源的代码，会提供相应的模拟（Mock）方案。

## 技能执行流程

### 1. 分析目标代码
- 读取并分析目标类的代码
- 理解类的功能、方法和属性
- 识别关键的业务逻辑和边界情况
- 分析依赖关系，识别需要模拟的外部资源

### 2. 设计测试策略
- 确定测试覆盖范围和边界条件
- 设计测试数据和测试场景
- 确定需要模拟的依赖项
- 制定测试方法的命名和组织结构

### 3. 生成单元测试代码
基于前两步的分析结果，生成以下内容：
- 测试类的基本结构
- 测试方法（针对每个公共方法）
- 测试数据和测试场景
- 依赖项的模拟实现
- 断言和验证逻辑

## 生成原则

1. **全面覆盖**：测试应该覆盖所有公共方法和关键业务逻辑
2. **边界条件**：测试应该包括正常场景、边界情况和异常情况
3. **隔离性**：测试应该独立运行，不依赖外部资源
4. **可读性**：测试代码应该清晰表达测试意图
5. **可维护性**：测试代码应该易于修改和扩展
6. **业务整体测试**：对于Controller、Service等分层代码，可以直接从Controller层测试，覆盖Service层的业务逻辑

## 细节注意事项
- 对于依赖数据库的代码，使用内存数据库或模拟（Mock）框架
- 对于依赖Redis的代码，使用模拟（Mock）Redis客户端或内存实现
- 对于依赖第三方接口的代码，使用模拟（Mock）或桩（Stub）实现
- 测试方法应该有清晰的命名，表达测试的功能和场景
- 每个测试方法应该只测试一个特定功能
- 测试数据应该具有代表性，覆盖不同的场景

## 技能执行步骤详解

### 第一步：代码分析
- 使用 `Read` 工具或其他方式读取目标类文件
- 分析类的公共接口和内部实现
- 识别需要测试的关键方法和边界条件
- 识别需要模拟的外部依赖

### 第二步：测试策略设计
- 确定测试覆盖范围：所有公共方法、关键业务逻辑
- 设计测试场景：正常场景、边界情况、异常情况
- 确定模拟策略：对于数据库、Redis、第三方接口等外部依赖
- 设计测试数据：创建代表性的测试数据

### 第三步：测试代码生成
- 创建测试类，使用适当的测试框架（如JUnit、TestNG等），可以探寻当前项目中已经依赖的测试框架
- 生成测试方法，每个方法对应一个测试场景
- 实现依赖项的模拟（Mock）
- 添加测试数据和测试逻辑
- 添加断言和验证

## 最佳实践

1. **测试命名**：使用 `testXxx` 模式命名测试方法, 其中 `Xxx` 是测试功能的描述，可以追加 `ForXxx`、`WhenXxx` 、`WithXxx`等后缀来指定额外的测试场景
2. **测试隔离**：每个测试应该独立运行，不依赖其他测试的状态
3. **断言清晰**：使用明确的断言消息，便于调试
4. **模拟适度**：只在必要时使用模拟，避免过度模拟
5. **性能考虑**：避免在测试中创建不必要的对象或执行耗时操作
6. **测试数据**：使用有意义的测试数据，便于理解测试意图

## 依赖处理策略

### 数据库依赖
- 使用内存数据库（如H2）进行测试
- 使用模拟框架（如Mockito）模拟数据库访问层
- 示例：
  ```java
  // 使用Mockito模拟数据库访问
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
      // 模拟数据库行为
      User user = new User("john", "john@example.com");
      when(userRepository.save(any(User.class))).thenReturn(user);
      
      // 测试逻辑
      User result = userService.createUser("john", "john@example.com");
      
      // 断言
      assertNotNull(result);
      assertEquals("john", result.getUsername());
  }
  ```

### Redis依赖
- 使用模拟框架（如Mockito）模拟Redis客户端
- 使用内存Redis实现（如Embedded Redis）
- 示例：
  ```java
  // 使用Mockito模拟Redis客户端
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
      // 测试逻辑
      cacheService.setCache("key", "value", 60);
      
      // 验证Redis调用
      verify(redisTemplate).opsForValue().set("key", "value", 60, TimeUnit.SECONDS);
  }
  ```

### 第三方接口依赖
- 使用模拟框架（如Mockito）模拟接口客户端
- 使用桩（Stub）实现模拟接口行为
- 示例：
  ```java
  // 使用Mockito模拟第三方接口
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
      // 模拟支付服务行为
      when(paymentService.processPayment(anyString(), anyBigDecimal())).thenReturn(new PaymentResult(true, "success"));
      
      // 测试逻辑
      OrderResult result = orderService.processOrder("order1", new BigDecimal(100));
      
      // 断言
      assertTrue(result.isSuccess());
  }
  ```

## 使用示例

### 场景：为用户服务类生成单元测试

**目标代码分析：**
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

**生成的测试代码：**
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
        // 模拟数据库行为
        User user = new User("john", "john@example.com");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // 测试逻辑
        User result = userService.createUser("john", "john@example.com");
        
        // 断言
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

## 结语
这个技能将帮助您为现有代码生成高质量的单元测试，确保代码的正确性和可靠性。通过合理的模拟策略，即使对于依赖外部资源的代码，也能生成有效的单元测试。