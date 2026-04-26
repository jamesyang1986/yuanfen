### Requirement: 用户可填写职业信息
系统 SHALL 允许已认证用户在个人资料中填写职业字段。

#### Scenario: 保存职业信息
- **WHEN** 已认证用户发送 `PUT /api/v1/users/profile`，请求体包含 `{"occupation": "软件工程师"}`
- **THEN** 系统返回 HTTP 200，响应体中 `occupation` 为 "软件工程师"

#### Scenario: 职业字段为空时不覆盖
- **WHEN** 已认证用户发送 `PUT /api/v1/users/profile`，请求体不含 `occupation` 字段
- **THEN** 系统返回 HTTP 200，`occupation` 保持原值不变

### Requirement: 用户可填写自我介绍
系统 SHALL 允许已认证用户填写不超过 500 字的自我介绍。

#### Scenario: 保存自我介绍
- **WHEN** 已认证用户发送 `PUT /api/v1/users/profile`，请求体包含 `{"bio": "喜欢旅行和摄影..."}`
- **THEN** 系统返回 HTTP 200，响应体中 `bio` 为提交的内容

#### Scenario: 自我介绍超长校验
- **WHEN** 已认证用户提交 `bio` 字段超过 500 字
- **THEN** 系统返回 HTTP 400，响应体包含字段校验错误

### Requirement: 用户可选择择偶标签
系统 SHALL 允许已认证用户从预设标签集中最多选择 5 个择偶标签。

#### Scenario: 保存择偶标签
- **WHEN** 已认证用户发送 `PUT /api/v1/users/profile`，请求体包含 `{"partnerTags": ["三观相合", "爱好运动"]}`
- **THEN** 系统返回 HTTP 200，响应体 `partnerTags` 为 `["三观相合", "爱好运动"]`

#### Scenario: 择偶标签超出上限
- **WHEN** 已认证用户提交超过 5 个 partnerTags
- **THEN** 系统返回 HTTP 400，提示标签数量超限

### Requirement: 年龄由生日自动计算
系统 SHALL 在返回用户资料时自动计算并返回 age 字段，age 不可直接写入。

#### Scenario: 返回已填写生日的用户年龄
- **WHEN** 用户已设置 `birthDate`，调用 `GET /api/v1/users/profile`
- **THEN** 响应体包含 `age` 字段，值为当前年份减去出生年份

#### Scenario: 未填写生日时 age 为 null
- **WHEN** 用户未设置 `birthDate`，调用 `GET /api/v1/users/profile`
- **THEN** 响应体中 `age` 字段为 null
