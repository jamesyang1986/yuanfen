## 1. 项目依赖与配置

- [x] 1.1 在 `pom.xml` 中添加 Spring Security、jjwt 0.12.x、MyBatis Spring Boot Starter、MySQL Connector/J、Spring Validation 依赖（移除 Spring Data JPA）
- [x] 1.2 配置 `application.properties`：数据库连接（MySQL 5.7）、`mybatis.mapper-locations=classpath:mapper/*.xml`、`mybatis.configuration.map-underscore-to-camel-case=true`、JWT secret、Token 过期时间
- [x] 1.3 创建 `SecurityConfig`：放行 `/api/v1/auth/**`、`/api/v1/sms/**` 端点，其余端点需要认证；禁用 CSRF、Session（无状态）

## 2. 公共基础设施

- [x] 2.1 创建 `ApiResponse<T>` 统一响应体（code、message、data 字段）
- [x] 2.2 创建 `GlobalExceptionHandler`（`@RestControllerAdvice`）：处理 `MethodArgumentNotValidException`、自定义业务异常，返回统一格式
- [x] 2.3 创建 `BizException` 自定义异常类和 `ErrorCode` 枚举（含 PHONE_ALREADY_EXISTS、EMAIL_ALREADY_EXISTS、USERNAME_ALREADY_EXISTS、INVALID_SMS_CODE、INVALID_CREDENTIALS、USER_NOT_FOUND、TOKEN_EXPIRED、INVALID_TOKEN、UNAUTHORIZED、REFRESH_TOKEN_EXPIRED、INVALID_REFRESH_TOKEN、REFRESH_TOKEN_REVOKED、SMS_SEND_TOO_FREQUENT 等）

## 3. 数据库初始化与 POJO

- [x] 3.1 编写 `src/main/resources/db/schema.sql`：创建 `users` 表（id BIGINT AUTO_INCREMENT、username、email、phone、password_hash、created_at、updated_at），字段显式指定 `utf8mb4`，为 phone/email/username 创建 UNIQUE 索引，兼容 MySQL 5.7 语法
- [x] 3.2 编写 `refresh_tokens` 表 DDL（id、user_id、token、expires_at、revoked TINYINT(1)、created_at），追加至 `schema.sql`
- [x] 3.3 创建 `User` POJO 类（id、username、email、phone、passwordHash、createdAt、updatedAt），不含 JPA 注解
- [x] 3.4 创建 `RefreshToken` POJO 类（id、userId、token、expiresAt、revoked、createdAt）

## 4. MyBatis Mapper

- [x] 4.1 创建 `UserMapper` 接口（`selectByPhone`、`selectByEmail`、`selectByUsername`、`insert`、`existsByPhone`、`existsByEmail`、`existsByUsername` 方法）
- [x] 4.2 创建 `src/main/resources/mapper/UserMapper.xml`，实现上述所有 SQL（SELECT、INSERT），使用 `resultType` 映射至 `User` POJO
- [x] 4.3 创建 `RefreshTokenMapper` 接口（`insert`、`selectByToken`、`deleteByUserId`、`revokeByToken` 方法）
- [x] 4.4 创建 `src/main/resources/mapper/RefreshTokenMapper.xml`，实现对应 SQL
- [x] 4.5 在主类或 `MyBatisConfig` 中添加 `@MapperScan("com.yf.yuanfen.**.mapper")` 注解

## 5. 短信验证码模块

- [x] 5.1 创建 `SmsService` 接口（`sendCode(phone)`、`verifyCode(phone, code)` 方法）
- [x] 5.2 创建 `MockSmsServiceImpl` 实现：`ConcurrentHashMap` 存储验证码+过期时间；生成 6 位随机数字；控制台打印；60 秒内不重复发送
- [x] 5.3 创建 `SmsController`，提供 `POST /api/v1/sms/send` 接口，调用 `SmsService.sendCode()`

## 6. JWT Token 工具

- [x] 6.1 创建 `JwtUtil`：生成 Access Token（含 sub=userId、iat、exp）、生成 Refresh Token、解析并校验 Token 签名与过期时间
- [x] 6.2 创建 `JwtAuthenticationFilter`（`OncePerRequestFilter`）：从 `Authorization` Header 提取 Bearer Token，校验后注入 SecurityContext
- [x] 6.3 将 `JwtAuthenticationFilter` 注册到 `SecurityConfig` 的过滤器链（在 `UsernamePasswordAuthenticationFilter` 之前）

## 6. 用户注册功能

- [x] 6.1 创建注册 DTO：`PhoneRegisterRequest`（phone、smsCode、password）、`EmailRegisterRequest`（email、password）、`UsernameRegisterRequest`（username、password），添加 Bean Validation 注解
- [x] 6.2 创建 `UserService.registerByPhone()`：校验验证码 → 检查手机号唯一性 → BCrypt 加密密码 → 保存 User → 颁发 Token 对
- [x] 6.3 创建 `UserService.registerByEmail()`：校验邮箱唯一性 → BCrypt 加密密码 → 保存 User → 颁发 Token 对
- [x] 6.4 创建 `UserService.registerByUsername()`：校验用户名唯一性（大小写不敏感）→ BCrypt 加密密码 → 保存 User → 颁发 Token 对
- [x] 6.5 创建 `AuthController`，提供 `POST /api/v1/auth/register/phone`、`/register/email`、`/register/username` 接口

## 7. 用户登录功能

- [x] 7.1 创建 `LoginRequest` DTO（loginType、identifier、credential），添加 Bean Validation 注解
- [x] 7.2 创建 `LoginService`：策略模式分发不同 `loginType`（PHONE_SMS、PHONE_PASSWORD、EMAIL_PASSWORD、USERNAME_PASSWORD）
- [x] 7.3 实现 `PhoneSmsLoginStrategy`：查询用户（手机号）→ 校验验证码 → 颁发 Token 对
- [x] 7.4 实现 `PhonePasswordLoginStrategy`：查询用户（手机号）→ BCrypt 校验密码 → 颁发 Token 对
- [x] 7.5 实现 `EmailPasswordLoginStrategy`：查询用户（邮箱）→ BCrypt 校验密码 → 颁发 Token 对
- [x] 7.6 实现 `UsernamePasswordLoginStrategy`：查询用户（用户名）→ BCrypt 校验密码 → 颁发 Token 对
- [x] 7.7 在 `AuthController` 添加 `POST /api/v1/auth/login` 接口，调用 `LoginService`

## 8. Token 刷新与登出

- [x] 8.1 创建 `TokenService.refreshAccessToken(refreshToken)`：查询数据库校验 Refresh Token → 检查 revoked 状态 → 颁发新 Access Token
- [x] 8.2 在 `AuthController` 添加 `POST /api/v1/auth/refresh` 接口
- [x] 8.3 创建 `TokenService.revokeRefreshToken(refreshToken)`：将 Refresh Token 标记为 revoked=true
- [x] 8.4 在 `AuthController` 添加 `POST /api/v1/auth/logout` 接口（需认证），调用 `revokeRefreshToken`

## 9. 集成验证

- [x] 9.1 手动在 MySQL 5.7 中执行 `schema.sql`，验证 `users`、`refresh_tokens` 表及索引创建成功
- [ ] 9.2 使用 curl 或 Postman 验证手机号注册、邮箱注册、用户名注册流程
- [ ] 9.3 验证四种登录方式均返回正确 Token 对
- [ ] 9.4 验证 Access Token 过期后使用 Refresh Token 刷新成功
- [ ] 9.5 验证登出后 Refresh Token 不可再用
- [ ] 9.6 验证非法 Token、缺失字段等异常场景返回正确错误码
