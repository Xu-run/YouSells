# 攻略区前端开发规划（Topic Module）

> 遵循 vibecoding-workflow Phase 2 规范
> 日期：2026-05-22

---

## 1. 模块概述

实现 YouSells P1 攻略区前端模块，替代旧话术库，提供团队内部协作知识库：问题列表、分类筛选、问题详情、回答列表、最佳方案标记、提问/回答功能。

---

## 2. 架构设计

### 数据流

```
TopicListView
  ├── TopicFilterBar (状态: category/keyword)
  ├── TopicCard[] (列表渲染)
  ├── TopicCreateDialog (弹窗表单)
  └── el-pagination

TopicDetailView
  ├── 问题信息区 (标题/分类/作者/时间/描述/已解决状态)
  ├── "标记为已解决" 按钮 (权限控制)
  ├── TopicReplyList (回答列表 + 最佳方案标识 + 标记按钮)
  └── TopicReplyForm (写回答表单)
```

### 跨模块通信

- `authStore.currentUser.userId` → 用于判断当前用户是否是提问人（控制权限按钮显示）
- `request.ts` → 统一 HTTP 请求（已存在，直接使用）
- `router` → 列表页点击卡片跳转到详情页（`RouteName.TopicDetail`）

---

## 3. 文件清单

### 新建文件

| 文件路径 | 职责 | 说明 |
|---|---|---|
| `src/components/topic/TopicFilterBar.vue` | 分类筛选 + 关键词搜索 | Props: modelValue/loading; Emits: update:modelValue/search |
| `src/components/topic/TopicCard.vue` | 单个问题卡片 | Props: topic; Emits: click |
| `src/components/topic/TopicReplyList.vue` | 回答列表 + 最佳方案 | Props: replies/solved/isAuthor/loading; Emits: markSolution |
| `src/components/topic/TopicReplyForm.vue` | 写回答表单 | Props: loading; Emits: submit |
| `src/components/topic/TopicCreateDialog.vue` | 新建问题弹窗 | Props: visible/loading; Emits: update:visible/submit |
| `src/views/topic/TopicListView.vue` | 问题列表页 | 重写空壳占位 |
| `src/views/topic/TopicDetailView.vue` | 问题详情页 | 重写空壳占位 |

### 修改文件

| 文件路径 | 修改内容 |
|---|---|
| `src/api/topic.ts` | 补充 `createTopic` / `createReply` / `markSolved` / `markSolution` |
| `src/types/topic.ts` | 补充 `TopicCreateRequest` / `TopicReplyCreateRequest` |

---

## 4. 接口定义

### API 层（api/topic.ts）

```typescript
// 已有
fetchTopics(params: TopicQuery): Promise<PageResponse<TopicListItem>>
fetchTopicDetail(id): Promise<TopicDetail>

// 新增
createTopic(data: TopicCreateRequest): Promise<IdResponse>
createReply(topicId, data: TopicReplyCreateRequest): Promise<void>
markSolved(topicId): Promise<void>
markSolution(topicId, replyId): Promise<void>
```

### 类型层（types/topic.ts）

```typescript
// 已有
interface TopicListItem { id, title, category, authorRealName, replyCount, solved, createdAt }
interface TopicDetail { id, title, description, category, authorRealName, solved, createdAt, replies }
interface TopicReply { id, userId, userRealName, content, isSolution, createdAt }
interface TopicQuery { page?, pageSize?, category?, keyword? }

// 新增
interface TopicCreateRequest { title: string; description?: string; category: string; }
interface TopicReplyCreateRequest { content: string; }
```

---

## 5. 假设清单

1. **后端接口已全部实现**：`TopicController` 6 个接口真实落库，可直接联调
2. **当前用户 ID 来源**：`authStore.currentUser.userId`（类型已确认存在）
3. **提问人权限判断**：`currentUser.userId === topic.authorUserId`，后端也会做二次校验
4. **回答排序**：从新到旧（时间倒序），与跟进时间线一致
5. **分页渲染**：`total > 0` 时显示分页条
6. **最佳方案更新策略**：乐观更新（本地先改）+ 成功后静默重新拉取详情兜底
7. **新建问题弹窗**：使用 `el-dialog`，与项目现有 `TaskEditDialog` 风格统一
8. ** authorRealName vs authorDisplayName**：后端 `TopicController` 返回的是 `authorRealName`，前端类型已对齐

---

## 6. 风险点与预案

| 风险 | 预案 |
|---|---|
| `types/topic.ts` 中的 `authorRealName` 与后端 VO 字段名不一致 | 联调时根据实际响应调整，若后端返回 `authorDisplayName` 则同步修改前端类型和组件 |
| 后端 `markSolution` 返回数据结构不确定 | API 层用 `Promise<void>`，不关心响应体；若后端返回特定结构再调整 |
| 回答列表为空时的 UI 状态 | 组件内处理空态：显示"暂无回答"，与 `FollowUpTimeline` 空态风格一致 |
| 权限判断字段名不一致 | `CurrentUser` 中有 `userId`，若 `TopicDetail` 中作者 ID 字段名不同（如 `authorUserId` vs `authorRealName`），联调时确认并调整 |
