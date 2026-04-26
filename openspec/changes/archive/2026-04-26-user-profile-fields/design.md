## Context

`users` 表已有 `birth_date DATE` 字段，年龄可直接计算（当前年 - 出生年，简单近似）。现有 `UserProfileDTO` 和 `UserPublicDTO` 均需扩展。前端 `SquareView` 和 `UserDetailView` 已有 `calcAge` 内联函数，存在重复；本次一并提取为公共工具函数。

择偶标签为多选有限集（如：三观相合、爱好运动、独立自主…），以逗号分隔字符串存储于 `partner_tags VARCHAR(512)`，前后端以字符串数组互转。

## Goals / Non-Goals

**Goals:**
- DB 新增 `occupation`、`bio`、`partner_tags` 三列，提供迁移 SQL
- 后端 DTO 新增对应字段，`age` 字段只读（Java 端计算，不入库）
- `updateProfile` 的 MyBatis 动态 SQL 支持新字段
- 前端 `ProfileView` 新增表单项；`SquareView`/`UserDetailView` 展示 `age`
- 提取 `src/utils/age.js` 统一 `calcAge` 函数

**Non-Goals:**
- 不实现标签管理后台（标签集硬编码在前端）
- 不实现按标签搜索/过滤
- 不做字段隐私设置

## Decisions

### D1: partner_tags 存储为逗号分隔字符串
避免引入关联表，VARCHAR(512) 足够存储约 20 个标签。后端 DTO 以 `List<String>` 暴露，Service 层做 join/split 转换。

### D2: age 只读，Java 端计算
`age = Year.now() - birthDate.getYear()`，仅在 DTO 序列化时计算，不写库，避免定时任务维护。`birthDate` 为 null 时 age 返回 null。

### D3: 择偶标签候选集在前端硬编码
10-15 个预设标签（如：三观相合、爱好运动、喜欢旅行、宅家宅男/女、爱宠物…），使用 `el-checkbox-group` 多选，上限 5 个。

### D4: 迁移 SQL 单独文件
新建 `src/main/resources/db/migration_add_user_extended_fields.sql`，只包含 `ALTER TABLE` 语句，不修改 `schema.sql`（schema.sql 保持完整建表用于全新环境）。同时在 `schema.sql` 中补齐新列定义。

## Risks / Trade-offs

- [partner_tags 拼接] 逗号分隔不支持含逗号的标签内容 → 候选标签集不含逗号，前端限制输入
- [age 精度] 只精确到年，不考虑月份 → 对社交场景够用
