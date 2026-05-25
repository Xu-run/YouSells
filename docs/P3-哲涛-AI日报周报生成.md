# P3-AI-REPORT 任务说明书

**负责人：** 哲涛
**模块代号：** P3-AI-REPORT
**模块名称：** AI 日报/周报自动生成
**任务类型：** 全栈（后端 + 前端 + 调优）
**预估工期：** 5-7 天

---

## 一、模块目标

打开日报/周报页面时，AI 自动根据当日/当周活动数据预填摘要，降低手动填写成本。

1. **日报**：AI 分析今日跟进记录 + 完成任务 + 新增客户 → 生成"今日小结""遇到的问题""明天计划"
2. **周报**：AI 汇总本周日报 → 生成"本周总结""问题复盘""下周计划"
3. 用户可编辑 AI 生成内容后再提交

---

## 二、数据流设计

### 每日数据来源全景

哲涛的模块 **不创造新数据**，而是**聚合已有数据**交给 AI 总结。理解数据从哪里来是核心：

```
当前用户 userId=1, date=2026-05-25

  customers 表                     task_boards 表
  │                                │
  │ creator_user_id=1              │ owner_user_id=1
  │ created_at = 2026-05-25        │ (看板已按状态展示)
  ├─ 今日新增: 3 个客户             │
  │ (张三/李四/王五)               ├─ 待开始: 1 条
  │                                ├─ 进行中: 2 条
  │                                └─ 已完成: 1 条 → 查 task_logs
  │                                                   │
  customer_follow_ups 表            task_logs 表
  │                                │
  │ customer_id IN (可看客户)       │ task_id IN (我的任务)
  │ created_at = 2026-05-25        │ created_at = 2026-05-25
  ├─ 张三: 跟进1次(进度:技术栈)     ├─ 完成: "修复登录页样式"
  ├─ 李四: 跟进2次(进度:课程)       └─ 完成: "更新客户导入模板"
  └─ 陈七: 跟进1次(意向:冷淡)
       │
       └─ progress 变化的客户 → 推进客户列表
```

### 数据聚合流程

```
POST /api/reports/daily/ai-generate  { date: "2026-05-25" }
       │
       ▼
ReportServiceImpl.generateDaily(date, currentUser)
       │
       ├─(1)─► customerMapper.selectByCreatorAndDate(userId, date)      → 今日新增客户
       ├─(2)─► followUpMapper.selectByUserAndDate(userId, date)          → 今日跟进记录
       ├─(3)─► taskLogMapper.selectByUserAndDate(userId, date, "完成")   → 今日完成任务
       ├─(4)─► followUpMapper.selectProgressChanged(userId, date)        → 推进客户
       │
       ├─(5)─► 组装 Prompt(
       │         用户: currentUser.realName
       │         日期: date
       │         新增客户: [姓名/年级/意向/专业]
       │         跟进记录: [客户名/进度/内容/反馈/下一步]
       │         完成任务: [任务标题]
       │         推进客户: [客户名 旧进度→新进度]
       │       )
       │
       ├─(6)─► aiService.generate(prompt, AiDailyReportSuggestion.class)
       │         → { summary, issues, tomorrowPlan, stats }
       │
       └─(7)─► 返回前端（不落库）
```

### 周报数据来源

```
POST /api/reports/weekly/ai-generate  { weekKey: "2026-W12" }
       │
       ├─ 优先: 查 daily_reports WHERE user_id=? AND week=?
       │        → 提取每篇的 summary + issues
       │        → 汇总为:"本周完成了X次跟进，推进了Y个客户..."
       │
       └─ 降级(无日报): 同日报逻辑, 时间范围扩展到整周
```

### 关键约束
- **不落库**：AI 生成的是建议，不自动创建日报/周报，用户确认后才通过现有表单提交
- **数据权限**：所有查询限定 `userId = currentUser`，只看自己的数据
- **空数据**：今日无活动 → 直接返回空建议 `{ summary: "今日暂无工作记录" }`，不调 LLM

### 2.1 POST `/api/reports/daily/ai-generate`

**请求体：**
```json
{
  "date": "2026-05-25"
}
```

**TypeScript 类型：**
```ts
// src/types/report.ts 中扩展
export interface AiDailyReportSuggestion {
  summary: string;
  issues: string;
  tomorrowPlan: string;
  stats: {
    followUpCount: number;
    taskCompletedCount: number;
    newCustomerCount: number;
    progressAdvanceCount: number;
  };
}

// src/api/report.ts 中新增
export async function generateDailyReport(date: string): Promise<AiDailyReportSuggestion> {
  const response = await request.post<ApiResponse<AiDailyReportSuggestion>>("/reports/daily/ai-generate", { date });
  return response.data.data;
}
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "summary": "今日完成 4 次客户跟进，重点推进了张三和李四的课程咨询。张三对就业班表现出较强意向，已安排下周试听。协助王五解决了技术栈选型问题。整体推进进度顺利。",
    "issues": "遇到赵六联系不上，已尝试电话和微信均无回复，后续需关注是否流失。陈七对课程价格有顾虑，需要进一步接触家长。",
    "tomorrowPlan": "1. 跟进张三试听反馈\n2. 联系赵六尝试家长渠道\n3. 为陈七准备详细的课程价值对比材料\n4. 参加团队周会",
    "stats": {
      "followUpCount": 4,
      "taskCompletedCount": 2,
      "newCustomerCount": 1,
      "progressAdvanceCount": 2
    }
  }
}
```

### 2.2 POST `/api/reports/weekly/ai-generate`

**请求体：**
```json
{
  "weekKey": "2026-W12"
}
```

**TypeScript 类型：**
```ts
export interface AiWeeklyReportSuggestion {
  summary: string;
  issues: string;
  nextWeekPlan: string;
  stats: {
    newCustomerCount: number;
    followUpCount: number;
    progressAdvanceCount: number;
    convertedCount: number;
    taskCompletedCount: number;
  };
}

export async function generateWeeklyReport(weekKey: string): Promise<AiWeeklyReportSuggestion> {
  const response = await request.post<ApiResponse<AiWeeklyReportSuggestion>>("/reports/weekly/ai-generate", { weekKey });
  return response.data.data;
}
```

---

## 三、后端实现指引

### 3.1 文件规划

| 文件 | 操作 | 说明 |
|---|---|---|
| `modules/report/controller/ReportController.java` | 修改 | 新增 2 个端点 |
| `modules/report/service/ReportService.java` | 修改 | 新增接口方法 |
| `modules/report/service/impl/ReportServiceImpl.java` | 修改 | 实现 AI 生成逻辑 |
| `modules/report/dto/AiReportGenerateRequest.java` | 新建 | 请求 DTO |
| `modules/report/dto/AiDailyReportSuggestion.java` | 新建 | 日报建议响应 DTO |
| `modules/report/dto/AiWeeklyReportSuggestion.java` | 新建 | 周报建议响应 DTO |

### 3.2 核心业务逻辑（日报）

```
1. 收到请求 date="2026-05-25"
2. 查询数据源：
   a. 当日跟进记录（followUps where createdAt = today, currentUser）
   b. 当日完成的任务（task_logs where createdAt = today, currentUser）
   c. 当日新增客户（customers where createdAt = today, creatorUserId = currentUser）
   d. 当日推进的客户（followUps where createdAt = today, progress changed）
3. 组装 Prompt，包含：
   - 每一条跟进记录的进度/内容/反馈/下一步
   - 完成任务列表
   - 新增客户列表
4. 调 AiService.generate(prompt, AiDailyReportSuggestion.class)
5. 返回结构化建议（不落库，前端接收后填入表单）
```

### 3.3 核心业务逻辑（周报）

```
1. 收到请求 weekKey="2026-W12"
2. 查询本周所有日报（daily_reports where week = currentWeek, userId = currentUser）
3. 如果没有日报 → 改为查询本周活动数据（方法同日报，时间范围扩展到整周）
4. 组装 Prompt → 调 AiService
5. 返回结构化建议
```

### 3.4 Prompt 设计提示

System prompt 方向：
> "你是 YouSells 系统内部的日报助手。根据用户今天的工作数据（跟进记录、完成任务、新增客户），生成一份专业的工作日报。日报应该：
> 1. 像人写的，不是模板化语言
> 2. 具体提到客户姓名和关键进展
> 3. 遇到的问题是真实的（如果有就写，没有就说无特别问题）
> 4. 明天计划是可执行的行动项
> 只输出 JSON，不要额外文字。"

### 3.5 测试场景

| 场景 | 输入 | 期望输出 |
|---|---|---|
| 今天有丰富工作数据 | date=有跟进+有任务+有新客户 | 摘要具体提到名字和进展 |
| 今天没有工作数据 | date=休息日 | 返回空摘要 + 说明"今日无工作记录" |
| 今天只有跟进没有任务 | date=有跟进无任务 | 摘要聚焦跟进，stats.taskCompletedCount=0 |
| LLM 超时 | 模拟 | 返回错误 JSON，前端显示"AI 生成失败" |
| 周报有日报 | weekKey=本周（已有3篇日报） | 基于日报汇总，不是重新查活动数据 |

---

## 四、前端实现指引

### 4.1 文件规划

| 文件 | 操作 | 说明 |
|---|---|---|
| `src/views/report/DailyReportView.vue` | 修改 | 新增"AI 生成"按钮 + AI 建议展示 |
| `src/views/report/WeeklyReportView.vue` | 修改 | 同上 |
| `src/components/report/AiSuggestionCard.vue` | 新建 | AI 建议展示卡片（日报/周报共用） |
| `src/api/report.ts` | 修改 | 新增 AI 生成 API |
| `src/types/report.ts` | 修改 | 新增 AI 建议类型 |

### 4.2 AiSuggestionCard 组件规格

**Props：**
| 属性 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `suggestion` | `AiDailyReportSuggestion \| AiWeeklyReportSuggestion \| null` | 是 | AI 建议数据 |
| `generating` | `boolean` | 是 | 是否正在生成 |
| `error` | `string` | 否 | 错误信息 |

**Emits：**
| 事件 | 参数 | 说明 |
|---|---|---|
| `apply` | `suggestion` | 用户点击"应用"→ 填入表单 |
| `regenerate` | — | 用户点击"换一版" |
| `dismiss` | — | 用户关闭建议 |

**Template 结构（伪代码）：**
```html
<div class="ai-suggestion-card">
  <!-- 生成中 -->
  <div v-if="generating" class="ai-generating">
    <el-icon class="is-loading"><Loading /></el-icon>
    <span>AI 正在分析你的工作数据，生成日报建议...</span>
  </div>

  <!-- 错误 -->
  <el-alert v-else-if="error" type="warning" title="AI 生成失败" :description="error">
    <el-button @click="$emit('regenerate')">重试</el-button>
  </el-alert>

  <!-- 建议内容 -->
  <template v-else-if="suggestion">
    <div class="ai-badge">AI 建议</div>
    <div class="suggestion-preview">
      <div class="field"><label>今日小结</label><p>{{ suggestion.summary }}</p></div>
      <div class="field" v-if="suggestion.issues"><label>问题</label><p>{{ suggestion.issues }}</p></div>
      <div class="field"><label>明天计划</label><p>{{ suggestion.tomorrowPlan || suggestion.nextWeekPlan }}</p></div>
    </div>
    <div class="ai-actions">
      <el-button type="primary" @click="$emit('apply', suggestion)">采用</el-button>
      <el-button @click="$emit('regenerate')">换一版</el-button>
      <el-button text @click="$emit('dismiss')">自己写</el-button>
    </div>
  </template>
</div>
```

**状态覆盖：**

| 状态 | 展示 |
|---|---|
| 未触发 | 页面显示"AI 生成"按钮 |
| 生成中 | 骨架屏/loading + "正在分析你的工作数据..." |
| 生成完成 | 建议卡片 + 三个按钮（采用/换一版/自己写） |
| 生成失败 | Alert + 重试按钮 |
| 已应用 | 建议内容填入表单，卡片收起 |

### 4.3 在 DailyReportView 中接入

在 DailyReportForm 上方或表单内部新增：
```html
<AiSuggestionCard
  v-if="showAiSuggestion"
  :suggestion="aiSuggestion"
  :generating="aiGenerating"
  :error="aiError"
  @apply="handleApplySuggestion"
  @regenerate="handleGenerateReport"
  @dismiss="showAiSuggestion = false"
/>
```

"AI 生成"按钮放在表单顶部，用户可选择手动写或 AI 辅助。

---

## 五、验收清单

### 后端验收
- [ ] `POST /api/reports/daily/ai-generate` 返回完整建议 JSON
- [ ] `POST /api/reports/weekly/ai-generate` 返回完整建议 JSON
- [ ] 无工作数据时正确返回空建议
- [ ] LLM 错误降级正常
- [ ] 周报优先使用已有日报数据（不重复调活动数据）

### 前端验收
- [ ] 日报页面出现"AI 生成"按钮
- [ ] 点击 → 生成中 loading → 建议卡片出现
- [ ] 点击"采用" → 内容填入表单，可手动编辑
- [ ] 点击"换一版" → 重新生成
- [ ] 点击"自己写" → 卡片关闭，正常手动填写
- [ ] 生成失败 → 错误提示 + 重试
- [ ] 周报页面同样正常

### 回归验收
- [ ] 手动填写日报/周报功能不受影响
- [ ] `npm run build` 通过
- [ ] `mvn test` 全部通过

---

## 六、遇到问题怎么办

1. **生成内容太模板化**：调整 Prompt，强调"具体姓名+具体事件+具体感受"，可以给 LLM 几个示例
2. **周报没有日报数据**：降级到查周活动数据直接生成
3. **token 超限**（跟进记录太多）：只取最新 N 条（20条），Prompt 里说明"以下是部分记录"
4. **用户想部分采用 AI 建议**：前端支持"点击单个字段采用"，不强制全量替换
5. **卡住超过半天**：群里说明情况，@梓源

---

## 七、交付物

- [ ] 后端：2 个新端点
- [ ] 前端：AiSuggestionCard 组件 + 日报/周报页面集成
- [ ] 测试：5 个后端测试场景
- [ ] 自测报告：截图 + 验收清单勾选
