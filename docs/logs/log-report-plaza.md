# 开发日志：报告广场（Report Plaza）

## 2026-05-23 Phase 2 方案规划

### 变更文件
- `docs/plan-report-plaza.md` — 创建规划文档
- `docs/logs/log-report-plaza.md` — 创建本日志文档

### 变更原因
进入 Phase 2，按 vibecoding-workflow 规范建立开发档案。

### 当前状态
✅ 规划文档已确认

---

## 需求确认记录

| 确认项 | 用户答复 |
|--------|----------|
| 点赞 | 一人一次，可取消（toggle） |
| 评论可见性 | 全员可见 |
| 编辑时间 | 随时可改，标记最后编辑时间 |
| 每页展示 | 4-5 条，占满屏幕 |
| 日报/周报 | 分 Tab |
| 评论权限 | 所有人都能评论 |
| 页面结构 | 保留个人工作台，新增报告广场 |

---

## 2026-05-23 Phase 3 代码落地

### 后端变更
- `yousells-server/src/main/resources/db/04_schema_report_plaza.sql` — 新增 `report_likes`、`report_comments` 表（MySQL）
- `yousells-server/src/test/resources/test-schema.sql` — H2 测试库同步新增上述表
- `DailyReportEntity.java` / `WeeklyReportEntity.java` — `createdBy`/`updatedBy` 恢复 `@TableField(exist = false)`，避免 H2/MySQL schema 缺失列导致查询失败；`createdAt`/`updatedAt` 正常持久化
- `ReportServiceImpl.java` — `updateDailyReport`/`updateWeeklyReport` 增加作者本人校验，防止越权修改
- `ReportPlazaMapper.java` / `ReportPlazaMapper.xml` — Feed 流 UNION 查询，关联用户表、点赞数、评论数、当前用户是否点赞
- `ReportPlazaServiceImpl.java` / `ReportPlazaController.java` — 广场列表、点赞（toggle）、评论 CRUD
- `ReportLikeEntity.java` / `ReportCommentEntity.java` — 新实体
- VO/DTO: `ReportPlazaItemVo`, `ReportCommentVo`, `CreateReportCommentRequest`, `ToggleLikeResponse`

### 前端变更
- `yousells-web/src/views/report/ReportPlazaView.vue` — 广场主页面（Tab 切换、成员筛选、分页加载）
- `yousells-web/src/components/report/ReportPlazaCard.vue` — 报告卡片（作者信息、摘要、统计、点赞、评论入口）
- `yousells-web/src/components/report/ReportCommentPanel.vue` — 评论抽屉（列表、分页、发表评论）
- `yousells-web/src/api/report.ts` — 扩展广场相关 API
- `yousells-web/src/router/index.ts` + `route-names.ts` + `AppSidebar.vue` — 新增「报告广场」导航入口

### 当前状态
⏳ **Phase 3 完成，进入 Phase 4 自动化测试**

---

## 2026-05-23 Phase 4 自动化测试

### 新增测试文件
- `ReportPlazaServiceTest.java` — Mockito 单元测试，覆盖 `pagePlaza`（daily/weekly/invalid）、`toggleLike`（点赞/取消）、`pageComments`、`createComment`（7 tests）
- `ReportPlazaControllerIntegrationTest.java` — Spring Boot 集成测试，覆盖登录→创建日报/周报→广场列表（无筛选/按用户筛选）→点赞 toggle→评论创建/列表→验证 likeCount/commentCount/likedByMe 统计（12 tests）

### 联调发现 & 修复
- `ReportPlazaItemVo` 字段 `hasLiked` 与前端 `likedByMe` 不一致，统一为 `likedByMe`，同步修改 Mapper XML
- `ReportPlazaServiceImpl.pageComments` 未填充 `userRealName`，导致前端 `c.userRealName.charAt(0)` 可能 NPE；修复：批量查询 `UserMapper.selectBatchIds` 填充姓名

### 测试结果
- 后端 `mvn test`：**167 tests pass, 0 failures, 6 skipped**（新增 19 个测试）
- 前端 `npm run build`：**✓ 编译通过**

### 当前状态
⏳ **Phase 4 完成，进入 Phase 5 联调验收**

---

## 2026-05-23 Phase 5 联调验收

### 环境
- MySQL Docker (13306) + Redis Docker (16379)
- 后端 `mvn spring-boot:run -Dspring-boot.run.profiles=dev` (8080)
- 前端 `npm run dev` (5173)

### 验收项
| 验收项 | 结果 |
|--------|------|
| MySQL schema `report_likes` / `report_comments` | ✅ 已执行 `04_schema_report_plaza.sql` |
| 登录获取 Token | ✅ 正常 |
| `GET /api/reports/plaza?type=daily` | ✅ 返回列表，含 likeCount/commentCount/likedByMe |
| `GET /api/reports/plaza?type=weekly` | ✅ 正常 |
| `POST /api/reports/plaza/daily/{id}/like` | ✅ toggle 点赞/取消 |
| `POST /api/reports/plaza/daily/{id}/comments` | ✅ 创建评论成功 |
| `GET /api/reports/plaza/daily/{id}/comments` | ✅ 列表分页，含 userRealName |
| 成员筛选 `?userId=2` | ✅ 仅返回指定成员日报 |

### 当前状态
⏳ **Phase 5 完成，进入 Phase 6 Code Review**

---

## 待办

- [x] 规划文档获用户确认
- [x] Phase 3 代码落地
- [x] Phase 4 自动化测试
- [x] Phase 5 联调验收
- [ ] Phase 6 Code Review
- [ ] Phase 7 PR 提交
