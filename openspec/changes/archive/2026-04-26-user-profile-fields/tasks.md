## 1. 数据库迁移

- [x] 1.1 在 `schema.sql` 的 users 表定义中补充新列：`occupation VARCHAR(64)`、`bio TEXT`、`partner_tags VARCHAR(512)`
- [x] 1.2 新建 `src/main/resources/db/migration_add_user_extended_fields.sql`，包含 `ALTER TABLE users ADD COLUMN` 语句

## 2. 后端实体与 Mapper

- [x] 2.1 在 `User.java` 新增字段：`occupation`（String）、`bio`（String）、`partnerTags`（String，存原始逗号串）
- [x] 2.2 在 `UserMapper.xml` 的 `UserResultMap` 新增三列映射；`selectProfileById` SELECT 列表补充新列；`updateProfile` 动态 SQL 补充三列

## 3. 后端 DTO 与 Service

- [x] 3.1 在 `UserProfileDTO.java` 新增 `occupation`、`bio`、`partnerTags`（List\<String\>）、`age`（Integer，只读）字段及 getter/setter
- [x] 3.2 在 `UserPublicDTO.java` 新增 `occupation`、`bio`、`partnerTags`（List\<String\>）、`age`（Integer）字段
- [x] 3.3 在 `UserService.getUserProfile` 中补充新字段映射，并计算 `age`（`Year.now().getValue() - birthDate.getYear()`，birthDate 为 null 时 age=null）；`partnerTags` 由逗号串转 List
- [x] 3.4 在 `UserService.updateUserProfile` 中支持写入 `occupation`、`bio`；`partnerTags` 由 List 转逗号串写入 User 实体
- [x] 3.5 在 `UserService.toPublicDTO` 中补充新字段映射及 age 计算

## 4. 前端工具函数

- [x] 4.1 新建 `src/utils/age.js`，导出 `calcAge(birthDate)` 函数（birthDate 为 YYYY-MM 串，返回年龄整数或 null）
- [x] 4.2 在 `SquareView.vue` 中移除内联 `calcAge`，改为使用 API 返回的 `user.age`
- [x] 4.3 在 `UserDetailView.vue` 中移除内联 `calcAge`，改为使用 API 返回的 `user.age`

## 5. 前端个人资料页

- [x] 5.1 在 `ProfileView.vue` 的 `form` 中新增 `occupation`、`bio`、`partnerTags`（数组）、`age`（只读）字段
- [x] 5.2 在 `loadProfile` 中将新字段从接口响应填充到 form（`partnerTags` 直接赋数组，`age` 展示）
- [x] 5.3 在 `save()` 中将 `occupation`、`bio`、`partnerTags` 加入提交 fields（`partnerTags` 传数组，由后端转换）
- [x] 5.4 在模板中新增职业输入框（`el-input`，maxlength=64）
- [x] 5.5 在模板中新增自我介绍文本域（`el-input` type=textarea，maxlength=500，showWordLimit）
- [x] 5.6 在模板中新增择偶标签多选（`el-checkbox-group`，预设标签，最多 5 个）
- [x] 5.7 在模板中新增年龄只读展示（基于 form.birthDate 用 calcAge 计算显示，birthDate 为空时显示"请先填写出生年月"）

## 6. 前端广场与详情页

- [x] 6.1 在 `UserDetailView.vue` 模板中新增职业、自我介绍、择偶标签展示
