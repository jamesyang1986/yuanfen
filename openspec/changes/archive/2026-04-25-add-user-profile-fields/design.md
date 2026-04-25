## Context

当前 `users` 表仅存储认证相关字段（username、email、phone、password_hash、created_at、updated_at）。业务需要展示用户个人资料以支持社交功能，需在现有表中扩展字段，并暴露对应的 REST API。

技术栈：Spring Boot 2.7 + MyBatis + MySQL 5.7，认证使用 Spring Security + JWT。

## Goals / Non-Goals

**Goals:**
- 在 `users` 表新增 6 个可选 profile 字段（性别、出生年月、居住地址、所在城市、昵称、头像 URL）
- 暴露 `GET /api/users/profile` 和 `PUT /api/users/profile` 两个受保护接口
- 头像字段存储外部图片 URL 字符串

**Non-Goals:**
- 文件上传/图片托管（头像仅存 URL）
- 多版本 schema 迁移工具（项目使用 schema.sql 初始化）
- 用户搜索或按字段过滤

## Decisions

### 1. 字段存放位置：在 `users` 表直接扩展，而非新建 `user_profiles` 表

**决策**：将新字段直接加入 `users` 表。  
**理由**：用户量级未知，当前无分表需求；单表读取避免 JOIN，MyBatis 映射更简单；所有字段均可为 NULL，不影响现有注册/登录流程。  
**备选**：新建 `user_profiles` 表一对一关联。若未来字段超过 20 个或需要分库，可再拆分。

### 2. 性别字段类型：`TINYINT` 枚举（0=未知, 1=男, 2=女, 3=其他）

**决策**：使用 `TINYINT` 存储枚举值，Java 侧定义 `Gender` 枚举类映射。  
**理由**：节省存储空间，查询高效，支持国际化；避免 MySQL `ENUM` 类型在 ALTER TABLE 时的锁表问题。

### 3. 出生年月：`DATE` 类型（精度到月即可，存为每月 1 日）

**决策**：使用 `DATE` 类型，客户端传 `YYYY-MM` 格式，服务端补全为 `YYYY-MM-01` 存储。  
**理由**：`DATE` 类型排序/计算年龄方便；只要求年月精度，补日不影响业务语义。

### 4. 接口鉴权：复用现有 Spring Security JWT 过滤器

**决策**：`/api/users/profile` 接口加入 `authenticated()` 规则，从 JWT 中提取 userId。  
**理由**：无需额外鉴权中间件，与现有 `/api/auth/**` 保持一致的安全模型。

## Risks / Trade-offs

- **schema.sql 不支持增量迁移** → 现有数据库实例需手动执行 ALTER TABLE；新部署走 schema.sql 无问题。建议在 schema.sql 中用 `ALTER TABLE ... ADD COLUMN IF NOT EXISTS` 或提供单独的 migration SQL 文件。
- **头像 URL 无校验** → 恶意用户可存入任意 URL；后续可加正则/域名白名单校验。
- **出生日期隐私** → API 返回完整 birthDate；如有隐私需求，后续可按关系等级脱敏。

## Migration Plan

1. 执行 `ALTER TABLE users ADD COLUMN ...`（或重建 schema.sql 用于新环境）
2. 部署新版应用（新字段默认 NULL，旧数据兼容）
3. 回滚：新字段为 NULL 可选，删除列不影响认证流程

## Open Questions

- 头像 URL 是否需要长度限制（当前建议 512 字符）？
- 出生日期是否需要对外隐藏精确日期，只展示年龄？
