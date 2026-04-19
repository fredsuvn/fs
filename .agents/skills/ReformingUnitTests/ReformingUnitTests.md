---
name: "ReformingUnitTests"
description: "分析、重整和优化单元测试代码。当需要重构、改进或优化现有单元测试时调用此技能。"
---

# 单元测试重整优化技能

这个技能专门用于分析现有的单元测试代码，理解测试意图和被测试类的功能，然后进行系统性的重整和优化。

## 技能执行流程

### 1. 分析原单元测试内容
- 读取并分析现有的单元测试类
- 理解每个测试方法的意图和测试场景
- 识别测试覆盖的范围和边界条件

### 2. 阅读被测试类的代码
- 分析被测试类的功能、方法和属性
- 理解类的设计模式和架构
- 识别关键的业务逻辑和边界情况

### 3. 分析原始测试代码
- 分析原始测试代码代码质量，如重复代码、注释不足、命名不规范、代码是否粘黏在一起等
- 分析原始测试代码的依赖关系，如是否依赖了被测试类的其他方法、是否依赖了其他类的方法等
- 识别关键的业务逻辑和边界情况，如是否覆盖了所有可能的输入场景、是否处理了异常情况等

### 4. 重整优化单元测试
基于前两步的分析结果，进行以下优化：
- **提取重复代码**：将重复的测试逻辑提取到辅助方法或单独的类中，对于重复的测试场景，考虑是否可以提取为一个通用的测试方法
- **优化测试结构**：改进测试类的组织结构，使其更清晰易读
- **优化代码结构**：改进测试类的代码质量，如整理代码结构、添加注释、增加代码可读性等
- **保持覆盖范围**：确保优化后的测试覆盖所有原有测试场景，不可遗漏！
- **补充缺失测试**：发现并补充未覆盖的代码路径
- **合理分类**：根据需要将测试拆分为多个逻辑相关的类

## 优化原则

1. **DRY原则**：消除重复代码，提取公共逻辑
2. **单一职责**：每个非完整场景测试方法只测试一个特定功能，对于功能业务复杂的场景，可以一个测试方法调用多个单一职责方法进行场景完整的测试
3. **可读性**：测试代码应该清晰表达测试意图
4. **可维护性**：测试代码应该易于修改和扩展
5. **覆盖完整性**：优化后，原测试代码的覆盖范围必须保持或扩大！不可遗漏！

## 细节注意事项
- 优化后的测试代码要保持与原始测试代码的依赖关系一致
- 优化后的测试代码要保持与原始测试代码的异常处理一致
- 优化后的测试代码要保持与原始测试代码的注释意思一致
- 原有的未启用的测试代码要保留，不要删除，可以加上注释说明
- 除非必要或者有完全替代的代码，否则原测试代码尽量不要遗漏（可以整理、换位置）
- 对于有专门测试用途的内部类等结构，可以重命名以明确用途和测试目的
- 注意子包内的内容也需要一起重整和优化
- 对于测试异常类的代码，记得new完异常后，不要忘记throw异常！

## 技能执行步骤详解

### 第一步：测试代码分析
- 使用 `Read` 工具或其他方式读取测试文件
- 使用 `SearchCodebase` 或其他方式查找相关的测试辅助类
- 分析被测试类的功能、方法和属性，理解其业务逻辑和边界情况
- 分析测试方法的命名、结构、代码质量和依赖关系

### 第二步：被测试类分析
- 定位被测试的类文件
- 分析类的公共接口和内部实现
- 识别需要测试的关键方法和边界条件

### 第三步：优化实施
- 提取重复的测试准备逻辑到 `@BeforeEach` 或 `@BeforeAll` 等 setUp 类方法中
- 提取重复的测试代码或逻辑到辅助方法或单独的类中，对于重复的测试场景，考虑是否可以提取为一个通用的测试方法
- 整理依赖的测试类，对于公共的依赖类，考虑是否可以提取为一个单独的测试类
- 创建测试数据构建器或工厂方法
- 重新组织测试方法，按功能模块分组
- 添加缺失的边界条件测试
- 确保每个测试方法有清晰的命名和单一职责
- 除非用户特别说明，否则重整完成后不要执行，让用户自己操作

## 最佳实践

1. **测试命名**：使用 `testXxx` 模式命名测试方法, 其中 `Xxx` 是测试功能的描述，可以追加 `ForXxx`、`WhenXxx` 、`WithXxx`等后缀来指定额外的测试场景
2. **测试隔离**：每个测试应该独立运行，不依赖其他测试的状态
3. **断言清晰**：使用明确的断言消息，便于调试
4. **模拟适度**：只在必要时使用模拟，避免过度模拟
5. **性能考虑**：避免在测试中创建不必要的对象或执行耗时操作
6. **模拟参数**：如有必要，使用模拟参数来测试边界情况和异常情况

## 注意事项

- 优化过程中要确保不破坏现有的测试逻辑
- 修改后要运行测试验证功能正确性
- 如果项目有特定的测试规范，要遵循项目约定
- 对于复杂的测试场景，考虑创建专门的测试工具类
- 类的私有空构造方法不需要覆盖测试
- 代码中的注释用英语编写，注意不是我跟你的AI对话语言，而是代码中的注释
- 包内的子包内的测试类也需要一起重整和优化
- 对于测试异常类的代码，记得new完异常后，不要忘记throw异常！

## 使用示例

### 场景：优化一个用户服务类的单元测试

**原始测试代码：**
```java
// UserServiceTest.java - 原始版本
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

**原始测试代码分析：**

- 测试方法中包含重复的用户创建逻辑，导致代码冗余
- 测试方法中没有测试边界条件，如用户名为空或为空字符串
- 测试方法中没有测试邮箱格式是否正确
- 测试方法代码粘黏在一起，没有注释，没有分组

**原始测试代码改进：**

- 提取重复的用户创建逻辑到辅助方法或者辅助类中
- 添加缺失的边界条件测试
- 重新组织测试方法，按功能模块分组

**优化后的测试代码：**
```java
// UserServiceTest.java - 优化版本
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

## 结语
这个技能将帮助您系统性地改进单元测试代码的质量，提高测试的可维护性和可读性，同时确保测试覆盖的完整性。