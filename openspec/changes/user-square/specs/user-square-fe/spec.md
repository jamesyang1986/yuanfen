## ADDED Requirements

### Requirement: 用户广场列表页
系统 SHALL 提供用户广场页面，以卡片形式展示所有用户，按最近活跃时间逆序排列。

#### Scenario: 进入广场页加载用户列表
- **WHEN** 已登录用户访问 `/square`
- **THEN** 系统调用 `GET /api/v1/users?page=0&size=20`，以卡片网格展示用户列表

#### Scenario: 用户卡片内容
- **WHEN** 用户列表加载完成
- **THEN** 每张卡片展示：圆形头像（无头像时显示占位图）、昵称、来自城市（无则显示"未知"）、年龄（由 birthDate 计算，无 birthDate 则显示"未知"）

#### Scenario: 翻页
- **WHEN** 用户点击分页组件的页码
- **THEN** 系统请求对应页数据并更新卡片列表

### Requirement: 广场导航入口
系统 SHALL 在导航栏提供"广场"链接，已登录状态下可见。

#### Scenario: 导航栏显示广场入口
- **WHEN** 用户已登录并查看任意页面
- **THEN** 顶部导航栏显示"广场"和"我的资料"两个链接
