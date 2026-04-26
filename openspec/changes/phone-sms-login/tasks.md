## 1. 后端依赖与配置

- [x] 1.1 在 `pom.xml` 中新增 `spring-boot-starter-data-redis` 依赖
- [x] 1.2 在 `src/main/resources/application.properties` 中新增 Redis 连接配置（host/port）

## 2. 后端短信服务重写

- [x] 2.1 重写 `MockSmsServiceImpl`：注入 `StringRedisTemplate`，`sendCode` 写入 `sms:code:{phone}`（TTL 300s）和 `sms:lock:{phone}`（TTL 60s），固定验证码 `123456`
- [x] 2.2 重写 `MockSmsServiceImpl.verifyCode`：从 Redis 取值比对，验证成功后删除 `sms:code:{phone}`

## 3. TokenResponse 新增 newUser 字段

- [x] 3.1 在 `TokenResponse` 类中新增 `private boolean newUser` 字段（含 getter/setter）

## 4. PhoneSmsLoginStrategy 修改

- [x] 4.1 修改 `PhoneSmsLoginStrategy.login()`：验证码通过后，若手机号未注册则自动创建用户（insert）并设置 `newUser=true`，否则 `newUser=false`
- [x] 4.2 在登录成功后调用 `userMapper.updateLastLoginAt(user.getId())`，保持广场排序一致

## 5. 前端 API 层

- [x] 5.1 在 `frontend/src/api/auth.js` 中新增 `sendSmsCode(phone)` 函数（POST /v1/sms/send）
- [x] 5.2 在 `frontend/src/api/auth.js` 中新增 `loginByPhone(phone, code)` 函数（POST /v1/auth/login，loginType=PHONE_SMS）

## 6. 前端登录页改造

- [x] 6.1 改造 `LoginView.vue` 为 Tab 布局，包含"用户名登录"和"手机号登录"两个 Tab
- [x] 6.2 实现手机号登录表单：手机号输入框 + 验证码输入框 + 发送验证码按钮
- [x] 6.3 实现发送验证码 30 秒倒计时逻辑（按钮禁用 + 显示"Xs 后重发"）
- [x] 6.4 在 `onUnmounted` 中清除倒计时 interval，防止内存泄漏
- [x] 6.5 手机号登录成功后根据 `newUser` 字段决定跳转：`true` 跳转 `/profile?welcome=1`，`false` 跳转 `/square`
