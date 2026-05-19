# API 接口基线

## 1. 统一约定

### 1.1 路径风格

- 全部接口前缀统一为 `/api`
- 采用 REST 风格
- 路径统一小写
- 多词资源使用稳定复数命名

### 1.2 响应结构

统一响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

说明：

- `code = 0` 表示成功
- 非 `0` 表示业务失败
- `message` 用于前端提示
- `data` 为业务数据体

### 1.3 分页结构

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "page": 1,
    "pageSize": 20,
    "total": 100
  }
}
```

### 1.4 鉴权方式

当前受保护接口统一使用：

```http
Authorization: Bearer <accessToken>
```

前端不再依赖 cookie 登录态。

## 2. 鉴权接口

### 2.1 登录

`POST /api/auth/login`

请求：

```json
{
  "username": "admin",
  "password": "admin123"
}
```

响应：

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

### 2.2 当前登录人

`GET /api/auth/me`

说明：

- 需要 Bearer Token
- 返回当前登录用户信息

### 2.3 登出

`POST /api/auth/logout`

说明：

- 当前后端保留接口
- 前端负责清理本地 token

## 3. 首页看板接口

### 3.1 首页概览

`GET /api/dashboard/overview`

返回内容包括：

- 今日待跟进数
- 逾期客户数
- 最近新增客户数
- 高意向客户数
- 今日任务列表
- 重点客户列表

## 4. 客户模块接口

### 4.1 客户分页列表

`GET /api/customers`

支持参数：

- `page`
- `pageSize`
- `keyword`
- `intentLevel`
- `currentStage`
- `ownerUserId`
- `sourcePlatform`

### 4.2 客户详情

`GET /api/customers/{id}`

### 4.3 新增客户

`POST /api/customers`

### 4.4 编辑客户

`PUT /api/customers/{id}`

### 4.5 客户标签列表

`GET /api/customers/tags`

### 4.6 更新客户标签

`PUT /api/customers/{id}/tags`

### 4.7 更新下次跟进

`PUT /api/customers/{id}/next-follow`

## 5. 跟进记录接口

### 5.1 跟进记录列表

`GET /api/follow-ups`

支持参数：

- `customerId`
- `page`
- `pageSize`

### 5.2 新增跟进记录

`POST /api/follow-ups`

## 6. 公共安排接口

### 6.1 任务列表

`GET /api/tasks`

### 6.2 新增任务

`POST /api/tasks`

### 6.3 更新任务

`PUT /api/tasks/{id}`

### 6.4 看板数据

`GET /api/tasks/board`

## 7. 日报周报接口

### 7.1 日报列表

`GET /api/reports/daily`

### 7.2 新增日报

`POST /api/reports/daily`

### 7.3 周报列表

`GET /api/reports/weekly`

### 7.4 新增周报

`POST /api/reports/weekly`

## 8. 话术库接口

### 8.1 分类列表

`GET /api/scripts/categories`

### 8.2 话术列表

`GET /api/scripts`

### 8.3 新增话术

`POST /api/scripts`

### 8.4 更新话术

`PUT /api/scripts/{id}`

### 8.5 话术详情

`GET /api/scripts/{id}`

## 9. 通用错误码

- `0`：成功
- `4001`：参数错误
- `4003`：未登录或无权限
- `4004`：资源不存在
- `4009`：状态冲突
- `5000`：系统异常

## 10. P0 当前接口结论

当前 P0 接口基线已经具备：

- JWT 登录主链路
- 看板数据接口
- 客户列表与详情接口
- 跟进接口
- 公共安排接口
- 日报周报接口
- 话术库接口

后续各成员继续开发时，统一在这份基线上增量扩展，不再回退到 session 登录态设计。
