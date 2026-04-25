## ADDED Requirements

### Requirement: 手机号+验证码登录
系统 SHALL 允许用户使用手机号和短信验证码登录。验证码有效且手机号已注册时，登录成功并返回 Token 对。

#### Scenario: 手机号+验证码登录成功
- **WHEN** 用户提交已注册手机号和有效验证码，`loginType=PHONE_SMS`
- **THEN** 系统返回 HTTP 200 及 Access Token + Refresh Token

#### Scenario: 手机号未注册
- **WHEN** 用户提交未注册的手机号
- **THEN** 系统返回 HTTP 401，错误码 `USER_NOT_FOUND`

#### Scenario: 验证码错误或已过期
- **WHEN** 验证码与系统记录不匹配或已超过有效期（5 分钟）
- **THEN** 系统返回 HTTP 401，错误码 `INVALID_SMS_CODE`

---

### Requirement: 手机号+密码登录
系统 SHALL 允许用户使用手机号和密码登录。

#### Scenario: 手机号+密码登录成功
- **WHEN** 用户提交已注册手机号和正确密码，`loginType=PHONE_PASSWORD`
- **THEN** 系统返回 HTTP 200 及 Access Token + Refresh Token

#### Scenario: 密码错误
- **WHEN** 用户提交正确手机号但错误密码
- **THEN** 系统返回 HTTP 401，错误码 `INVALID_CREDENTIALS`

---

### Requirement: 邮箱+密码登录
系统 SHALL 允许用户使用邮箱和密码登录。

#### Scenario: 邮箱+密码登录成功
- **WHEN** 用户提交已注册邮箱和正确密码，`loginType=EMAIL_PASSWORD`
- **THEN** 系统返回 HTTP 200 及 Access Token + Refresh Token

#### Scenario: 邮箱未注册
- **WHEN** 用户提交未注册的邮箱
- **THEN** 系统返回 HTTP 401，错误码 `USER_NOT_FOUND`

#### Scenario: 密码错误
- **WHEN** 用户提交正确邮箱但错误密码
- **THEN** 系统返回 HTTP 401，错误码 `INVALID_CREDENTIALS`

---

### Requirement: 用户名+密码登录
系统 SHALL 允许用户使用用户名和密码登录。

#### Scenario: 用户名+密码登录成功
- **WHEN** 用户提交已注册用户名和正确密码，`loginType=USERNAME_PASSWORD`
- **THEN** 系统返回 HTTP 200 及 Access Token + Refresh Token

#### Scenario: 用户名不存在
- **WHEN** 用户提交未注册的用户名
- **THEN** 系统返回 HTTP 401，错误码 `USER_NOT_FOUND`

---

### Requirement: 登录失败不泄露敏感信息
系统 SHALL 对"用户不存在"和"密码错误"返回统一错误码，不区分两种情况，防止用户枚举攻击。

#### Scenario: 密码错误与用户不存在返回相同 HTTP 状态
- **WHEN** 登录请求中手机号/邮箱/用户名不存在，或存在但密码错误
- **THEN** 均返回 HTTP 401，不在响应体中区分具体原因（SMS 验证码场景除外）

---

### Requirement: 登录请求字段校验
系统 SHALL 对所有登录请求进行基础字段格式校验，不合法请求在业务逻辑前拦截。

#### Scenario: 缺少必要字段
- **WHEN** 请求体缺少 `loginType` 或对应的标识字段/凭证字段
- **THEN** 系统返回 HTTP 400，错误码 `MISSING_REQUIRED_FIELD`
