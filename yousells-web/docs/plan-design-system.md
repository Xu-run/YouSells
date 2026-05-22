# 前端全局风格重构规划 — 专业商务蓝

> 遵循 vibecoding-workflow Phase 2 规范
> 日期：2026-05-22
> 设计方向：D — 专业商务蓝

---

## 1. 模块概述

将现有 Golden Hour Theme（暖棕复古风）全面重构为专业商务蓝白风格，同时重新设计 Dashboard 看板布局，提升整体视觉专业度。

---

## 2. 设计系统规格

### 2.1 新配色

| Token | 旧值 | 新值 | 说明 |
|---|---|---|---|
| Primary | #f4a900 (芥末黄) | #2563eb (商务蓝) | 主按钮、链接、高亮 |
| Primary Hover | #b07d00 | #1d4ed8 | 按钮悬停 |
| Background | #fbf7ef (暖米) | #f8fafc (冷灰白) | 页面底色 |
| Card BG | rgba(255,253,250,0.95) | #ffffff | 卡片纯白 |
| Sidebar BG | #3a2e24 (深棕) | #ffffff (纯白) | 侧边栏 |
| Sidebar Text | #fdfaf5 | #475569 | 侧边栏文字 |
| Text Primary | #3a2e24 | #1e293b | 主文字 |
| Text Secondary | #6b5d53 | #64748b | 次要文字 |
| Text Muted | #9b8d83 | #94a3b8 | 弱化文字 |
| Border | rgba(74,64,58,0.07) | #e2e8f0 | 边框 |
| Success | — | #10b981 | 成功状态 |
| Warning | — | #f59e0b | 警告状态 |
| Danger | — | #ef4444 | 危险状态 |
| Info | — | #3b82f6 | 信息状态 |

### 2.2 新圆角

| Token | 旧值 | 新值 |
|---|---|---|
| sm | 10px | 6px |
| md | 16px | 8px |
| lg | 22px | 12px |
| xl | 28px | 16px |

### 2.3 新阴影

| Token | 新值 |
|---|---|
| shadow-card | 0 1px 3px rgba(0,0,0,0.04), 0 1px 2px rgba(0,0,0,0.02) |
| shadow-elevated | 0 4px 6px rgba(0,0,0,0.03), 0 10px 15px rgba(0,0,0,0.02) |

---

## 3. 文件清单

### 核心设计系统

| 文件 | 修改内容 |
|---|---|
| `src/styles/base.css` | **全面重写**：CSS变量、配色、布局、组件样式 |

### 全局布局组件

| 文件 | 修改内容 |
|---|---|
| `src/components/app/AppSidebar.vue` | 深色→浅色白底、蓝字、激活态蓝色高亮 |
| `src/components/app/AppHeader.vue` | 适配新配色 |
| `src/components/app/PageSection.vue` | 适配新配色和间距 |
| `src/components/app/EmptyStateCard.vue` | 适配新配色 |

### 页面视图

| 文件 | 修改内容 |
|---|---|
| `src/views/login/LoginView.vue` | 棕黑渐变→蓝白商务风格 |
| `src/views/dashboard/DashboardView.vue` | **重新设计布局** |
| `src/views/customer/CustomerListView.vue` | 微调样式适配 |
| `src/views/customer/CustomerDetailView.vue` | 微调样式适配 |
| `src/views/topic/TopicListView.vue` | 微调样式适配 |
| `src/views/topic/TopicDetailView.vue` | 微调样式适配 |
| `src/views/task/TaskBoardView.vue` | 微调样式适配 |
| `src/views/report/DailyReportView.vue` | 微调样式适配 |
| `src/views/report/WeeklyReportView.vue` | 微调样式适配 |

### Dashboard 组件（重新设计）

| 文件 | 修改内容 |
|---|---|
| `src/components/dashboard/DashboardStatCards.vue` | 新样式：紧凑数字卡片 |
| `src/components/dashboard/DashboardTaskList.vue` | 新样式 |
| `src/components/dashboard/DashboardCustomerList.vue` | 新样式 |

### topic 组件

| 文件 | 修改内容 |
|---|---|
| `src/components/topic/TopicCard.vue` | 适配新配色 |
| `src/components/topic/TopicFilterBar.vue` | 适配新配色 |
| `src/components/topic/TopicReplyList.vue` | 适配新配色 |
| `src/components/topic/TopicReplyForm.vue` | 适配新配色 |
| `src/components/topic/TopicCreateDialog.vue` | 适配新配色 |

### customer-list 组件

| 文件 | 修改内容 |
|---|---|
| `src/components/customer-list/CustomerFilterBar.vue` | 适配新配色 |
| `src/components/customer-list/CustomerTable.vue` | 适配新配色 |
| `src/components/customer-list/CustomerCreateDialog.vue` | 适配新配色 |

---

## 4. Dashboard 新布局设计

```
┌─────────────────────────────────────────────────────┐
│  首页看板                    [刷新数据]              │
├─────────────────────────────────────────────────────┤
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐       │
│  │ 待跟进  │ │ 职规人数│ │ 技术栈 │ │ 课程人数│       │
│  │   12   │ │   8    │ │   5    │ │   3    │       │
│  └────────┘ └────────┘ └────────┘ └────────┘       │
├──────────────────────────┬──────────────────────────┤
│  进度分布                │  意向分布                │
│  职规 8 人               │  很稳 5 人               │
│  技术栈 5 人             │  可跟 6 人               │
│  课程 3 人               │  观望 4 人               │
│                          │  冷淡 1 人               │
├──────────────────────────┼──────────────────────────┤
│  今日任务                │  重点客户                │
│  • 任务A  待开始  逾期   │  • 张同学  很稳  课程    │
│  • 任务B  进行中         │  • 王同学  可跟  职规    │
└──────────────────────────┴──────────────────────────┘
```

**新 Dashboard 数据需求：**
当前后端 `DashboardOverviewVo` 返回的是旧模型（4个stats），但 P1 规划要求新模型（progressDistribution + intentDistribution + 5个stats）。由于 BE-107 尚未完成，前端暂时按现有数据结构渲染，UI 布局已预留新指标位置。

---

## 5. 假设清单

1. Element Plus 组件的基础样式不需要重写，通过 CSS 变量覆盖即可
2. 所有页面的 DOM 结构保持不变，仅修改 class 和样式
3. Dashboard 布局重构不影响后端接口（当前仍使用旧数据结构）
4. 响应式断点保持现有逻辑

---

## 6. 风险点与预案

| 风险 | 预案 |
|---|---|
| base.css 全面重写可能导致某些页面样式错乱 | 逐页检查，发现问题立即修复 |
| Element Plus 主题变量覆盖不完全 | 针对性补充 override |
| Dashboard 新布局与旧数据结构不匹配 | 先按旧数据结构渲染，等 BE-107 完成后再补充新指标 |
| 改动文件过多，构建错误难以定位 | 先改 base.css，再逐个改页面，每步构建验证 |
