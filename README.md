# CBF 企业级后端框架

CBF (Company Backend Framework) 是一个基于 Spring Boot 3.x 和 MyBatis Plus 的企业级后端开发框架，旨在为公司后端项目提供统一的技术脚手架。

## 🚀 特性

- **统一版本管理**: 通过 Parent POM 统一管理所有依赖版本
- **自动配置**: 基于 Spring Boot Starter 机制，开箱即用
- **分层架构**: 支持 Controller-Service-Manager-Repository (CSMR) 四层架构
- **统一响应**: 提供统一的响应格式和异常处理
- **数据访问**: 集成 MyBatis Plus，支持分页、字段自动填充等
- **缓存支持**: 集成 Redis 和 Redisson，提供分布式锁
- **API文档**: 集成 Knife4j，自动生成 API 文档
- **监控支持**: 集成 Druid 监控，支持 Spring Boot Admin

## 📦 模块结构

```
cbf-framework/
├── pom.xml                  # 根POM，继承Spring Boot Parent，统一版本管理
├── cbf-common/              # 基础工具包
├── cbf-starter-data/        # 数据库访问自动配置
├── cbf-starter-web/         # Web层自动配置
├── cbf-starter-cache/       # 缓存自动配置
└── cbf-demo/               # 示例项目
```

## 🛠️ 技术栈

- **Java**: 17+
- **Spring Boot**: 3.2.0
- **MyBatis Plus**: 3.5.4.1
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Redisson**: 3.24.3
- **Druid**: 1.2.20
- **Knife4j**: 4.3.0

## 📋 快速开始

### 1. 创建业务项目

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.company.cbf</groupId>
        <artifactId>cbf-framework</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>your-project</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- CBF Starter Web -->
        <dependency>
            <groupId>com.company.cbf</groupId>
            <artifactId>cbf-starter-web</artifactId>
        </dependency>

        <!-- CBF Starter Data -->
        <dependency>
            <groupId>com.company.cbf</groupId>
            <artifactId>cbf-starter-data</artifactId>
        </dependency>

        <!-- CBF Starter Cache -->
        <dependency>
            <groupId>com.company.cbf</groupId>
            <artifactId>cbf-starter-cache</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 2. 创建实体类

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer status;
}
```

### 3. 创建Mapper接口

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

### 4. 创建Service类

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }
    
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
}
```

### 5. 创建Controller类

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public CommonResponse<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return CommonResponse.success("创建用户成功", createdUser);
    }
    
    @GetMapping("/{id}")
    public CommonResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return CommonResponse.success(user);
    }
}
```

### 6. 配置文件

```yaml
# application.yml
spring:
  application:
    name: your-project
  datasource:
    url: jdbc:mysql://localhost:3306/your_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

# CBF 配置
cbf:
  druid:
    monitor:
      enabled: true
      login-username: admin
      login-password: admin
  knife4j:
    enabled: true
  cache:
    redis:
      enabled: true
```

## 🔧 配置说明

### 数据库配置

```yaml
# Druid 数据源配置
cbf:
  druid:
    url: jdbc:mysql://localhost:3306/your_db
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    initial-size: 5
    min-idle: 5
    max-active: 20
    max-wait: 60000
    monitor:
      enabled: true
      stat-view-servlet-url: /druid/*
      login-username: admin
      login-password: admin
```

### Redis配置

```yaml
# Redis 配置
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
      timeout: 5000ms

# Redisson 配置
spring:
  data:
    redis:
      redisson:
        config: classpath:redisson.yml
```

### API文档配置

```yaml
# Knife4j 配置
cbf:
  knife4j:
    enabled: true

knife4j:
  enable: true
  openapi:
    title: Your Project API
    description: Your Project API文档
    version: 1.0.0
```

## 📚 开发规范

### 1. 分层架构

- **Controller层**: 处理HTTP请求，参数校验，返回统一响应
- **Service层**: 业务逻辑编排，事务控制
- **Manager层**: 核心业务逻辑，缓存处理
- **Repository层**: 数据访问，继承BaseMapper

### 2. 编码规范

- 实体类必须继承 `BaseEntity`
- Mapper接口必须继承 `BaseMapper<T>`
- Controller方法必须返回 `CommonResponse<T>`
- 事务控制仅在Service层使用 `@Transactional`
- 异常处理使用 `BusinessException`

### 3. 响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {...},
  "traceId": "uuid",
  "timestamp": 1640995200000
}
```

## 🚀 部署说明

### 1. 框架发布

```bash
# 在框架根目录执行
mvn clean deploy -DskipTests
```

### 2. 业务项目部署

```bash
# 在业务项目目录执行
mvn clean package -DskipTests
java -jar target/your-project.jar
```

## 📖 API文档

启动应用后，访问以下地址查看API文档：

- Knife4j UI: http://localhost:8080/doc.html
- Druid监控: http://localhost:8080/druid/

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 Apache 2.0 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 项目维护者: CBF Framework Team
- 邮箱: cbf@company.com
- 项目地址: https://github.com/company/cbf-framework
