## Why

用户资料目前仅有昵称、性别、出生年月、城市、地址、头像，信息维度不足，用户在广场中难以快速了解彼此。增加职业、自我介绍、择偶标签三个字段，并将"年龄"改为由 `birth_date` 自动计算（前端展示、公开接口返回），提升用户画像的丰富度和匹配参考价值。

## What Changes

- 后端 `users` 表新增三列：`occupation VARCHAR(64)`、`bio TEXT`、`partner_tags VARCHAR(512)`
- 后端 `UserProfileDTO` 新增对应字段及 `age`（由 `birthDate` 计算，只读，不入库）
- 后端公开用户接口 `UserPublicDTO` 同步新增 `occupation`、`bio`、`partnerTags`、`age`
- 后端 `updateProfile` 支持更新新增字段
- 前端个人资料页新增职业输入框、自我介绍文本域、择偶标签多选，年龄改为只读展示（基于 birthDate 计算）
- 前端用户广场卡片和详情页展示年龄（已有 birthDate 计算逻辑，统一到工具函数）

## Capabilities

### New Capabilities

- `user-extended-profile`: 用户扩展资料字段（职业、自我介绍、择偶标签）及年龄自动计算

### Modified Capabilities

- `user-profile`: 接口返回字段新增 occupation、bio、partnerTags、age（只读）

## Impact

- 数据库：`users` 表 DDL 变更，需迁移脚本
- 后端：`User` 实体、`UserProfileDTO`、`UserPublicDTO`、`UserMapper.xml`、`UserService`
- 前端：`ProfileView.vue`（新增字段表单）、`SquareView.vue` / `UserDetailView.vue`（展示 age）、新增 `calcAge` 工具函数统一复用
