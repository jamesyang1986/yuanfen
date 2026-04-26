## ADDED Requirements

### Requirement: 用户可查看和编辑个人资料
已登录用户 SHALL 能够在个人资料页查看并编辑昵称、性别、出生年月、城市、地址字段，保存后立即刷新展示。

#### Scenario: 加载个人资料
- **WHEN** 用户进入个人资料页
- **THEN** 客户端调用 `GET /api/v1/users/profile`，将返回的字段填充到对应表单控件（null 字段显示为空）

#### Scenario: 保存资料成功
- **WHEN** 用户修改任意字段后点击保存
- **THEN** 客户端调用 `PUT /api/v1/users/profile`（仅传入非空字段），收到 200 后页面刷新展示最新数据，Toast 提示"保存成功"

#### Scenario: 出生年月选择
- **WHEN** 用户点击出生年月字段
- **THEN** 弹出年月选择器（DatePickerDialog 限月级别），选择结果以 YYYY-MM 格式填入字段

#### Scenario: 性别选择
- **WHEN** 用户点击性别字段
- **THEN** 展示单选弹窗（未知/男/女/其他），选择结果映射为 0/1/2/3 写入

### Requirement: 用户可上传头像
已登录用户 SHALL 能够从相册选取图片上传为头像，上传成功后头像展示区域立即更新。

#### Scenario: 选取并上传头像
- **WHEN** 用户点击头像区域，从相册选取合法图片（JPEG/PNG/GIF/WebP，≤ 5MB）
- **THEN** 客户端以 multipart/form-data 调用 `POST /api/v1/users/avatar`，收到 200 后用 Glide 加载返回的 avatarUrl 更新头像显示，Toast 提示"头像上传成功"

#### Scenario: 文件类型或大小不符
- **WHEN** 选取的文件超过 5MB 或类型不受支持，服务端返回 400
- **THEN** Toast 提示对应错误信息，头像不更新

#### Scenario: 头像展示
- **WHEN** 个人资料页加载完成且 avatarUrl 不为空
- **THEN** 用 Glide + CircleImageView 加载显示圆形头像；avatarUrl 为空时显示默认占位图
