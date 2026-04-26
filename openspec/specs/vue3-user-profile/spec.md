## ADDED Requirements

### Requirement: 查看个人资料
系统 SHALL 在资料页加载并展示当前用户的资料信息。

#### Scenario: 加载资料成功
- **WHEN** 用户进入 /profile 页面
- **THEN** 系统调用 GET /api/v1/users/profile，将昵称、性别、出生年月、城市、地址、头像填充到对应字段

#### Scenario: 头像展示
- **WHEN** 用户资料包含 avatarUrl
- **THEN** 系统显示圆形头像图片；无 avatarUrl 时显示默认占位图

### Requirement: 编辑个人资料
系统 SHALL 允许用户修改昵称、性别、出生年月、城市、地址。

#### Scenario: 保存资料成功
- **WHEN** 用户修改任意字段后点击保存
- **THEN** 系统调用 PUT /api/v1/users/profile，成功后显示提示"保存成功"并刷新页面数据

#### Scenario: 性别选择
- **WHEN** 用户点击性别字段
- **THEN** 系统显示下拉选择（未知/男/女/其他），选择后更新字段值（对应 0/1/2/3）

#### Scenario: 出生年月选择
- **WHEN** 用户点击出生年月字段
- **THEN** 系统显示月份选择器（Element Plus DatePicker，type=month），选择后格式化为 YYYY-MM

### Requirement: 头像上传
系统 SHALL 允许用户选取本地图片，预览后上传为新头像。

#### Scenario: 头像上传成功
- **WHEN** 用户点击头像区域，选取一张 JPEG/PNG/GIF/WebP 格式、不超过 5MB 的图片
- **THEN** 系统以 multipart/form-data 调用 POST /api/v1/users/avatar，成功后用返回的 avatarUrl 更新头像展示

#### Scenario: 文件过大校验
- **WHEN** 用户选取超过 5MB 的图片
- **THEN** 系统在上传前提示"文件不能超过 5MB"，不发起上传请求

#### Scenario: 文件类型校验
- **WHEN** 用户选取非图片类型的文件
- **THEN** 系统提示"仅支持 JPEG/PNG/GIF/WebP 格式"，不发起上传请求
