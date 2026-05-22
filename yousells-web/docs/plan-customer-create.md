# 客户列表增强 — 新建客户功能规划

> 遵循 vibecoding-workflow Phase 2 规范
> 日期：2026-05-22

---

## 1. 模块概述

在现有客户列表页基础上增加"新建学生客户"功能，包含弹窗表单、字段校验、提交后刷新列表。

---

## 2. 架构设计

### 数据流

```
CustomerListView
  ├── CustomerFilterBar
  ├── CustomerTable
  ├── CustomerCreateDialog (新增)
  └── el-pagination
```

用户点击"+ 新建客户" → 打开弹窗 → 填写表单 → 提交 → 成功后关闭弹窗 → 刷新列表（page 重置为 1）

---

## 3. 文件清单

### 新建文件

| 文件路径 | 职责 |
|---|---|
| `src/components/customer-list/CustomerCreateDialog.vue` | 新建客户弹窗（9个字段表单 + 校验） |

### 修改文件

| 文件路径 | 修改内容 |
|---|---|
| `src/views/customer/CustomerListView.vue` | 增加"+ 新建客户"按钮，引入弹窗，处理提交成功回调 |
| `src/api/customer-list.ts` | 补充 `createCustomer` 接口 |
| `src/types/customer-list.ts` | 补充 `CustomerCreateRequest` 类型 |

---

## 4. 接口定义

### API 层

```typescript
createCustomer(data: CustomerCreateRequest): Promise<IdResponse>
```

### 类型层

```typescript
interface CustomerCreateRequest {
  realName: string;
  grade: string;
  major: string;
  className?: string;
  inviterUserId?: number;
  ownerUserId: number;
  progress: string;
  intent: string;
  inviterNote?: string;
}
```

### 后端契约

```
POST /api/customers
→ { realName, grade, major, className?, inviterUserId?, ownerUserId, progress, intent, inviterNote? }
```

---

## 5. 表单字段与校验

| 字段 | 组件 | 必填 | 默认值 | 选项 |
|---|---|---|---|---|
| realName | el-input | ✅ | - | - |
| grade | el-select | ✅ | - | 大一/大二/大三/大四 |
| major | el-input | ✅ | - | - |
| className | el-input | ❌ | - | - |
| inviterUserId | el-select | ❌ | 当前用户 | 秦梓源(1)/小赵(2) |
| ownerUserId | el-select | ✅ | 当前用户 | 秦梓源(1)/小赵(2) |
| progress | el-select | ✅ | - | 职规/技术栈/课程 |
| intent | el-select | ✅ | - | 很稳/可跟/观望/冷淡 |
| inviterNote | el-textarea | ❌ | - | rows=3 |

---

## 6. 假设清单

1. 后端 `POST /api/customers` 已实现（CustomerController 中已确认存在）
2. 用户下拉选项硬编码（与 TaskEditDialog 一致），等后端提供用户列表 API 后替换
3. 邀约人和负责人默认值取 `authStore.currentUser.userId`
4. 新建成功后保持筛选条件，仅 `page` 重置为 1
5. 表单使用 `label-position="top"`，与 TaskEditDialog 风格一致

---

## 7. 风险点与预案

| 风险 | 预案 |
|---|---|
| `authStore.currentUser` 可能为 null | 弹窗打开时判断，若 null 则默认值设为 1（admin）|
| 后端返回字段名与前端类型不一致 | 联调时根据实际响应调整 |
| 硬编码用户 ID 与真实环境不匹配 | 规划文档已记录，等后端用户列表接口就绪后替换 |
