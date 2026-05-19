# YouSells Session 切 JWT 改造方案

日期：`2026 年 5 月 19 日`

---

## 1. 目标

这次改造的目标不是把鉴权体系一步做到非常重，而是先把 `YouSells P0` 当前最影响联调体验的问题解决掉：

- 本地开发环境下 `Session/Cookie + CORS + SameSite` 联调不稳定
- 登录后仍出现 `login required`
- 前后端分离场景下，登录态排查成本偏高
- 后续如果继续并行开发，更多受保护接口还会重复踩同类问题

因此本轮改造目标是：

1. 将当前 `Session/Cookie` 鉴权切换为 `JWT Bearer Token`
2. 保持当前角色模型与接口风格尽量稳定
3. 保证前后端联调链路更简单、更可控
4. 为未来可能的微服务拆分预留方向

---

## 2. 改造范围

### 2.1 后端

需要改造：

- `SecurityConfig`
- `SessionAuthenticationFilter` 替换为 `JwtAuthenticationFilter`
- `AuthService / AuthController`
- `AuthConstants`
- 新增 JWT 工具类与 JWT 配置类
- 认证相关测试

### 2.2 前端

需要改造：

- `request.ts`
- `auth.ts`
- `auth store`
- 路由守卫保持不变，但登录态恢复逻辑改为基于 token

### 2.3 文档

需要同步：

- 鉴权与权限模型
- API 基线
- 本地启动与联调说明
- 开发日志

---

## 3. 不做范围

本轮不做：

- refresh token
- Redis 黑名单
- 多端登录踢出策略
- 强制失效 token 管理
- 用户表/角色表真实持久化
- OAuth / 单点登录

也就是说，这次先做 **P0 可稳定联调的轻量 JWT 版本**。

---

## 4. 方案选型

### 4.1 采用方案

采用：

- `access token` 单 token 方案
- 前端请求头携带 `Authorization: Bearer <token>`
- 后端每次请求解析 token，还原当前用户到 `SecurityContext`

### 4.2 为什么先不用 refresh token

因为当前项目阶段：

- 用户规模小
- 先解决联调稳定性
- 当前账号仍是 seed user
- 先让开发者和测试同学不卡住最重要

后续真要正式上线，再升级成：

- `access token + refresh token`
- Redis 黑名单
- 续签机制

---

## 5. Token 设计

### 5.1 token 内容

token 中至少放：

- `userId`
- `username`
- `displayName`
- `roles`

### 5.2 token 有效期

P0 建议：

- `access token` 默认 `12 小时`

原因：

- 团队内部工具
- 联调和开发阶段使用频繁
- 先降低频繁重新登录的干扰

### 5.3 token 存放位置

前端先放：

- `localStorage`

说明：

- 这不是最终最强安全方案
- 但对当前前后端分离联调最省事、最稳定
- 比继续让大家在 session/cookie 跨域链路上反复踩坑更合适

后续如果要增强安全性，可以改为：

- refresh token 放 httpOnly cookie
- access token 放内存

---

## 6. 接口改造方案

### 6.1 `POST /api/auth/login`

当前：

- 入参：`username + password`
- 返回：`CurrentUserVo`

改造后：

- 入参不变
- 返回改为：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "accessToken": "jwt-token",
    "tokenType": "Bearer",
    "expiresIn": 43200,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "displayName": "系统管理员",
      "roles": ["ADMIN"]
    }
  }
}
```

### 6.2 `GET /api/auth/me`

保持接口不变，但认证来源改为：

- 从 `Authorization` 头解析 JWT

返回仍是：

- `CurrentUserVo`

### 6.3 `POST /api/auth/logout`

P0 阶段处理方式：

- 后端接口保留
- 默认返回成功
- 主要由前端清理本地 token 完成登出

后续如果加入 token 黑名单，再让后端真正记录失效。

---

## 7. 后端改造设计

### 7.1 新增组件

建议新增：

```text
yousells-server/src/main/java/com/yousells/common/config/JwtProperties.java
yousells-server/src/main/java/com/yousells/common/security/JwtTokenProvider.java
yousells-server/src/main/java/com/yousells/common/security/JwtAuthenticationFilter.java
```

### 7.2 保留组件

保留：

- `LoginUser`
- `SecurityUserContext`
- `RestAuthenticationEntryPoint`
- `RestAccessDeniedHandler`

这样业务层拿当前用户的方式不变，迁移成本低。

### 7.3 替换逻辑

当前：

- `SessionAuthenticationFilter` 从 `HttpSession` 读用户

改造后：

- `JwtAuthenticationFilter` 从请求头读取 `Bearer token`
- 校验 token
- 解析用户信息
- 写入 `SecurityContext`

### 7.4 `AuthServiceImpl`

当前：

- 登录成功后写 `HttpSession`

改造后：

- 登录成功后生成 token 并返回
- 不再依赖 `HttpSession`

---

## 8. 前端改造设计

### 8.1 新增类型

需要新增登录响应类型，例如：

- `LoginResult`
- `TokenPayload`

### 8.2 `request.ts`

改造为：

- 自动从本地 token 存储读取 token
- 自动追加 `Authorization: Bearer xxx`
- 删除 `withCredentials: true`

### 8.3 `auth store`

需要增加：

- `accessToken`
- `setToken`
- `clearToken`
- `restoreAuthState`

登录成功后：

- 保存 token
- 保存 `currentUser`

刷新页面后：

- 如果本地有 token，就调用 `/api/auth/me` 恢复当前用户

### 8.4 登出

登出时：

- 调后端 `/api/auth/logout`
- 无论后端是否做黑名单，前端都清 token 和用户信息

---

## 9. 兼容性与风险

### 9.1 直接收益

切完后会直接改善：

- 本地联调 cookie 问题
- `login required` 偶发问题
- `127.0.0.1 / localhost` 混用带来的会话问题
- 后续多人本地开发时的联调成本

### 9.2 新风险

会引入的新风险：

- token 放 `localStorage` 的安全性一般
- logout 目前无法强制让旧 token 立刻失效
- 后续如果要做更正式的登录安全，还得继续增强

### 9.3 当前判断

对 `YouSells P0` 而言，这些风险是可接受的，收益明显更大。

---

## 10. 落地步骤

建议按下面顺序改：

1. 补 JWT 配置与工具类
2. 补后端 JWT 过滤器
3. 改 `SecurityConfig`
4. 改 `AuthServiceImpl / AuthController / Auth VO`
5. 改后端测试
6. 改前端 `types / api / request / store`
7. 重启联调
8. 跑前后端测试
9. 更新文档

---

## 11. 本轮验收标准

改造完成后，至少满足：

- 登录成功后返回 token
- 前端持有 token 后访问 `/api/auth/me` 正常
- 刷新页面后登录态仍能恢复
- `/dashboard`、`/customers` 等受保护页面不再频繁出现 `login required`
- 后端测试通过
- 前端构建通过

---

## 12. 当前结论

对于 `YouSells` 当前阶段，我建议：

- 现在就切 JWT
- 先做轻量版
- 先把联调链路稳定下来
- 不要再继续在 session/cookie 开发态上耗时间

下一步就按本方案直接落代码。
