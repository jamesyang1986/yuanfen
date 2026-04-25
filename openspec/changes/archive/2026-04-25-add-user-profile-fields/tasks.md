## 1. 数据库变更

- [x] 1.1 在 `schema.sql` 的 `users` 表中新增 `nickname`、`gender`、`birth_date`、`city`、`address`、`avatar_url` 字段定义
- [x] 1.2 创建 `src/main/resources/db/migration_add_user_profile_fields.sql`，包含 `ALTER TABLE users ADD COLUMN IF NOT EXISTS ...` 语句，供现有数据库实例手动执行

## 2. 实体与枚举

- [x] 2.1 在 `User.java` 中新增 6 个字段：`nickname (String)`、`gender (Integer)`、`birthDate (LocalDate)`、`city (String)`、`address (String)`、`avatarUrl (String)`，并添加 MyBatis 列名注解/映射
- [x] 2.2 创建 `Gender.java` 枚举（UNKNOWN=0, MALE=1, FEMALE=2, OTHER=3），用于业务层校验（数据库仍存 TINYINT）

## 3. DTO 层

- [x] 3.1 创建 `UserProfileDTO.java`，包含 6 个 profile 字段，`birthDate` 使用 `String` 类型接收 `YYYY-MM` 格式，并添加 `@Size`/`@Pattern` 校验注解
- [x] 3.2 在 `UserProfileDTO` 中实现 `birthDate` 转换辅助方法：`YYYY-MM` → `LocalDate`（补全为每月 1 日）

## 4. Mapper 层

- [x] 4.1 在 `UserMapper.java` 中新增 `selectProfileById(Long id)` 方法（仅查询 profile 字段 + id）
- [x] 4.2 在 `UserMapper.java` 中新增 `updateProfile(User user)` 方法，使用动态 SQL（`<set>` 标签）只更新非 null 字段

## 5. Service 层

- [x] 5.1 在 `UserService.java` 中新增 `getUserProfile(Long userId)` 方法，查询并返回 `UserProfileDTO`
- [x] 5.2 在 `UserService.java` 中新增 `updateUserProfile(Long userId, UserProfileDTO dto)` 方法，将 DTO 转换为 User 实体后调用 Mapper 更新

## 6. Controller 层

- [x] 6.1 创建 `UserProfileController.java`，注册路由 `GET /api/users/profile` 和 `PUT /api/users/profile`
- [x] 6.2 在 Controller 中通过 `SecurityContextHolder` 提取当前登录用户 ID，传入 Service 方法
- [x] 6.3 在 `PUT` 接口中使用 `@Valid` 触发 DTO 字段校验，并统一处理 `MethodArgumentNotValidException` 返回 400

## 7. 安全配置

- [x] 7.1 在 Spring Security 配置中将 `/api/users/profile` 路径加入 `authenticated()` 规则（确保 JWT 过滤器已覆盖该路径）

## 8. 测试

- [x] 8.1 编写 `UserProfileControllerTest`，覆盖：未登录返回 401、GET 返回 200 含 null 字段、PUT 更新成功返回 200
- [x] 8.2 验证 `PUT /api/users/profile` 传入非法 `birthDate` 格式返回 400
- [x] 8.3 验证 `PUT /api/users/profile` 传入超长 `nickname`（>32字符）返回 400
