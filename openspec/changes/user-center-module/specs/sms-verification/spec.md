## ADDED Requirements

### Requirement: 发送短信验证码
系统 SHALL 提供发送短信验证码接口，向指定手机号发送 6 位随机数字验证码，验证码有效期 5 分钟。本期使用 Mock 实现（控制台打印），预留真实短信服务接口。

#### Scenario: 发送验证码成功
- **WHEN** 用户提交合法手机号请求发送验证码
- **THEN** 系统生成 6 位验证码，存储至内存（Mock）并记录过期时间，返回 HTTP 200

#### Scenario: 手机号格式非法
- **WHEN** 用户提交的手机号不符合 11 位纯数字格式
- **THEN** 系统返回 HTTP 400，错误码 `INVALID_PHONE_FORMAT`，不发送验证码

#### Scenario: 发送频率限制
- **WHEN** 同一手机号在 60 秒内再次请求发送验证码
- **THEN** 系统返回 HTTP 429，错误码 `SMS_SEND_TOO_FREQUENT`，不重新生成验证码

---

### Requirement: 校验短信验证码
系统 SHALL 提供验证码校验能力（内部服务调用，非独立对外接口），供注册和登录流程使用。验证码校验通过后立即失效（一次性）。

#### Scenario: 验证码校验通过
- **WHEN** 提供的手机号和验证码与存储记录匹配，且未超过有效期（5 分钟）
- **THEN** 校验结果返回成功，验证码从存储中删除

#### Scenario: 验证码已过期
- **WHEN** 提供的验证码存在但已超过 5 分钟有效期
- **THEN** 校验结果返回失败，错误码 `SMS_CODE_EXPIRED`，从存储中删除过期验证码

#### Scenario: 验证码不存在或错误
- **WHEN** 提供的手机号无验证码记录，或验证码与记录不匹配
- **THEN** 校验结果返回失败，错误码 `INVALID_SMS_CODE`

---

### Requirement: 短信服务接口隔离
系统 SHALL 通过 `SmsService` 接口封装短信发送能力，Mock 实现与真实实现可通过 Spring Profile 切换，上层业务代码不感知具体实现。

#### Scenario: Mock 实现替换为真实短信服务
- **WHEN** 切换 Spring Profile 至生产环境
- **THEN** 系统自动使用真实短信服务实现，无需修改业务代码
