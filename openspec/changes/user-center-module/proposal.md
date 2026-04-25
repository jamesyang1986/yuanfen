## Why

社交 App 需要一个完善的用户中心模块作为基础设施，支持多种方式的用户注册与登录（手机号、邮箱、用户名），以降低用户注册门槛，提升用户留存，并为后续社交功能提供身份认证支撑。

## What Changes

- 新增用户注册功能：支持手机号、邮箱、用户名三种方式注册账户
- 新增用户登录功能：支持手机号（+验证码/密码）、邮箱（+密码）、用户名（+密码）三种方式登录
- 新增 JWT Token 颁发与刷新机制
- 新增用户信息基础模型（User Entity）
- 新增手机号验证码发送与校验接口
- 新增密码加密存储（BCrypt）

## Capabilities

### New Capabilities

- `user-registration`: 用户注册能力，支持手机号、邮箱、用户名三种注册方式，含字段校验与唯一性校验
- `user-login`: 用户登录能力，支持手机号+验证码、手机号+密码、邮箱+密码、用户名+密码四种登录方式，登录成功后返回 JWT Token
- `sms-verification`: 手机短信验证码发送与校验能力，用于手机号注册/登录流程
- `auth-token`: JWT Access Token 与 Refresh Token 的颁发、校验、刷新能力

### Modified Capabilities

（当前无已有 Spec，无需修改）

## Impact

- **新增代码**：`src/main/java/com/yf/yuanfen/` 下新增 `user/`、`auth/` 模块
- **数据库**：新增 `users` 表（id, username, email, phone, password_hash, created_at, updated_at）
- **API**：新增 `/api/v1/auth/register`、`/api/v1/auth/login`、`/api/v1/auth/refresh`、`/api/v1/sms/send` 端点
- **依赖**：新增 Spring Security、JWT（jjwt）、MyBatis Spring Boot Starter、MySQL Connector/J 依赖
- **无 BREAKING 变更**（全新模块）
