## ADDED Requirements

### Requirement: 手机号注册
系统 SHALL 允许用户通过手机号、验证码和密码完成注册。注册前须验证手机号格式合法、验证码有效，且该手机号未被注册。注册成功后自动创建用户记录并返回 JWT Token 对。

#### Scenario: 手机号注册成功
- **WHEN** 用户提交合法手机号、有效验证码、符合规则的密码
- **THEN** 系统创建用户记录，返回 HTTP 201 及 Access Token + Refresh Token

#### Scenario: 手机号已被注册
- **WHEN** 用户提交的手机号已存在于系统
- **THEN** 系统返回 HTTP 409，错误码 `PHONE_ALREADY_EXISTS`

#### Scenario: 验证码无效或已过期
- **WHEN** 用户提交的验证码与系统记录不匹配或已超过有效期（5 分钟）
- **THEN** 系统返回 HTTP 400，错误码 `INVALID_SMS_CODE`

#### Scenario: 手机号格式非法
- **WHEN** 用户提交的手机号不符合 11 位纯数字格式（中国大陆）
- **THEN** 系统返回 HTTP 400，错误码 `INVALID_PHONE_FORMAT`

#### Scenario: 密码不符合规则
- **WHEN** 密码长度不在 8-32 位之间
- **THEN** 系统返回 HTTP 400，错误码 `INVALID_PASSWORD_FORMAT`

---

### Requirement: 邮箱注册
系统 SHALL 允许用户通过邮箱和密码完成注册。注册前须验证邮箱格式合法且未被注册。

#### Scenario: 邮箱注册成功
- **WHEN** 用户提交合法邮箱和符合规则的密码
- **THEN** 系统创建用户记录，返回 HTTP 201 及 Access Token + Refresh Token

#### Scenario: 邮箱已被注册
- **WHEN** 用户提交的邮箱已存在于系统
- **THEN** 系统返回 HTTP 409，错误码 `EMAIL_ALREADY_EXISTS`

#### Scenario: 邮箱格式非法
- **WHEN** 用户提交的邮箱不符合标准邮箱格式
- **THEN** 系统返回 HTTP 400，错误码 `INVALID_EMAIL_FORMAT`

---

### Requirement: 用户名注册
系统 SHALL 允许用户通过用户名和密码完成注册。用户名须符合命名规则且在系统中唯一。

#### Scenario: 用户名注册成功
- **WHEN** 用户提交合法用户名和符合规则的密码
- **THEN** 系统创建用户记录，返回 HTTP 201 及 Access Token + Refresh Token

#### Scenario: 用户名已被注册
- **WHEN** 用户提交的用户名已存在于系统（大小写不敏感）
- **THEN** 系统返回 HTTP 409，错误码 `USERNAME_ALREADY_EXISTS`

#### Scenario: 用户名格式非法
- **WHEN** 用户名不满足 3-20 位字母/数字/下划线规则
- **THEN** 系统返回 HTTP 400，错误码 `INVALID_USERNAME_FORMAT`

---

### Requirement: 注册字段唯一性约束
系统 SHALL 在数据库层对 phone、email、username 字段分别设置唯一索引，防止并发注册产生重复数据。

#### Scenario: 并发注册同一手机号
- **WHEN** 两个请求同时以相同手机号注册
- **THEN** 只有一个请求成功，另一个返回 HTTP 409
