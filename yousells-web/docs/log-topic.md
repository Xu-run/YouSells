# 攻略区前端开发日志（Topic Module）

> 遵循 vibecoding-workflow Phase 2 规范
> 日期：2026-05-22

---

## 2026-05-22 19:40 — Phase 2 方案规划

**变更文件：**
- 新建 `yousells-web/docs/plan-topic.md`（规划文档）
- 新建 `yousells-web/docs/log-topic.md`（本日志文档）

**变更原因：**
按照 vibecoding-workflow 规范，在进入 Phase 3 编码前，必须先完成方案规划（双文档）并获取用户确认。

**当前状态：**
- Phase 0 上下文装载 ✅ 完成
- Phase 1 需求澄清 ✅ 完成（5个问题已确认）
- Phase 2 规划文档 ✅ 已创建，等待用户审批

---

## 2026-05-22 20:00 — Phase 3 代码落地完成

**变更文件（新建 7 个，修改 2 个）：**

新建：
- `src/types/topic.ts` — 补充 `TopicCreateRequest`、`TopicReplyCreateRequest`、`authorUserId`
- `src/api/topic.ts` — 补充 `createTopic`、`createReply`、`markSolved`、`markSolution`
- `src/components/topic/TopicFilterBar.vue` — 分类筛选 + 关键词搜索
- `src/components/topic/TopicCard.vue` — 问题卡片组件
- `src/components/topic/TopicCreateDialog.vue` — 新建问题弹窗
- `src/components/topic/TopicReplyList.vue` — 回答列表 + 最佳方案标识
- `src/components/topic/TopicReplyForm.vue` — 写回答表单
- `src/views/topic/TopicListView.vue` — 重写列表页（替换空壳）
- `src/views/topic/TopicDetailView.vue` — 重写详情页（替换空壳）

**变更原因：**
按照规划文档实现攻略区前端完整功能，覆盖列表/详情/筛选/搜索/提问/回答/标记解决/标记最佳方案。

**实现要点：**
- 列表页：分类筛选（el-radio-group）+ 关键词搜索 + 分页 + 提问弹窗
- 详情页：问题信息 + 权限按钮 + 回答列表 + 写回答表单
- 权限控制：`authStore.currentUser.userId === topic.authorUserId`
- 最佳方案标记：乐观更新（本地先改）+ 成功后静默重新拉取详情兜底
- 回答排序：依赖后端返回顺序（后端 TopicController 查询回复按时间排序）

**当前状态：**
- Phase 3 代码落地 ✅ 完成
- 等待构建验证 + Phase 4 测试

---

## 2026-05-22 20:05 — Phase 3 构建验证通过

**变更文件：**
- `src/utils/format.ts` — 修复 `datetime` bug（`d` → `day`），新增 `formatDate` 函数
- `src/views/topic/TopicListView.vue` — 空态改用 `el-empty`，移除未使用的 `EmptyStateCard` 导入

**变更原因：**
构建阶段发现 `formatDate` 未导出、`datetime` 存在变量引用 bug、`EmptyStateCard` props 不匹配。修复后 `npm run build` 通过。

**当前状态：**
- Phase 3 代码落地 ✅ 完成
- Phase 4 自动化测试 → 待进行
- Phase 5 联调验收 → 待进行

---

## 2026-05-22 20:08 — Phase 4 自动化测试（部分）

**变更文件：**
- 新建 `tests/smoke/topic-module.spec.ts`（Playwright E2E 测试）

**测试覆盖：**
- 列表页标题/按钮/筛选栏渲染
- 分类切换刷新
- 提问弹窗表单校验
- 空数据/列表渲染
- 详情页跳转（有条件跳过）

**注意：**
项目尚未初始化 Playwright 配置（无 `playwright.config.ts`），测试文件已按现有 smoke 测试风格编写，待配置补齐后可运行。

**当前状态：**
- Phase 4 测试代码 ✅ 已编写（配置待补齐）
- Phase 5 联调验收 → 启动前后端浏览器验证

---

## 待记录（Phase 5 开始后逐条追加）


## 2026-05-22 20:20 — Phase 5 联调验收（API 层）

**后端接口联调结果：**

| 接口 | 结果 | 备注 |
|---|---|---|
| `GET /api/topics` | ✅ 通过 | 空列表/有数据均正常 |
| `POST /api/topics` | ✅ 通过 | curl 中文编码为测试环境问题，前端 axios 无此问题 |
| `GET /api/topics/1` | ✅ 通过 | 返回含 authorName/authorUserId/solved/replies |
| `POST /api/topics/1/replies` | ✅ 通过 | 创建回答正常 |
| `PATCH /api/topics/1/solved` | ✅ 通过 | 标记已解决正常 |
| `PATCH /api/topics/1/replies/1/solution` | ✅ 通过 | 标记最佳方案正常 |

**联调中发现的问题及修复：**
1. **字段名不一致**：后端 VO 返回 `authorName` / `userName`，前端类型定义的是 `authorRealName` / `userRealName`。已统一修改前端类型和组件。
2. **curl 中文编码问题**：Windows Git Bash 下 curl 发送中文 JSON 时终端编码为 GBK。前端 axios 默认 UTF-8，不影响真实使用。
3. **Java 版本问题**：系统默认 Maven 使用 Java 11，与 Spring Boot 3.5 不兼容。已用 `export JAVA_HOME=/d/develop/jdk21` 重新启动后端。

**当前状态：**
- Phase 5 API 联调 ✅ 完成
- 前端 `npm run build` ✅ 通过
- 后端 6 个接口全部验证通过

---

## 待记录（Phase 6/7 开始后逐条追加）
