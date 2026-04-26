## Context

现有手机号登录基础设施（`SmsController`, `MockSmsServiceImpl`, `PhoneSmsLoginStrategy`）已在后端落地，但有三个缺陷：

1. 验证码存储在 JVM 内存 `ConcurrentHashMap`，多实例部署时不共享
2. 验证码随机生成，测试无法预测
3. 手机号未注册时抛出 `USER_NOT_FOUND` 异常，未引导注册

前端完全没有手机号登录入口。

**约束：**
- 后端：Spring Boot 2.7.18 + MyBatis；现有登录体系为策略模式（`LoginStrategy` interface）
- Redis：仅用于验证码 KV 存储，不引入 Session/Spring Security
- 不依赖真实短信商，Mock 模式固定验证码 `123456`
- `TokenResponse` 需新增 `newUser` 字段，前端据此决定跳转路径

## Goals / Non-Goals

**Goals:**
- 将验证码存储迁移到 Redis，支持多实例部署
- Mock 模式固定验证码 `123456`，方便 dev/test 环境
- 手机号未注册时自动创建用户，返回 `newUser=true`
- `TokenResponse` 新增 `newUser` 布尔字段
- 前端登录页新增手机号 Tab，支持发送验证码 + 30 秒倒计时
- 新用户登录成功后跳转 `/profile?welcome=1`，老用户跳转 `/square`

**Non-Goals:**
- 接入真实短信服务商（仅 Mock）
- 手机号格式校验（前端简单验证即可，后端不做强约束）
- 修改现有用户名/密码登录流程

## Decisions

### 1. Redis Key 设计

```
sms:code:{phone}   → 验证码字符串（TTL 5 分钟）
sms:lock:{phone}   → 发送锁（TTL 60 秒，防止频繁发送）
```

用两个 key 分别控制"验证码有效期"和"发送间隔"，语义清晰，TTL 独立管理。

**替代方案：** 单 key 存 JSON（code + sendTime），但 TTL 复用时会互相干扰，放弃。

### 2. MockSmsServiceImpl 实现

- `sendCode`：先检查 `sms:lock:{phone}`，存在则抛"发送过于频繁"；否则写 `sms:code:{phone}=123456`（TTL 300s）+ `sms:lock:{phone}=1`（TTL 60s）
- `verifyCode`：取 `sms:code:{phone}` 与入参对比，匹配后删除 key（一次性）

**替代方案：** StringRedisTemplate vs RedisTemplate<String, String>，选 `StringRedisTemplate` 更简洁。

### 3. 自动注册新用户

`PhoneSmsLoginStrategy.login()` 验证码通过后：

```
if (userMapper.findByPhone(phone) == null) {
    User newUser = new User();
    newUser.setPhone(phone);
    userMapper.insert(newUser);
    tokenResponse.setNewUser(true);
} else {
    tokenResponse.setNewUser(false);
}
```

手机号作为唯一标识，用户名/密码字段留空（已有 nullable 设计）。同时调用 `updateLastLoginAt` 保持广场排序一致。

**替代方案：** 返回 HTTP 409 让前端跳注册页，但增加了前端状态管理复杂度，放弃。

### 4. TokenResponse.newUser 字段

在现有 `TokenResponse` 中追加 `private boolean newUser`，JSON 序列化为 `"newUser": true/false`。前端已有 `?welcome=1` 跳转逻辑，只需在 SMS 登录成功回调中判断该字段。

### 5. 前端 30 秒倒计时

发送验证码后启动 `setInterval` 每秒递减计数，`<= 0` 时清除 interval 并恢复按钮。使用 Vue `ref(0)` 存储剩余秒数：

```js
const countdown = ref(0)
// 按钮 disabled: countdown > 0
// 按钮文字: countdown > 0 ? `${countdown}s 后重发` : '发送验证码'
```

组件卸载时 `clearInterval` 防止内存泄漏（`onUnmounted` 钩子）。

## Risks / Trade-offs

- **Redis 单点** → 开发环境 localhost:6379 即可，生产环境使用 Redis Sentinel/Cluster 时配置 `spring.redis.*` 即可，代码不变
- **Mock 固定验证码泄漏风险** → `MockSmsServiceImpl` 仅在 dev profile 使用；生产替换为真实实现类时，`123456` 不再有效
- **手机号用户无密码** → 用户可能无法通过用户名/密码方式登录，但本系统双轨并存，影响有限；后续可加"设置密码"功能
- **TTL 5 分钟内验证码固定** → Mock 环境下同一手机号 5 分钟内验证码不变（`123456`），60 秒内不能重发；功能测试友好，安全影响可接受

## Migration Plan

1. `pom.xml` 新增 `spring-boot-starter-data-redis`
2. `application.properties` 新增 `spring.redis.host=localhost` / `port=6379`
3. 替换 `MockSmsServiceImpl`（注入 `StringRedisTemplate`）
4. 修改 `PhoneSmsLoginStrategy`（自动注册 + newUser 标记）
5. `TokenResponse` 新增 `newUser` 字段
6. 前端 `auth.js` 新增 `sendSmsCode(phone)` 函数
7. 前端 `LoginView.vue` 改造为 Tab 布局

**回滚：** 回滚 `MockSmsServiceImpl` 到旧版 `ConcurrentHashMap` 实现，移除 Redis 依赖即可；`TokenResponse.newUser` 向后兼容（新字段，旧客户端忽略）。

## Open Questions

- 生产环境短信服务商选型（阿里云 / 腾讯云 / 其他）——不在本次范围内
- 是否需要手机号格式正则校验（11 位数字）——可在后续迭代加入
