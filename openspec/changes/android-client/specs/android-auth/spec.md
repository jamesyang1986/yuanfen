## ADDED Requirements

### Requirement: 用户可通过用户名注册账号
Android 用户 SHALL 能够在注册页面输入用户名和密码（8-32位）完成注册，注册成功后自动登录并跳转到个人资料页。

#### Scenario: 注册成功
- **WHEN** 用户填写合法用户名（3-20位字母/数字/下划线）和密码后点击注册
- **THEN** 客户端调用 `POST /api/v1/auth/register/username`，收到 201 响应，持久化 Token，跳转到个人资料页

#### Scenario: 用户名已存在
- **WHEN** 注册时用户名已被占用，服务端返回 409
- **THEN** 页面显示"用户名已被注册"错误提示，表单保持可编辑状态

#### Scenario: 表单校验不通过
- **WHEN** 用户名为空或密码不足8位时点击注册
- **THEN** 本地校验提示错误信息，不发起网络请求

### Requirement: 用户可通过用户名/密码登录
Android 用户 SHALL 能够在登录页面输入用户名和密码完成登录，登录成功后 Token 持久化，跳转到个人资料页。

#### Scenario: 登录成功
- **WHEN** 用户填写正确的用户名和密码后点击登录
- **THEN** 客户端调用 `POST /api/v1/auth/login`（loginType=USERNAME_PASSWORD），收到 200 响应，持久化 accessToken 和 refreshToken，跳转到个人资料页

#### Scenario: 密码错误
- **WHEN** 密码不正确，服务端返回 401
- **THEN** 页面显示"账号或密码错误"提示

#### Scenario: Token 自动附加
- **WHEN** 客户端发起任何受保护接口请求
- **THEN** OkHttp 拦截器自动在请求头添加 `Authorization: Bearer <accessToken>`

#### Scenario: 未登录跳转
- **WHEN** 应用启动时本地无 Token
- **THEN** 自动跳转到登录页

#### Scenario: 登出
- **WHEN** 用户点击登出按钮
- **THEN** 客户端调用 `POST /api/v1/auth/logout`，清空本地 Token，跳转到登录页
