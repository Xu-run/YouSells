# YouSells 数据模型与 API 基线

日期：`2026-05-19`

## 1. 数据库设计目标

P0 数据库要支撑：

- 团队成员登录与角色识别
- 客户统一沉淀
- 客户标签
- 跟进记录
- 公共安排
- 日报 / 周报
- 话术库
- 后续操作日志扩展

建表 SQL 已落地：

- `yousells-server/src/main/resources/db/01_schema_p0.sql`
- `yousells-server/src/main/resources/db/02_seed_p0.sql`

## 2. 表清单

权限相关：

- `users`
- `roles`
- `permissions`
- `user_roles`
- `role_permissions`

客户相关：

- `customers`
- `customer_tags`
- `customer_tag_relations`
- `customer_follow_ups`

协作相关：

- `task_boards`

汇报相关：

- `daily_reports`
- `weekly_reports`

话术库相关：

- `script_categories`
- `scripts`

预留：

- `operation_logs`

## 3. 命名规范

- 表名使用小写复数蛇形命名
- 字段名使用小写蛇形命名
- 主键统一使用 `id BIGINT`
- 时间字段统一使用 `created_at / updated_at`
- 逻辑删除字段统一使用 `is_deleted`
- Java 字段使用 camelCase，通过 MyBatis-Plus 自动映射

## 4. 关键枚举

客户意向：

- `A`
- `B`
- `C`
- `D`

客户阶段：

- `FIRST_COMMUNICATION`
- `NURTURING`
- `HIGH_INTENT`
- `PENDING_CLOSE`
- `CONVERTED`
- `PAUSED`
- `TRANSFER_TO_EXAM`

任务状态：

- `TODO`
- `IN_PROGRESS`
- `DONE`

角色：

- `ADMIN`
- `MEMBER`

## 5. API 统一约定

路径统一以 `/api` 开头。

统一响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

分页响应：

```json
{
  "list": [],
  "page": 1,
  "pageSize": 20,
  "total": 0
}
```

鉴权：

```http
Authorization: Bearer <accessToken>
```

前端开发环境使用 `VITE_API_BASE_URL=/api`，由 Vite proxy 转发到 `http://127.0.0.1:8080`。

## 6. API 清单

鉴权：

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

首页看板：

- `GET /api/dashboard/overview`

客户：

- `GET /api/customers`
- `GET /api/customers/{id}`
- `POST /api/customers`
- `PUT /api/customers/{id}`
- `GET /api/customers/tags`
- `PUT /api/customers/{id}/tags`
- `PUT /api/customers/{id}/next-follow`

跟进记录：

- `GET /api/follow-ups`
- `POST /api/follow-ups`

公共安排：

- `GET /api/tasks`
- `GET /api/tasks/board`
- `POST /api/tasks`
- `PUT /api/tasks/{id}`

日报周报：

- `GET /api/reports/daily`
- `POST /api/reports/daily`
- `GET /api/reports/weekly`
- `POST /api/reports/weekly`

话术库：

- `GET /api/scripts/categories`
- `GET /api/scripts`
- `GET /api/scripts/{id}`
- `POST /api/scripts`
- `PUT /api/scripts/{id}`

## 7. 错误码

- `0`：成功
- `4000`：请求参数错误
- `4003`：未登录或 token 无效
- `4004`：资源不存在
- `4090`：状态冲突
- `5000`：系统错误

## 8. 当前实现状态

已完成：

- JWT 登录接口
- 当前用户接口
- 登出接口占位
- 首页看板聚合接口
- P0 所有 controller / service / dto / vo 骨架

待真实化：

- 客户模块从样例数据切换为 MySQL
- 跟进模块从样例数据切换为 MySQL
- 公共安排从样例数据切换为 MySQL
- 日报 / 周报从样例数据切换为 MySQL
- 话术库从样例数据切换为 MySQL

## 9. 后端开发要求

成员真实化接口时必须同步：

- `entity`
- `mapper`
- `convert`
- `service`
- 测试

如果接口入参或返回字段变化，必须先同步本文档，再通知前端联调。
