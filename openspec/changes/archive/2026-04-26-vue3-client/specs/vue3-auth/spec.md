## ADDED Requirements

### Requirement: 用户注册
系统 SHALL 提供注册页面，用户通过用户名和密码完成注册。

#### Scenario: 注册成功
- **WHEN** 用户填写合法用户名（4-32位字母数字）和密码（6-64位），点击注册
- **THEN** 系统调用 POST /api/v1/auth/register/username，成功后保存 Token 并跳转到资料页

#### Scenario: 用户名已存在
- **WHEN** 用户填写已被注册的用户名，点击注册
- **THEN** 系统显示错误提示"用户名已被使用"

#### Scenario: 客户端格式校验
- **WHEN** 用户提交空用户名或不符合格式的密码
- **THEN** 系统在表单字段下方显示校验错误信息，不发起网络请求

### Requirement: 用户登录
系统 SHALL 提供登录页面，使用用户名密码方式登录。

#### Scenario: 登录成功
- **WHEN** 用户输入正确的用户名和密码，点击登录
- **THEN** 系统调用 POST /api/v1/auth/login（loginType=USERNAME_PASSWORD），保存 accessToken / refreshToken，跳转到资料页

#### Scenario: 登录失败
- **WHEN** 用户输入错误的用户名或密码
- **THEN** 系统显示错误提示，不跳转

#### Scenario: 已登录跳转
- **WHEN** 已持有有效 Token 的用户访问登录页
- **THEN** 系统自动重定向到资料页

### Requirement: 路由守卫
系统 SHALL 对需要认证的页面添加路由守卫。

#### Scenario: 未登录访问受保护页面
- **WHEN** 未登录用户访问 /profile 等需要认证的路由
- **THEN** 系统自动跳转到登录页

### Requirement: 退出登录
系统 SHALL 提供退出登录功能。

#### Scenario: 退出登录成功
- **WHEN** 用户点击退出登录
- **THEN** 系统调用 POST /api/v1/auth/logout，清除本地 Token 和用户状态，跳转到登录页
