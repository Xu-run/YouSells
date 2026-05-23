# 规划文档：报告广场（Report Plaza）

## 模块概述

将日报/周报从"个人工作台"升级为"团队广场"，支持全员可见、点赞、评论、重新编辑，营造透明协作氛围。

---

## 架构设计

### 数据流

```
用户打开报告广场 → GET /api/reports/plaza → Service 查询日报/周报 + 点赞数 + 评论数
                                       ↓
                                  返回分页 VO（含作者名、点赞状态、评论数）
                                       ↓
前端渲染 Feed 卡片 → 用户点赞/评论/编辑 → 对应 API → 刷新局部数据
```

### 模块划分

| 模块 | 职责 |
|------|------|
| `report`（已有） | 日报/周报 CRUD + 自动统计聚合 |
| `report-plaza`（新增） | Feed 流查询、点赞、评论 |
| `report` 前端 | 个人工作台（写报告） |
| `report-plaza` 前端 | 广场 Feed 流（看报告 + 互动） |

---

## 文件清单

### 后端

| 文件路径 | 职责 | 操作 |
|----------|------|------|
| `db/04_schema_report_plaza.sql` | MySQL 迁移：新增 `report_likes`、`report_comments` 表 | 新增 |
| `test-schema.sql` | H2 测试：同步新增 likes/comments 表 | 修改 |
| `modules/report/entity/ReportLikeEntity.java` | 点赞实体 | 新增 |
| `modules/report/entity/ReportCommentEntity.java` | 评论实体 | 新增 |
| `modules/report/mapper/ReportLikeMapper.java` | 点赞 Mapper | 新增 |
| `modules/report/mapper/ReportCommentMapper.java` | 评论 Mapper | 新增 |
| `modules/report/vo/ReportPlazaItemVo.java` | 广场 Feed 项 VO | 新增 |
| `modules/report/vo/ReportCommentVo.java` | 评论 VO | 新增 |
| `modules/report/dto/CreateReportCommentRequest.java` | 创建评论请求 | 新增 |
| `modules/report/service/ReportPlazaService.java` | 广场 Service 接口 | 新增 |
| `modules/report/service/impl/ReportPlazaServiceImpl.java` | 广场 Service 实现 | 新增 |
| `modules/report/controller/ReportPlazaController.java` | 广场 API（Feed、点赞、评论） | 新增 |
| `modules/report/controller/ReportController.java` | 补充：编辑日报/周报接口 + 返回编辑标记 | 修改 |
| `modules/report/service/ReportService.java` | 补充编辑方法签名 | 修改 |
| `modules/report/service/impl/ReportServiceImpl.java` | 实现编辑逻辑 + 记录最后编辑时间 | 修改 |
| `modules/report/entity/DailyReportEntity.java` | 移除 `@TableField(exist = false)` 让审计字段生效 | 修改 |
| `modules/report/entity/WeeklyReportEntity.java` | 同上 | 修改 |

### 前端

| 文件路径 | 职责 | 操作 |
|----------|------|------|
| `src/types/report.ts` | 新增 PlazaItem、Comment、Like 类型 | 修改 |
| `src/api/report.ts` | 新增广场 API（Feed、点赞、评论、编辑） | 修改 |
| `src/views/report/ReportPlazaView.vue` | 报告广场主页面（Feed 流 + 筛选 + 分页） | 新增 |
| `src/components/report/ReportPlazaCard.vue` | 广场报告卡片（日报/周报通用） | 新增 |
| `src/components/report/ReportCommentPanel.vue` | 评论区组件（展开/收起评论） | 新增 |
| `src/components/report/DailyReportForm.vue` | 补充编辑模式支持 | 修改 |
| `src/components/report/WeeklyReportForm.vue` | 补充编辑模式支持 | 修改 |
| `src/router/index.ts` | 新增 `report-plaza` 路由 | 修改 |
| `src/components/app/AppSidebar.vue` | 新增「报告广场」菜单项 | 修改 |

---

## 接口定义

### 广场 Feed 流

```
GET /api/reports/plaza?type=daily|weekly&userId={可选}&page=1&pageSize=5

Response:
{
  "code": 0,
  "data": {
    "list": [
      {
        "id": 1,
        "type": "daily",           // daily | weekly
        "reportDate": "2026-05-23", // 日报日期
        "weekKey": null,            // 周报周号
        "userId": 2,
        "userRealName": "小赵",
        "userLevel": "T0",
        "summary": "今天跟进3个客户...",
        "issues": null,
        "tomorrowPlan": "明天继续...",
        "newCustomerCount": 1,
        "followUpCount": 3,
        "progressAdvanceCount": 0,
        "taskCompletedCount": 2,
        "likeCount": 5,
        "commentCount": 2,
        "hasLiked": true,           // 当前用户是否点过赞
        "createdAt": "2026-05-23T18:00:00",
        "updatedAt": "2026-05-23T18:00:00",
        "edited": false             // 是否编辑过
      }
    ],
    "total": 42,
    "page": 1,
    "pageSize": 5
  }
}
```

### 点赞（Toggle）

```
POST /api/reports/plaza/{reportType}/{reportId}/like
  reportType = daily | weekly

Response: { "code": 0, "data": { "liked": true, "likeCount": 6 } }
```

### 评论列表

```
GET /api/reports/plaza/{reportType}/{reportId}/comments?page=1&pageSize=20

Response:
{
  "code": 0,
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 1,
        "userRealName": "秦梓源",
        "content": "跟进得很细，继续保持",
        "createdAt": "2026-05-23T19:00:00"
      }
    ],
    "total": 3
  }
}
```

### 发表评论

```
POST /api/reports/plaza/{reportType}/{reportId}/comments
Body: { "content": "写得好" }

Response: { "code": 0, "data": { "id": 2 } }
```

### 编辑日报

```
PUT /api/reports/daily/{id}
Body: { "summary": "...", "issues": "...", "tomorrowPlan": "..." }

Response: { "code": 0 }
```

### 编辑周报

```
PUT /api/reports/weekly/{id}
Body: { "summary": "...", "issues": "...", "nextWeekPlan": "..." }

Response: { "code": 0 }
```

---

## 假设清单

1. **点赞数据量可控**：团队人数 < 100，单篇报告点赞数不会爆炸，不用考虑 Redis 计数
2. **评论不涉及嵌套**：一级评论即可，不需要回复评论（类似朋友圈）
3. **Feed 流只展示已提交的报告**：自动聚合的预览报告不出现在广场
4. **日报/周报表已有 `is_deleted` 软删除**：离职成员的报告被过滤
5. **编辑不改变统计数字**：newCustomerCount 等自动统计字段编辑时不重置
6. **广场查询走索引**：`daily_reports(user_id, report_date)` 和 `weekly_reports(user_id, week_key)` 已建索引
7. **点赞去重**：`(user_id, report_type, report_id)` 联合唯一约束

---

## 风险点

| 风险 | 影响 | 预案 |
|------|------|------|
| Feed 流查询 N+1（逐条查点赞/评论数） | 性能差 | 用 JOIN + 子查询一次查出点赞数和评论数 |
| 编辑权限越权 | 安全问题 | Controller 层校验 `report.userId == currentUserId` |
| 评论内容 XSS | 安全问题 | 前端 v-html 不用，后端不做 HTML 转义（纯文本展示） |
| 审计字段不生效 | 数据缺失 | 移除 Entity 上的 `@TableField(exist = false)` |
