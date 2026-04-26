## ADDED Requirements

### Requirement: 手机号验证码登录
系统 SHALL 提供手机号验证码登录方式，用户输入手机号并获取验证码后完成登录；若手机号未注册则自动创建账号并引导完善资料。

#### Scenario: 发送验证码成功
- **WHEN** 用户在手机号登录 Tab 输入合法手机号并点击"发送验证码"
- **THEN** 系统调用 POST /api/v1/sms/send，成功后按钮进入 30 秒冷却倒计时并显示"Xs 后重发"，期间按钮禁用

#### Scenario: 发送验证码冷却中
- **WHEN** 用户在 30 秒冷却期内再次点击"发送验证码"
- **THEN** 按钮处于禁用状态，不发起网络请求

#### Scenario: 老用户验证码登录成功
- **WHEN** 已注册手机号用户输入正确验证码（Mock 固定为 123456）并点击登录
- **THEN** 系统调用 POST /api/v1/auth/login（loginType=PHONE_SMS），保存 Token，根据响应 newUser=false 跳转到 /square

#### Scenario: 新用户首次手机号登录
- **WHEN** 未注册手机号用户输入正确验证码并点击登录
- **THEN** 系统调用登录接口，后端自动创建账号，响应 newUser=true，前端跳转到 /profile?welcome=1

#### Scenario: 验证码错误
- **WHEN** 用户输入错误验证码并点击登录
- **THEN** 系统显示错误提示，不跳转，不清空已填写内容

#### Scenario: 手机号为空时点击发送
- **WHEN** 用户未填写手机号点击"发送验证码"
- **THEN** 前端校验拦截，显示"请输入手机号"提示，不发起网络请求

### Requirement: 登录页 Tab 切换
系统 SHALL 在登录页提供"用户名登录"和"手机号登录"两个 Tab，默认显示用户名登录。

#### Scenario: 切换到手机号登录 Tab
- **WHEN** 用户点击"手机号登录" Tab
- **THEN** 页面切换到手机号输入 + 验证码输入表单，用户名登录表单隐藏

#### Scenario: 切换回用户名登录 Tab
- **WHEN** 用户从手机号登录 Tab 切换回"用户名登录" Tab
- **THEN** 页面切换回用户名/密码表单，手机号登录表单隐藏
