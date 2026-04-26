## MODIFIED Requirements

### Requirement: 用户登录
系统 SHALL 提供登录页面，支持用户名密码和手机号验证码两种登录方式，通过 Tab 切换。

#### Scenario: 用户名密码登录成功
- **WHEN** 用户在"用户名登录" Tab 输入正确的用户名和密码，点击登录
- **THEN** 系统调用 POST /api/v1/auth/login（loginType=USERNAME_PASSWORD），保存 accessToken / refreshToken，跳转到 /square

#### Scenario: 登录失败
- **WHEN** 用户输入错误的用户名或密码
- **THEN** 系统显示错误提示，不跳转

#### Scenario: 已登录跳转
- **WHEN** 已持有有效 Token 的用户访问登录页
- **THEN** 系统自动重定向到 /square
