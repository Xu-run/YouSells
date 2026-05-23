# YouSells Changelog

## 2026-05-23 — Bug Fix Sprint & P2 功能补齐

### ✨ P2 功能补齐
- **Dashboard 沉默客户列表**：`DashboardServiceImpl` 新增 `buildSilentCustomers()`，展示 3 天未跟进的客户；前端 Dashboard 新增"沉默客户"卡片
- **Excel 导入模板下载**：后端新增 `GET /api/customers/import-template` endpoint；前端客户列表新增"下载模板"按钮

### 🔒 安全修复（同上一轮）

### 🔒 安全修复
- **客户越权访问修复**：`CustomerServiceImpl` 新增 `getCustomerDetail` / `updateCustomer` 数据范围校验（仅自己或下属的客户可操作）
- **用户管理权限修复**：`UserServiceImpl` 限制 `getUser` 仅自己/下属可见，`createUser` / `updateUser` / `resignUser` 仅 T3 可操作
- **任务权限修复**：`TaskBoardServiceImpl` 限制 `getTask` / `updateTaskStatus` / `addTaskLog` 仅创建人/执行人可操作
- **跟进创建权限修复**：`FollowUpServiceImpl.createFollowUp` 新增客户所有权校验
- **客户创建限制**：`createCustomer` 限制 owner/inviter 必须在当前用户可见范围内
- **任务创建限制**：`createTask` 限制 owner 必须在当前用户可见范围内
- **配置文件脱敏**：`application.yml` / `application-prod.yml` 移除所有密码/secret 的 fallback
- **JWT 异常日志**：`JwtAuthenticationFilter` catch 块增加 `log.debug` 记录
- **PII 日志脱敏**：`CustomerImportListener` 移除客户姓名日志输出

### 🏗 架构加固
- **事务边界**：`TaskBoardServiceImpl.createTask` / `updateTaskStatus`、`TopicServiceImpl.createReply` / `markBestSolution`、`UserServiceImpl.createUser` / `updateUser` / `resignUser` 添加 `@Transactional`
- **DTO 校验**：`CustomerController` / `TaskController` / `FollowUpController` 添加 `@Validated`，GET Query DTO 约束生效
- **DTO 校验增强**：`DailyReportCreateRequest` / `WeeklyReportCreateRequest` 增加 `@NotNull`/`@Size`；`TaskStatusUpdateRequest` 增加 `@Pattern`；分页 `pageSize` 增加 `@Max(500)`
- **SQL 语法修正**：`03_schema_p2.sql` 移除 MySQL 不支持的 `IF NOT EXISTS`（ALTER TABLE ADD COLUMN）

### 🌐 前端修复
- **DashboardView**：`onUnmounted` 中移除 resize 监听并 `dispose()` ECharts 实例
- **NotificationBell**：WebSocket 卸载后清理重连定时器；`JSON.parse` 增加 `try-catch`
- **LoginView**：默认凭据仅在 `import.meta.env.DEV` 生效；演示账号文本仅在开发环境显示
- **ReportPlazaView**：`el-radio-button` 废弃 `label` 改为 `value`；移除 `v-model` 反模式
- **AppSidebar**：`settingItems` 改为 `computed`，响应用户权限变化
- **Router**：`/settings/members` 增加 T3 路由守卫
- **TopicDetailView**：Route param 增加数字格式校验；`topic.replies` 增加可选链保护

### ⚡ 性能优化
- **Leaderboard N+1 消除**：`ReportAggregationMapper` 新增 4 个批量聚合方法，`LeaderboardServiceImpl` 从 `4N+1` 查询降为 **5 个查询**
- **DataScopeHelper 缓存**：添加 5 分钟 TTL 内存缓存，减少递归下级查询的 DB 压力；用户写操作后自动清除缓存

### 🧪 新增测试
- `CustomerServiceTest`：越权查看/修改测试（+4）
- `TaskBoardServiceTest`：任务权限校验测试（+5）
- `FollowUpServiceTest`：范围过滤 + 创建拒绝测试（+2）
- `UserServiceImplTest`：**新建**，用户权限校验测试（+7）

### 📦 配置变更
- `application-prod.yml`：移除 `MYSQL_USERNAME` fallback；添加 HikariCP 连接池配置；添加生产日志级别
- `.env.example`：补全 `JWT_SECRET`、`REDIS_PASSWORD` 及注释说明

---

## 2026-05-19 — 报告广场（P2.5）上线

### 新增功能
- 报告广场 Feed 流（日报/周报）
- 报告点赞（`report_likes` 表 + 幂等设计）
- 报告评论（`report_comments` 表 + 软删除）
- 前后端联调通过

---

## 2026-05-18 — 项目骨架 & P0 基线

### 基础设施
- Spring Boot 3.5 + JDK 21 后端骨架
- Vue 3 + Vite + Element Plus 前端骨架
- JWT Bearer Token 鉴权基线
- MySQL 8 + Redis 7 Docker Compose 基线
- 统一响应/异常处理
- 逻辑删除（MyBatis-Plus）
