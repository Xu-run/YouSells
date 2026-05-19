# API 接口基线

## 1. 目标

这份文档的目标不是把所有细节写死，而是先统一 `P0` 接口边界、路径命名、请求方式、返回结构和错误处理口径，让前后端可以并行开发。

## 2. 基础约定

### 2.1 路径风格

- 全部使用 REST 风格
- 路径统一小写
- 多词路径使用 `kebab-case` 或稳定业务名
- 当前项目统一使用复数资源名

### 2.2 响应包装格式

建议统一返回：

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
- `message` 用于提示
- `data` 为业务数据

### 2.3 分页格式

列表接口统一返回：

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

### 2.4 时间格式

- 后端统一返回 ISO 风格时间字符串，或统一 `yyyy-MM-dd HH:mm:ss`
- `P0` 阶段建议统一为：`yyyy-MM-dd HH:mm:ss`

## 3. 鉴权相关接口

### 3.1 登录

`POST /api/auth/login`

请求：

```json
{
  "username": "admin",
  "password": "******"
}
```

返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "displayName": "管理员",
    "roles": ["ADMIN"]
  }
}
```

### 3.2 当前用户信息

`GET /api/auth/me`

返回当前登录用户、角色、权限概要。

### 3.3 登出

`POST /api/auth/logout`

作用：销毁当前 Session。

## 4. 首页看板接口

### 4.1 首页概览

`GET /api/dashboard/overview`

返回：

- 今日待跟进数量
- 已逾期客户数量
- 最近新增客户
- A 类客户数量
- 今日公共安排概览

## 5. 客户模块接口

### 5.1 客户分页列表

`GET /api/customers`

支持参数：

- `page`
- `pageSize`
- `keyword`
- `intentLevel`
- `currentStage`
- `ownerUserId`
- `sourcePlatform`

### 5.2 客户详情

`GET /api/customers/{id}`

### 5.3 新增客户

`POST /api/customers`

### 5.4 编辑客户

`PUT /api/customers/{id}`

### 5.5 客户标签列表

`GET /api/customers/tags`

### 5.6 客户标签更新

`PUT /api/customers/{id}/tags`

## 6. 跟进记录接口

### 6.1 跟进记录列表

`GET /api/follow-ups`

支持参数：

- `customerId`
- `page`
- `pageSize`

### 6.2 新增跟进记录

`POST /api/follow-ups`

### 6.3 更新客户下次跟进

`PUT /api/customers/{id}/next-follow`

## 7. 公共安排接口

### 7.1 公共安排列表

`GET /api/tasks`

### 7.2 新增公共安排

`POST /api/tasks`

### 7.3 更新公共安排

`PUT /api/tasks/{id}`

### 7.4 看板数据

`GET /api/tasks/board`

## 8. 日报 / 周报接口

### 8.1 日报列表

`GET /api/reports/daily`

### 8.2 新增日报

`POST /api/reports/daily`

### 8.3 周报列表

`GET /api/reports/weekly`

### 8.4 新增周报

`POST /api/reports/weekly`

## 9. 话术库接口

### 9.1 分类列表

`GET /api/scripts/categories`

### 9.2 话术列表

`GET /api/scripts`

### 9.3 新增话术

`POST /api/scripts`

### 9.4 更新话术

`PUT /api/scripts/{id}`

### 9.5 话术详情

`GET /api/scripts/{id}`

## 10. 错误码建议

### 通用错误码

- `0`：成功
- `4001`：参数错误
- `4003`：无权限
- `4004`：资源不存在
- `4009`：状态冲突
- `5000`：系统异常

## 11. P0 当前结论

这份 API 基线的作用，是先把接口边界锁住，让：

- 前端可以先做页面
- 后端可以先拆控制层和 service 层
- 登录、客户、跟进、任务、报表、话术库这几条线互不打架

下一步最适合马上补的是：

1. `后端工程目录初始化方案`
2. `前端工程目录初始化方案`
