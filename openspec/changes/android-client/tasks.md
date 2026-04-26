## 1. 工程初始化

- [x] 1.1 在项目根目录创建 `android/` Android 工程（minSdk 24，targetSdk 34，原生 Java，包名 `com.yf.yuanfen`）
- [x] 1.2 在 `app/build.gradle` 添加依赖：Retrofit2、OkHttp3、Gson converter、Glide、CircleImageView
- [x] 1.3 创建 `res/xml/network_security_config.xml`，允许 `10.0.2.2` 和 `localhost` 明文 HTTP
- [x] 1.4 在 `AndroidManifest.xml` 声明网络权限、图片读取权限（`READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE`）及 `networkSecurityConfig`

## 2. 网络层

- [x] 2.1 创建 `network/NetworkConfig.java`，定义 `BASE_URL = "http://10.0.2.2:8080"`
- [x] 2.2 创建 `network/TokenManager.java`，封装 SharedPreferences 的 Token 读写（`access_token`、`refresh_token`）
- [x] 2.3 创建 `network/AuthInterceptor.java`，从 `TokenManager` 读取 accessToken 并自动添加 `Authorization` 请求头
- [x] 2.4 创建 `network/RetrofitClient.java`，构建带 `AuthInterceptor` 的 OkHttpClient 和 Retrofit 单例

## 3. API 接口定义

- [x] 3.1 创建 `api/AuthApi.java`，声明：`registerByUsername`、`login`、`refresh`、`logout`
- [x] 3.2 创建 `api/UserApi.java`，声明：`getProfile`、`updateProfile`、`uploadAvatar`（`@Multipart`）
- [x] 3.3 创建对应请求/响应 DTO：`UsernameRegisterRequest`、`LoginRequest`、`TokenResponse`、`ApiResponse<T>`、`UserProfileDTO`、`AvatarResponse`

## 4. 注册页面

- [x] 4.1 创建 `activity/RegisterActivity.java` 及布局 `activity_register.xml`（用户名输入框、密码输入框、注册按钮、跳转登录链接）
- [x] 4.2 实现本地校验（用户名格式、密码长度）
- [x] 4.3 调用 `AuthApi.registerByUsername`，成功后保存 Token，启动 `ProfileActivity`，失败显示错误 Toast

## 5. 登录页面

- [x] 5.1 创建 `activity/LoginActivity.java` 及布局 `activity_login.xml`（用户名、密码、登录按钮、跳转注册链接）
- [x] 5.2 应用启动逻辑：`MainActivity` 检测 Token 存在则跳 `ProfileActivity`，否则跳 `LoginActivity`
- [x] 5.3 调用 `AuthApi.login`（loginType=USERNAME_PASSWORD），成功后保存 Token，启动 `ProfileActivity`，失败显示错误 Toast

## 6. 个人资料页面

- [x] 6.1 创建 `activity/ProfileActivity.java` 及布局 `activity_profile.xml`（CircleImageView 头像、昵称、性别、出生年月、城市、地址输入控件、保存按钮、登出按钮）
- [x] 6.2 进入页面时调用 `UserApi.getProfile`，将返回字段填充到控件
- [x] 6.3 实现性别单选弹窗（AlertDialog，选项：未知/男/女/其他）
- [x] 6.4 实现出生年月 DatePickerDialog（年月选择，结果格式化为 YYYY-MM）
- [x] 6.5 点击保存调用 `UserApi.updateProfile`，成功后重新加载资料并 Toast 提示

## 7. 头像上传

- [x] 7.1 头像区域点击启动相册选择（`ActivityResultLauncher`，`ACTION_PICK`）
- [x] 7.2 将选取的图片 Uri 转为临时文件，以 `multipart/form-data` 调用 `UserApi.uploadAvatar`
- [x] 7.3 上传成功后用 Glide 加载返回的 `avatarUrl` 更新 CircleImageView，Toast 提示"头像上传成功"
- [x] 7.4 处理 400 错误（类型不支持 / 超限），Toast 显示服务端错误信息

## 8. 登出

- [x] 8.1 点击登出按钮调用 `AuthApi.logout`（携带 refreshToken），清空 `TokenManager` 中的 Token，跳转到 `LoginActivity` 并清除返回栈
