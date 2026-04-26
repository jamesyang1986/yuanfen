## Why

后端 API 已具备完整的认证、用户资料及头像上传能力，但目前缺少移动端接入层。创建 Android 原生客户端（Java），让用户能够在手机上完成注册、登录、个人资料管理和头像上传等核心操作。

## What Changes

- 新建独立 Android 工程（原生 Java，minSdk 24，targetSdk 34）
- 集成 Retrofit2 + OkHttp3 对接后端 REST API（基于 `http://localhost:8080`）
- 实现用户名注册界面与逻辑
- 实现用户名/密码登录界面与逻辑，Token 持久化到 SharedPreferences
- 实现查看与编辑个人资料界面（昵称、性别、出生年月、城市、地址）
- 实现头像上传功能（从相册选取图片，multipart 上传）
- 全局 OkHttp 拦截器自动附加 Bearer Token

## Capabilities

### New Capabilities
- `android-auth`: Android 端注册（用户名）与登录（用户名/密码）流程，Token 存储与自动附加
- `android-profile`: Android 端个人资料查看与编辑，头像选取与上传

### Modified Capabilities
（无，后端接口规格不变）

## Impact

- **新工程目录**：`android/`（与后端 `src/` 并列）
- **依赖**：Retrofit2、OkHttp3、Gson、Glide（头像显示）、CircleImageView
- **后端无变更**：复用现有 API，文档见 `http://localhost:8080/docs`
