# YouSells 全项目 Code Review 报告

> 审查时间：2026-05-23  
> 范围：后端（Spring Boot）+ 前端（Vue 3）+ 架构配置  
> 方法：4 维度并行扫描（安全 / 数据库 / 前端 / 架构）

---

## 一、风险总览

| 维度 | Critical | High | Medium | Low | 合计 |
|------|----------|------|--------|-----|------|
| 后端安全 | 2 | 3 | 5 | 2 | 12 |
| 后端数据库 | 1 | 2 | 3 | 3 | 9 |
| 前端 Vue/TS | 2 | 3 | 16 | 8 | 29 |
| 架构配置 | 3 | 6 | 8 | 7 | 24 |
| **合计** | **8** | **14** | **32** | **20** | **74** |

---

## 二、Critical（必须立即修复）

### CR-1 水平越权：任意用户可修改他人客户数据
- **文件**: `CustomerServiceImpl.java` — `updateCustomer`
- **问题**: 没有校验当前用户是否对客户有权限。任何登录用户可通过 `PUT /api/customers/{id}` 修改任意客户。
- **修复**: 查询客户后校验 `visibleUserIds.contains(customer.ownerUserId)`。

### CR-2 水平越权：任意用户可管理其他用户
- **文件**: `UserController.java` + `UserServiceImpl.java`
- **问题**: `POST /api/users`、`PUT /api/users/{id}`、`DELETE /api/users/{id}/resign` 无任何角色校验。T0 成员可创建/修改/离职任何用户（包括上级）。
- **修复**: 添加 `@PreAuthorize("hasRole('ADMIN')")` 或仅允许 T3 管理用户；在 Service 层校验操作者权限。

### CR-3 配置文件中硬编码生产级密码
- **文件**: `application-prod.yml`
- **问题**: 存在 `${MYSQL_PASSWORD:yousells@prod2026}`、`${REDIS_PASSWORD:yousells@redis2026}` 等 fallback 默认值。若生产环境未设置环境变量，将使用源码中明文密码。
- **修复**: 移除所有 fallback，使用纯环境变量 `${VAR}`。

### CR-4 JWT fallback 密钥可预测
- **文件**: `application.yml`
- **问题**: `JWT_SECRET` 有硬编码 fallback。若未设置环境变量，攻击者可伪造 Token。
- **修复**: 移除 fallback，启动时缺失则报错。

### CR-5 MySQL `ADD COLUMN IF NOT EXISTS` 语法错误
- **文件**: `db/03_schema_p2.sql`
- **问题**: MySQL 8 不支持 `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`，会导致执行失败。
- **修复**: 去掉 `IF NOT EXISTS`，或引入 Flyway/Liquibase。

### CR-6 前端内存泄漏：Dashboard resize 监听器未清理
- **文件**: `DashboardView.vue`
- **问题**: `window.addEventListener("resize", handleResize)` 注册了但从未 `removeEventListener`。反复进出 Dashboard 会泄漏监听器；ECharts 实例也未 `dispose()`。
- **修复**: `onUnmounted` 中清理监听器并调用 `chart.dispose()`。

### CR-7 前端内存泄漏：WebSocket 僵尸重连
- **文件**: `NotificationBell.vue`
- **问题**: `onUnmounted` 触发 `ws.close()` → `onclose` 回调中启动 `setTimeout(connectWebSocket, 5000)`。组件卸载后仍不断创建新 WebSocket 连接。
- **修复**: 用 `mounted` 标志位 + `clearTimeout(reconnectTimer)` 阻止卸载后的重连。

### CR-8 前端硬编码默认登录凭据
- **文件**: `LoginView.vue`
- **问题**: 表单默认值 `username: "admin"`, `password: "admin123"` 直接写死在源码中，生产环境也带默认值。
- **修复**: 仅开发环境填充默认值：`import.meta.env.DEV ? "admin" : ""`。

---

## 三、High（建议本迭代修复）

### HI-1 水平越权：任意用户查看任意客户详情
- **文件**: `CustomerServiceImpl.java` — `getCustomerDetail`
- **修复**: 查询后校验 `visibleUserIds` 范围。

### HI-2 水平越权：任意用户修改任意任务状态
- **文件**: `TaskBoardServiceImpl.java` — `updateTaskStatus`
- **修复**: 校验 `currentUserId == entity.getOwnerUserId()` 或 `creatorUserId`。

### HI-3 水平越权：任意用户查看/操作任意任务
- **文件**: `TaskBoardServiceImpl.java` — `getTask`、`addTaskLog`
- **修复**: 添加任务可见性校验。

### HI-4 WebSocket CORS 过于宽松
- **文件**: `WebSocketConfig.java`
- **问题**: `setAllowedOrigins("*")` 允许任意来源连接 WebSocket。
- **修复**: 注入 `app.cors.allowed-origins` 并解析为允许列表。

### HI-5 `spring.profiles.default: dev` 导致生产误启动为 dev
- **文件**: `application.yml`
- **问题**: 若生产环境忘记设置 `SPRING_PROFILES_ACTIVE`，应用会以 dev 配置启动（localhost DB、宽松 CORS）。
- **修复**: 移除 `default: dev`，改为本地开发时显式指定。

### HI-6 CORS 配置在生产环境回退到 localhost
- **文件**: `SecurityConfig.java` + `application-prod.yml`
- **问题**: `allowed-origins` 的 `@Value` 和 yml 都有 localhost fallback。
- **修复**: 生产配置中移除所有 localhost fallback。

### HI-7 前端 `el-radio-button` 使用已废弃的 `label` prop
- **文件**: `ReportPlazaView.vue`
- **问题**: Element Plus 2.6+ 废弃 `label`，改用 `value`。当前代码 `label="daily"` 在运行时可能失效。
- **修复**: `<el-radio-button value="daily">日报</el-radio-button>`。

### HI-8 前端 `ReportPlazaView.vue` 使用反模式 v-model
- **文件**: `ReportPlazaView.vue`
- **问题**: `v-model:item="reports[reports.indexOf(item)]"` 是 O(n) 且对 proxy 对象可能失效。
- **修复**: 传 `:item="item"` + emit 事件。

### HI-9 前端 `AppSidebar.vue` 非响应式权限判断
- **文件**: `AppSidebar.vue`
- **问题**: `settingItems.push(...)` 在模块加载时执行一次，用户切换登录后不会更新。
- **修复**: 将 `settingItems` 改为 `computed`。

### HI-10 docker-compose 中硬编码 root 密码
- **文件**: `docker-compose.yml`
- **问题**: `MYSQL_ROOT_PASSWORD: root` 硬编码。
- **修复**: 改为环境变量引用。

---

## 四、Medium（建议排期修复）

### 后端安全
- **输入校验缺失**: `DailyReportCreateRequest`、`WeeklyReportCreateRequest` 无任何 `@NotNull`/`@Size` 校验。
- **TaskStatusUpdateRequest** 只校验 `@NotBlank`，可传入任意字符串绕过状态机。
- **CreateUserRequest** 的 `managerUserId` 未校验，可创建自引用或指向不存在的用户。
- **pageSize 无上限**: 多个 QueryRequest 的 `pageSize` 可被恶意设为极大值（如 `999999999`）。
- **Excel 导入无文件校验**: `importCustomers` 未校验文件扩展名、Content-Type、大小。
- **FollowUp 列表无范围过滤**: `pageFollowUps` 返回全部跟进记录，未按当前用户客户范围过滤。
- **User 查询无权限校验**: `getUser(userId)` 允许查任何人。

### 后端数据库
- **无数据库迁移工具**: `pom.xml` 缺少 Flyway/Liquibase，生产 schema 演进靠手动 SQL。
- **日志中记录 PII**: `CustomerImportListener.java` 第 67 行 `log.warn("Failed to import customer: {}", dto.getRealName(), e)` 把客户真实姓名写入日志。
- **无日志脱敏配置**: 缺少 `logback-spring.xml`，异常中可能泄漏密码、token、手机号。

### 前端
- **API 错误静默吞掉**: `stores/user.ts` `loadUsers()`、`NotificationBell.vue` `loadUnreadCount`、`ScriptLibraryView.vue` `loadCategories()` 均 `catch { return [] }` 无任何提示。
- **NotificationListView.vue** `try/finally` 缺少 `catch`，API 异常时用户只看到空表格。
- **request.ts** 对 403/500 无差异化提示，统一抛 `Error(message)`。
- **MemberManageView.vue** T3 权限校验放在 `onMounted` 内，非 T3 用户会先看到页面布局再被重定向。
- **ReportPlazaCard.vue** `@comment-added="item.commentCount++"` 直接修改 prop，破坏单向数据流。
- **TopicDetailView.vue** `route.params.id` 未校验直接传给 API。
- **DashboardStatCards.vue** `iconMap: Record<string, any>` 应使用 `Component` 类型。
- **ScriptLibraryView.vue** `params as any` 应定义接口。
- **notification.ts** API 函数未 unwrap `response.data.data`，与其他模块不一致。

### 架构
- `JwtAuthenticationFilter.java` 解析异常时零日志记录，安全审计盲点。
- `AuthServiceImpl.java` `logout()` 为空方法，无 Token 黑名单机制。
- `ReportConvert.java` 存在未使用的 import `DateTimeFormatter`。
- `README.md` 多处与代码不符（鉴权方式写成 Session/Cookie、路径错误、话术库模块已改为攻略区）。
- `echarts` 版本写为 `^6.1.0`（不存在），应核实并修正为 5.x。

---

## 五、Low（优化项）

- 多处魔法字符串硬编码（`T0/T1/T2/T3`、`ACTIVE`、`待开始/进行中/已完成`、pageSize），建议抽取常量。
- `ReportServiceImpl` / `ReportConvert` 中日报/周报逻辑大量重复，可抽象共用逻辑。
- API 路径无版本号（`/api/v1/...`）。
- `/api/follow-ups` 与其他资源路径命名风格不一致。
- `main.ts` 全局事件监听器在应用卸载时未清理。
- `TaskEditDialog.vue` `el-date-picker` 的 `value-format` 使用非标准格式。
- `ReportPlazaServiceImpl.toggleLike/createComment` 未校验报告 ID 是否存在（会导致孤儿记录）。

---

## 六、修复优先级建议

### P0（本迭代必须修）
1. **后端越权**: `CustomerServiceImpl.updateCustomer/getCustomerDetail`、`UserServiceImpl` 用户管理、`TaskBoardServiceImpl.updateTaskStatus/getTask/addTaskLog`
2. **配置安全**: 移除 `application.yml` / `application-prod.yml` / `docker-compose.yml` 中所有密码/secret fallback
3. **MySQL schema**: `03_schema_p2.sql` 去掉 `IF NOT EXISTS`
4. **前端内存泄漏**: `DashboardView.vue` resize 清理 + `NotificationBell.vue` WebSocket 重连清理
5. **前端硬编码凭据**: `LoginView.vue` 默认值仅 DEV 生效
6. **Element Plus 废弃 prop**: `ReportPlazaView.vue` `label` → `value`
7. **前端反模式**: `ReportPlazaView.vue` `v-model:item` 改为 `:item` + emit

### P1（下个迭代修）
- DTO 输入校验补齐（`@NotNull`、`@Size`、`@Max(500)`）
- `pageSize` 上限钳制
- `FollowUp` / `ReportPlaza` 范围过滤
- `AppSidebar.vue` 响应式权限
- 日志 PII 脱敏 + `JwtAuthenticationFilter` 异常日志
- `AuthServiceImpl.logout()` 实现 Token 黑名单（Redis）

### P2（技术债）
- 引入 Flyway 管理数据库迁移
- 抽象日报/周报共用逻辑
- 前端抽取分页/状态常量
- 补充 API 版本号
- README 同步更新

---

*报告生成完毕。如需进入修复阶段，请确认 P0 范围。*
