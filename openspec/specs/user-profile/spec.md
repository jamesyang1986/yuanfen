### Requirement: 用户可查看自己的个人资料
已认证用户 SHALL 能够通过 `GET /api/users/profile` 获取自己的个人资料，包含性别、出生年月、居住地址、所在城市、昵称、头像 URL 等字段。

#### Scenario: 已登录用户获取个人资料
- **WHEN** 已认证用户发送 `GET /api/users/profile` 请求（携带有效 JWT）
- **THEN** 系统返回 HTTP 200，响应体包含 `nickname`、`gender`、`birthDate`、`city`、`address`、`avatarUrl` 字段（未填写的字段返回 null）

#### Scenario: 未登录用户访问个人资料接口
- **WHEN** 未携带 JWT 或 JWT 无效的请求访问 `GET /api/users/profile`
- **THEN** 系统返回 HTTP 401 Unauthorized

### Requirement: 用户可更新自己的个人资料
已认证用户 SHALL 能够通过 `PUT /api/users/profile` 更新自己的个人资料字段，所有字段均为可选。`avatarUrl` 字段可直接传入 URL 字符串，也可通过文件上传接口（`POST /api/users/avatar`）上传图片后获得 URL 再填入此字段。

#### Scenario: 用户更新昵称和头像 URL
- **WHEN** 已认证用户发送 `PUT /api/users/profile`，请求体包含 `{"nickname": "小明", "avatarUrl": "https://example.com/avatar.jpg"}`
- **THEN** 系统返回 HTTP 200，响应体中 `nickname` 为 "小明"，`avatarUrl` 为提交的 URL，其他字段保持不变

#### Scenario: 用户更新性别字段
- **WHEN** 已认证用户发送 `PUT /api/users/profile`，请求体包含 `{"gender": 1}`（1=男）
- **THEN** 系统返回 HTTP 200，响应体中 `gender` 为 1

#### Scenario: 用户提交无效的出生日期格式
- **WHEN** 已认证用户发送 `PUT /api/users/profile`，请求体包含 `{"birthDate": "not-a-date"}`
- **THEN** 系统返回 HTTP 400，响应体包含字段校验错误信息

#### Scenario: 用户提交超长昵称
- **WHEN** 已认证用户发送 `PUT /api/users/profile`，请求体中 `nickname` 长度超过 32 个字符
- **THEN** 系统返回 HTTP 400，响应体包含 `nickname` 字段校验错误

#### Scenario: 未登录用户尝试更新个人资料
- **WHEN** 未携带有效 JWT 的请求访问 `PUT /api/users/profile`
- **THEN** 系统返回 HTTP 401 Unauthorized

### Requirement: 个人资料字段约束
系统 SHALL 对个人资料字段进行如下约束：
- `nickname`：VARCHAR(32)，可为 null
- `gender`：TINYINT（0=未知, 1=男, 2=女, 3=其他），可为 null
- `birthDate`：DATE 类型，客户端传 `YYYY-MM` 格式，服务端存为每月 1 日，可为 null
- `city`：VARCHAR(64)，可为 null
- `address`：VARCHAR(256)，可为 null
- `avatarUrl`：VARCHAR(512)，可为 null

#### Scenario: 所有字段均未填写时获取资料
- **WHEN** 新注册用户（未填写任何 profile 信息）调用 `GET /api/users/profile`
- **THEN** 系统返回 HTTP 200，所有 profile 字段均为 null
