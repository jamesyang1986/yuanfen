## Why

用户模型当前只包含认证相关字段（用户名、邮箱、手机号、密码），缺乏个人资料信息，无法支持社交功能、个性化展示和精准匹配等核心业务场景。

## What Changes

- 在 `users` 表中新增性别、出生年月、居住地址、所在城市、昵称、头像 URL 等字段
- 新增获取和更新用户资料的 API 接口（`GET /api/users/profile`、`PUT /api/users/profile`）
- 更新 `User` 实体类、Mapper、Service 以支持新字段的读写
- 头像字段存储图片 URL（外部存储，本次不包含文件上传功能）

## Capabilities

### New Capabilities

- `user-profile`: 用户个人资料的查看与更新，包含性别、出生年月、居住地址、所在城市、昵称、头像 URL 字段的读写接口

### Modified Capabilities

<!-- 无现有 spec 需要变更 -->

## Impact

- **数据库**: `users` 表新增 6 个可选字段（NULL 允许），需更新 `schema.sql`
- **实体层**: `User.java` 新增对应字段
- **Mapper 层**: `UserMapper.java` 新增 profile 查询/更新 SQL
- **Service 层**: `UserService.java` 新增 profile 读写逻辑
- **Controller 层**: 新增 `UserProfileController.java` 暴露 REST 接口
- **DTO 层**: 新增 `UserProfileDTO.java` 用于请求/响应序列化
- **认证依赖**: 新接口需通过 Spring Security + JWT 鉴权
