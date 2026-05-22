# 全局打磨修复规划

> 遵循 vibecoding-workflow Phase 2 规范
> 日期：2026-05-22

---

## 1. 模块概述

清理 P0 阶段遗留的占位文案和标识，统一替换为正式产品文案。

---

## 2. 文件清单

### 修改文件

| 文件路径 | 修改内容 |
|---|---|
| `src/components/app/AppSidebar.vue` | 副标题 "P0 工作台" → "学生客户管理平台" |
| `src/components/app/EmptyStateCard.vue` | 移除 "P0 骨架" badge |
| `src/views/login/LoginView.vue` | 替换两处 P0 阶段描述为正式产品文案 |

---

## 3. 变更详情

### AppSidebar.vue
```diff
- <div class="brand-subtitle">P0 工作台</div>
+ <div class="brand-subtitle">学生客户管理平台</div>
```

### EmptyStateCard.vue
```diff
- <div class="empty-state-card__badge">P0 骨架</div>
```
（直接移除该 div）

### LoginView.vue
```diff
- 面向团队内部的客户管理与协作平台。P0 阶段先打通登录、看板、客户、跟进、公共安排、日报周报和话术库。
+ 面向团队内部的学生客户管理与协作平台。统一客户沉淀、跟进记录、公共安排、日报周报，让团队协作更高效。

- YouSells 团队工作台，P0 阶段先行接入核心业务主流程。
+ YouSells 团队工作台，登录后即可访问客户管理、公共安排、日报周报与攻略区。
```

---

## 4. 假设清单

1. 这些文案修改不影响任何业务逻辑和接口调用
2. EmptyStateCard 组件的 badge 移除后不影响布局（组件本身样式独立）
3. 文案修改不需要后端配合
