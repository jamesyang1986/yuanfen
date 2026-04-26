## ADDED Requirements

### Requirement: 用户详情页
系统 SHALL 提供用户详情页，展示被点击用户的公开资料。

#### Scenario: 点击广场卡片跳转详情页
- **WHEN** 用户在广场页点击某张用户卡片
- **THEN** 系统跳转到 `/user/:id` 路由，展示该用户的详情页

#### Scenario: 详情页加载用户资料
- **WHEN** 用户访问 `/user/:id`
- **THEN** 系统调用 `GET /api/v1/users/{id}`，展示头像、昵称、城市、年龄

#### Scenario: 用户不存在
- **WHEN** 用户访问 `/user/:id`，对应 ID 不存在（API 返回 404）
- **THEN** 系统显示"用户不存在"提示

#### Scenario: 返回广场
- **WHEN** 用户在详情页点击返回按钮
- **THEN** 系统返回用户广场列表页
