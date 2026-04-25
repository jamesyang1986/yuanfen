## ADDED Requirements

### Requirement: 颁发 JWT Token 对
系统 SHALL 在用户注册或登录成功后，颁发 Access Token（有效期 15 分钟）和 Refresh Token（有效期 7 天）。Token 中包含用户 ID 和颁发时间，不包含密码等敏感信息。

#### Scenario: 注册/登录成功后颁发 Token
- **WHEN** 用户注册或登录成功
- **THEN** 系统返回包含 `accessToken`、`refreshToken`、`expiresIn`（秒）字段的响应体

#### Scenario: Token 包含必要载荷
- **WHEN** 系统颁发 Access Token
- **THEN** Token 载荷 MUST 包含 `sub`（用户 ID）、`iat`（颁发时间）、`exp`（过期时间），不得包含密码或验证码

---

### Requirement: 校验 Access Token
系统 SHALL 对需要认证的接口进行 JWT Access Token 校验。Token 非法或已过期时拒绝请求。

#### Scenario: 合法 Token 放行
- **WHEN** 请求头携带有效的 `Authorization: Bearer <token>`
- **THEN** 系统解析 Token，将用户信息注入 SecurityContext，放行请求

#### Scenario: Token 已过期
- **WHEN** 请求头携带已过期的 Access Token
- **THEN** 系统返回 HTTP 401，错误码 `TOKEN_EXPIRED`

#### Scenario: Token 签名非法
- **WHEN** 请求头携带签名被篡改的 Token
- **THEN** 系统返回 HTTP 401，错误码 `INVALID_TOKEN`

#### Scenario: 未携带 Token
- **WHEN** 请求头不包含 `Authorization` 字段
- **THEN** 系统返回 HTTP 401，错误码 `UNAUTHORIZED`

---

### Requirement: 刷新 Access Token
系统 SHALL 提供 `/auth/refresh` 接口，接受有效的 Refresh Token，颁发新的 Access Token。Refresh Token 本身不续期。

#### Scenario: 刷新成功
- **WHEN** 用户提交有效且未过期的 Refresh Token
- **THEN** 系统返回新的 Access Token（15 分钟有效期），Refresh Token 不变

#### Scenario: Refresh Token 已过期
- **WHEN** 用户提交已过期的 Refresh Token
- **THEN** 系统返回 HTTP 401，错误码 `REFRESH_TOKEN_EXPIRED`，用户须重新登录

#### Scenario: Refresh Token 非法
- **WHEN** 用户提交格式错误或签名非法的 Refresh Token
- **THEN** 系统返回 HTTP 401，错误码 `INVALID_REFRESH_TOKEN`

---

### Requirement: Refresh Token 持久化存储
系统 SHALL 将 Refresh Token 存储于数据库（`refresh_tokens` 表），支持主动失效（登出场景）。

#### Scenario: 登出使 Refresh Token 失效
- **WHEN** 用户调用登出接口
- **THEN** 系统从数据库中删除或标记对应 Refresh Token 为已撤销，后续使用该 Token 刷新时返回 HTTP 401

#### Scenario: 使用已撤销的 Refresh Token
- **WHEN** 用户使用已登出的 Refresh Token 请求刷新
- **THEN** 系统返回 HTTP 401，错误码 `REFRESH_TOKEN_REVOKED`
