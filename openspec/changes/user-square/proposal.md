## Why

现有应用只有个人资料功能，用户之间没有发现彼此的入口。用户广场提供所有在线用户的浏览入口，按最近登录时间逆序展示，让用户能快速发现活跃用户并查看其详细资料，提升社交连接的可能性。

## What Changes

- 后端新增"在线用户列表"API，返回所有用户按最后登录时间倒序（分页），包含头像、昵称、城市、出生年月（用于计算年龄）
- 前端新增用户广场页面 `/square`，以卡片网格展示用户列表，支持下拉加载更多
- 前端新增用户详情页 `/user/:id`，展示被点击用户的完整公开资料
- 在顶部导航栏添加"广场"入口

## Capabilities

### New Capabilities

- `user-square-api`: 后端在线用户列表接口（分页、按 last_login_at 倒序）
- `user-square-fe`: 前端用户广场列表页（卡片：头像+昵称+城市+年龄）
- `user-detail-fe`: 前端用户详情页（查看他人公开资料）

### Modified Capabilities

（无现有规格变更）

## Impact

- 后端：新增 `GET /api/v1/users` 接口，需要在 `UserProfileController` 或新建 `UserSquareController`，`UserMapper` 新增分页查询
- 前端：新增 `SquareView.vue`、`UserDetailView.vue`，更新路由配置，添加导航栏组件
- 数据：依赖 `users` 表已有的 `avatar_url`、`nickname`、`city`、`birth_date`、`last_login_at` 字段
