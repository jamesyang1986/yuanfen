## Why

现有手机号登录后端基础设施已存在（`PhoneSmsLoginStrategy`、`MockSmsServiceImpl`、`SmsController`），但存在三个关键缺陷：验证码存储在 JVM 内存（多实例不共享）、验证码随机生成（测试不可预测）、手机号未注册时直接报错而非自动引导注册。前端完全缺少手机号登录入口。本次将三项问题一并修复，并添加完整前端交互。

## What Changes

- **后端**：
  - 新增 Redis 依赖，将验证码存储从 `ConcurrentHashMap` 迁移到 Redis（TTL 5 分钟，发送间隔 60 秒）
  - Mock 模式下验证码固定为 `123456`（方便测试），正式接入真实短信商时只需替换实现类
  - `TokenResponse` 新增 `newUser` 布尔字段，供前端判断是否跳转资料完善页
  - `PhoneSmsLoginStrategy`：验证码通过后若手机号未注册，自动创建用户，返回 `newUser=true`
- **前端**：
  - 登录页新增「手机号登录」Tab，与用户名登录并列
  - 发送验证码按钮支持 30 秒倒计时（冷却期内禁用）
  - 手机号登录成功后，根据 `newUser` 决定跳转 `/profile?welcome=1` 或 `/square`

## Capabilities

### New Capabilities

- `phone-sms-login-fe`: 前端手机号验证码登录页面（Tab 切换、倒计时、自动注册跳转）

### Modified Capabilities

- `vue3-auth`: 登录页增加手机号登录 Tab

## Impact

- 后端：`pom.xml` 新增 `spring-boot-starter-data-redis`；`application.properties` 新增 Redis 配置；`MockSmsServiceImpl` 重写；`PhoneSmsLoginStrategy` 修改；`TokenResponse` 新增字段
- 前端：`LoginView.vue` 改造为 Tab 布局；`src/api/auth.js` 新增 `sendSms` 函数
- 依赖：本地需运行 Redis（默认 `localhost:6379`）
