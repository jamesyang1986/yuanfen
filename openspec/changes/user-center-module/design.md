## Context

本项目为社交 App 后端，基于 Spring Boot（Java）构建。当前代码库只有骨架（`YuanfenApplication.java`），尚无任何业务模块。用户中心是整个社交平台的基础，所有其他功能均依赖用户身份认证，因此需要优先实现。

技术栈：Spring Boot 3.x、Spring Security 6、MyBatis 3.x、MySQL 5.7、JWT（jjwt 0.12.x）、BCrypt。

## Goals / Non-Goals

**Goals:**
- 实现多方式注册（手机号、邮箱、用户名）
- 实现多方式登录（手机号+验证码、手机号+密码、邮箱+密码、用户名+密码）
- JWT Access Token（短期）+ Refresh Token（长期）双 Token 机制
- 手机号验证码发送与校验（Mock 实现，预留短信服务接口）
- 密码 BCrypt 加密存储
- 统一响应体结构 `ApiResponse<T>`

**Non-Goals:**
- 第三方 OAuth 登录（微信、微博等）
- 双因素认证（2FA）
- 用户资料编辑（头像、昵称等）
- 设备管理与多端踢出
- 短信真实发送（本期 Mock）

## Decisions

### 1. 认证方案：JWT 无状态 Token
**选择**：JWT（Access Token 15min + Refresh Token 7d），无 Session。
**理由**：社交 App 天然移动端场景，无状态便于水平扩展；Refresh Token 存数据库可支持主动失效。
**备选**：Redis Session — 需额外基础设施，当前阶段引入成本高。

### 2. 密码存储：BCrypt
**选择**：`BCryptPasswordEncoder`（Spring Security 内置）。
**理由**：自带 Salt、计算成本可调、业界标准；无需额外依赖。

### 3. 手机验证码存储：内存 Map（Mock）
**选择**：`ConcurrentHashMap` 存储验证码 + 过期时间，预留 `SmsService` 接口。
**理由**：本期无真实短信服务合同；接口隔离后替换为 Redis + 真实短信 SDK 无需改动上层逻辑。

### 4. ORM 框架：MyBatis（XML Mapper）
**选择**：MyBatis 3.x，SQL 写在 XML Mapper 文件中，POJO 为普通 Java 类（无 JPA 注解）。
**理由**：MySQL 5.7 场景下 SQL 可控性更强；社交 App 后续会有复杂查询（关注、Feed 流），MyBatis 的原生 SQL 比 JPQL/Criteria API 更易调优；团队对 SQL 熟悉度通常高于 JPA。
**备选**：Spring Data JPA — 自动建表方便，但 MySQL 5.7 下需手动处理 utf8mb4、索引等细节，复杂查询退化为原生 SQL 反而两边都要写。
**约束**：MySQL 5.7 不支持 `JSON` 列默认值语法变化，建表 SQL 需兼容 5.7 语法（避免 `DEFAULT CURRENT_TIMESTAMP(6)` 等 8.0 特性）。

### 5. 数据库初始化：手动 SQL 脚本
**选择**：在 `src/main/resources/db/` 目录下维护 `schema.sql`（建表）和 `data.sql`（初始数据），通过 Spring Boot `spring.sql.init` 或手动执行。
**理由**：MyBatis 无自动建表；显式 SQL 脚本可 review、可版本控制；后续引入 Flyway/Liquibase 时直接复用脚本。

### 6. 包结构：按特性（Feature）分包
```
com.yf.yuanfen
├── common/          # ApiResponse, exception, constants
├── config/          # SecurityConfig, JwtConfig, MyBatisConfig
├── user/            # User POJO, Mapper, Service
│   ├── entity/
│   ├── mapper/
│   └── service/
└── auth/            # 登录/注册 Controller + DTO + JWT util
    ├── controller/
    ├── dto/
    └── util/
```
resources/
└── mapper/
    ├── UserMapper.xml
    └── RefreshTokenMapper.xml

**理由**：按特性聚合利于后续微服务拆分；Mapper XML 集中放 resources/mapper/ 便于统一扫描配置。

### 7. 登录入口：单一 `/auth/login` 端点，DTO 区分登录类型
**选择**：请求体携带 `loginType`（PHONE_PASSWORD、PHONE_SMS、EMAIL_PASSWORD、USERNAME_PASSWORD）字段，后端策略模式分发。
**理由**：保持 API 简洁，客户端无需记忆多个端点；策略模式便于扩展新登录方式。

## Risks / Trade-offs

- **验证码 Mock 无持久化** → 服务重启后验证码失效，测试时注意；上线前替换 Redis 实现。
- **MySQL 5.7 语法限制** → 建表 SQL 需避免 8.0 特性（如 `DEFAULT (expr)` 函数默认值、不可见索引）；`utf8mb4` 字符集需在 DDL 中显式指定。
- **MyBatis XML 维护成本** → 字段变更需同步改 POJO + XML Mapper，比 JPA 多一步；后续可引入 MyBatis Generator 缓解。
- **手机号唯一性**：注册时手机号未验证即占位，可能被滥用 → 本期接受，后续加验证码前置校验。
- **JWT 无法主动失效（Access Token）** → Access Token 窗口短（15min）可缓解；如需强失效，后续引入 Token 黑名单（Redis）。

## Migration Plan

1. 更新 `pom.xml` 添加依赖（Spring Security、jjwt、MyBatis Spring Boot Starter、MySQL Connector/J 8.x 兼容 5.7）
2. 配置 `application.properties`（DB 连接、MyBatis mapper-locations、JWT Secret、Token 过期时间）
3. 手动执行 `src/main/resources/db/schema.sql` 在 MySQL 5.7 中建表
4. 按包结构创建代码文件（POJO、Mapper 接口、XML、Service、Controller）
5. 启动服务，验证 `/auth/register`、`/auth/login`、`/auth/refresh` 接口
6. **回滚**：本期全新模块，回滚即回退代码，数据库 drop users 表。

## Open Questions

- 短信服务商选型（阿里云 / 腾讯云）？本期 Mock，上线前确定。
- 用户名命名规则（长度、允许字符）？当前默认 3-20 位字母数字下划线。
- Refresh Token 是否支持滑动续期？当前固定 7 天，可按需调整。
