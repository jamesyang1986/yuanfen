## ADDED Requirements

### Requirement: 用户列表接口
系统 SHALL 提供分页用户列表接口，按最后登录时间降序返回所有用户的公开信息。

#### Scenario: 获取第一页用户列表
- **WHEN** 已认证用户请求 `GET /api/v1/users?page=0&size=20`
- **THEN** 系统返回 HTTP 200，data 包含用户数组（字段：id、nickname、avatarUrl、city、birthDate）及分页信息（total、page、size），按 last_login_at 降序排列

#### Scenario: 翻页获取后续用户
- **WHEN** 已认证用户请求 `GET /api/v1/users?page=1&size=20`
- **THEN** 系统返回第二页用户数据，结果不与第一页重叠

#### Scenario: 未认证访问
- **WHEN** 未携带 Token 的请求访问 `GET /api/v1/users`
- **THEN** 系统返回 HTTP 401

### Requirement: 用户详情接口
系统 SHALL 提供按 ID 查询单个用户公开资料的接口。

#### Scenario: 查询存在的用户
- **WHEN** 已认证用户请求 `GET /api/v1/users/{id}`，该用户存在
- **THEN** 系统返回 HTTP 200，data 包含该用户的 id、nickname、avatarUrl、city、birthDate

#### Scenario: 查询不存在的用户
- **WHEN** 已认证用户请求 `GET /api/v1/users/{id}`，该 ID 不存在
- **THEN** 系统返回 HTTP 404
