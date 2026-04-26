## Context

后端已有 `users` 表，包含 `avatar_url`、`nickname`、`city`、`birth_date`、`last_login_at` 字段。前端已有 Vue 3 + Element Plus 工程，路由和认证体系完备。本次扩展需要在后端新增列表查询接口，在前端新增两个页面（广场列表、用户详情）。

## Goals / Non-Goals

**Goals:**
- 后端提供分页的用户列表接口，按 `last_login_at` 降序，不过滤"在线"状态（无 WebSocket，以登录时间逆序近似"活跃"）
- 前端广场页用卡片网格展示用户，每张卡片显示头像、昵称、城市、年龄（由 birth_date 计算）
- 支持点击卡片跳转用户详情页
- 用户详情页复用 getProfile 接口或新增 getUserById 接口展示他人资料
- 导航栏添加"广场"链接

**Non-Goals:**
- 不实现真实在线状态（WebSocket / 心跳）
- 不实现用户间互动（点赞、私信、关注）
- 不实现搜索过滤

## Decisions

### D1: 后端新增 GET /api/v1/users 接口
返回字段：`id`、`nickname`、`avatarUrl`、`city`、`birthDate`，按 `last_login_at` DESC 分页（`page`/`size` 参数）。复用 `UserProfileController` 或新建 `UserSquareController`，新增 `UserMapper.listUsers` 方法。

### D2: 年龄在前端计算
`birthDate` 格式为 `YYYY-MM`，前端用当前年份减去出生年份得出大致年龄（精确到年），不在后端计算，保持接口简洁。

### D3: 用户详情复用 getUserById 接口
新增 `GET /api/v1/users/{id}` 接口，返回目标用户的公开资料（同上字段集），供详情页使用。不复用 `/profile`（/profile 返回当前登录用户自己的数据）。

### D4: 前端分页用滚动加载（基础分页按钮）
使用 Element Plus `el-pagination` 组件，简单页码翻页，不做无限滚动（后续可扩展）。

### D5: 导航栏提取为独立组件
新建 `AppNav.vue` 包含"广场" / "我的资料"两个导航项，在 `App.vue` 中引入，登录状态下显示。

## Risks / Trade-offs

- [无真实在线状态] 按 last_login_at 排序近似"活跃度"，但不代表实时在线 → 在 UI 文案用"最近活跃"而非"在线"
- [birth_date 可为空] 部分用户未填出生年月 → 年龄显示为"未知"
- [隐私] 用户详情页展示他人城市/年龄 → 当前 MVP 阶段全量公开，后续可加隐私设置
