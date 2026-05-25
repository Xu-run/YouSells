# P3-AI-INSIGHT 任务说明书

**负责人：** 志明
**模块代号：** P3-AI-INSIGHT
**模块名称：** 客户 360° 智能洞察
**任务类型：** 全栈（后端 + 前端 + 调优）
**预估工期：** 5-7 天

---

## 一、模块目标

在客户详情页新增"AI 洞察"面板，AI 自动分析全部跟进历史，输出：
1. **意向趋势**：上升 / 平稳 / 下降
2. **关键关注点**：客户最关心什么（从跟进内容中提取）
3. **沟通风格画像**：客户是理性型/感性型/价格敏感型/技术型
4. **转化概率评分**：高/中/低 + 置信度百分比
5. **下一步建议**：基于当前阶段推荐具体沟通策略

---

## 二、前后端接口契约

### 2.1 GET `/api/customers/{id}/ai-insight`

**说明：** 获取客户 AI 洞察。服务端检查缓存，缓存命中直接返回；缓存过期或不存在则调 LLM 重新生成。

**前端调用方式：**
```ts
// src/api/customer-detail.ts 中新增
export async function fetchCustomerAiInsight(customerId: string | number): Promise<AiInsight> {
  const response = await request.get<ApiResponse<AiInsight>>(`/customers/${customerId}/ai-insight`);
  return response.data.data;
}

export async function refreshCustomerAiInsight(customerId: string | number): Promise<AiInsight> {
  const response = await request.post<ApiResponse<AiInsight>>(`/customers/${customerId}/ai-insight/refresh`);
  return response.data.data;
}
```

**TypeScript 类型：**
```ts
// src/types/ai-insight.ts（新建）
export interface AiInsight {
  customerId: number;
  intentTrend: "上升" | "平稳" | "下降";
  intentTrendReason: string;
  keyConcerns: string[];
  communicationStyle: string;
  conversionProbability: "高" | "中" | "低";
  conversionConfidence: number;       // 0-100
  nextActionSuggestion: string;
  summary: string;                     // 一句话总结
  generatedAt: string;                 // ISO datetime
}
```

**JSON 响应示例：**
```json
{
  "code": 200,
  "data": {
    "customerId": 1,
    "intentTrend": "上升",
    "intentTrendReason": "近3次跟进中明确表示对课程感兴趣，主动询问了价格和上课时间",
    "keyConcerns": ["课程价格", "上课时间灵活性", "就业保障"],
    "communicationStyle": "理性型 — 喜欢对比数据，需要具体案例支撑",
    "conversionProbability": "高",
    "conversionConfidence": 82,
    "nextActionSuggestion": "建议下次沟通重点介绍就业案例，提供试听机会",
    "summary": "张三对课程有明确兴趣，属于理性决策型客户，预计1-2周内可转化",
    "generatedAt": "2026-05-25T10:30:00"
  }
}
```

### 2.2 POST `/api/customers/{id}/ai-insight/refresh`

**说明：** 强制刷新洞察，忽略缓存。参数同 GET，行为差异在于跳过缓存直接调 LLM。

---

## 三、后端实现指引

### 3.1 文件规划

| 文件 | 操作 | 说明 |
|---|---|---|
| `modules/customer/controller/CustomerController.java` | 修改 | 新增 2 个端点 |
| `modules/customer/service/CustomerService.java` | 修改 | 新增接口方法 |
| `modules/customer/service/impl/CustomerServiceImpl.java` | 修改 | 实现洞察逻辑 |
| `modules/customer/dto/AiInsightResponse.java` | 新建 | 响应 DTO（record） |
| `modules/customer/entity/AiInsightCache.java` | 新建 | 缓存实体，映射 `ai_insight_cache` 表 |

### 3.2 核心业务逻辑

```
1. 收到请求 → 查 ai_insight_cache 表
2. 缓存命中且未过期（默认 TTL 2小时）→ 直接返回
3. 缓存未命中或过期 →
   a. 查询该客户全部跟进记录（followUps）
   b. 查询客户基本信息（年级/专业/意向/进度）
   c. 组装 Prompt → 调 AiService.generate(prompt, AiInsight.class)
   d. 解析 JSON → 存入缓存表 → 返回
```

### 3.3 Prompt 设计提示

- 输入上下文：客户基本信息 + 最近 20 条跟进记录（截断到 token 限制）
- 要求 LLM 输出结构化 JSON，需指定 JSON Schema 约束
- System prompt 示例方向：
  > "你是一名资深的销售教练，擅长分析客户沟通记录。根据客户基本信息和跟进历史，输出结构化的客户洞察报告。只输出 JSON，不要额外文字。"

### 3.4 AI 调用方式

使用队长提供的 `AiService`：
```java
// 注入
private final AiService aiService;

// 调用（自动处理 JSON 解析和重试）
AiInsightResponse insight = aiService.generate(prompt, AiInsightResponse.class);
```

### 3.5 数据权限

- 洞察查询复用现有客户数据权限（`resolveVisibleOwnerIds()`）
- 只有能看到该客户的人才能看 AI 洞察

### 3.6 测试场景

| 场景 | 输入 | 期望输出 |
|---|---|---|
| 有充足跟进记录的客户 | customerId=1（>10条跟进） | 返回完整洞察，confidence > 60 |
| 跟进记录很少的客户 | customerId=2（0-2条跟进） | 返回洞察但 confidence < 40，summary 提示"跟进记录不足" |
| 无跟进记录的客户 | customerId=3（0条） | 返回默认洞察，不调 LLM |
| LLM 调用超时 | 模拟超时 | 返回缓存旧数据，或降级提示"AI分析暂时不可用" |
| 缓存命中 | 第二次请求同一客户（2h内） | 直接返回缓存，不调 LLM |

---

## 四、前端实现指引

### 4.1 文件规划

| 文件 | 操作 | 说明 |
|---|---|---|
| `src/views/customer/CustomerDetailView.vue` | 修改 | 新增 AI 洞察 Tab/面板 |
| `src/components/insight/AiInsightPanel.vue` | 新建 | AI 洞察主面板 |
| `src/components/insight/IntentTrendBadge.vue` | 新建 | 意向趋势徽标 |
| `src/components/insight/KeyConcernTags.vue` | 新建 | 关注点标签列表 |
| `src/components/insight/ConversionGauge.vue` | 新建 | 转化概率仪表盘 |
| `src/api/customer-detail.ts` | 修改 | 新增 AI 洞察 API 调用 |
| `src/types/ai-insight.ts` | 新建 | TypeScript 类型定义 |

### 4.2 AiInsightPanel 组件规格

**Props：**
| 属性 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `insight` | `AiInsight \| null` | 是 | 洞察数据 |
| `loading` | `boolean` | 是 | 加载中 |
| `error` | `boolean` | 否 | 加载失败 |

**Emits：**
| 事件 | 参数 | 说明 |
|---|---|---|
| `refresh` | — | 用户点击刷新按钮 |

**Template 结构（伪代码）：**
```html
<div class="ai-insight-panel" v-loading="loading">
  <!-- 错误态 -->
  <el-alert v-if="error" type="warning" title="AI 分析暂时不可用" show-icon>
    <el-button @click="$emit('refresh')">重试</el-button>
  </el-alert>

  <!-- 无数据态 -->
  <EmptyState v-else-if="!insight" title="暂无 AI 洞察" description="点击下方按钮生成分析报告">
    <el-button type="primary" @click="$emit('refresh')">开始分析</el-button>
  </EmptyState>

  <!-- 正常态 -->
  <template v-else>
    <!-- 摘要区 -->
    <div class="insight-summary">{{ insight.summary }}</div>

    <!-- 意向趋势 -->
    <IntentTrendBadge :trend="insight.intentTrend" :reason="insight.intentTrendReason" />

    <!-- 转化概率仪表 -->
    <ConversionGauge :probability="insight.conversionProbability" :confidence="insight.conversionConfidence" />

    <!-- 关键关注点 -->
    <KeyConcernTags :concerns="insight.keyConcerns" />

    <!-- 沟通风格 -->
    <div class="insight-field">
      <span class="label">沟通风格</span>
      <el-tag>{{ insight.communicationStyle }}</el-tag>
    </div>

    <!-- 下一步建议 -->
    <div class="insight-suggestion">
      <span class="label">建议行动</span>
      <p>{{ insight.nextActionSuggestion }}</p>
    </div>

    <!-- 生成时间 + 刷新 -->
    <div class="insight-footer">
      <span>生成于 {{ friendlyDate(insight.generatedAt) }}</span>
      <el-button text @click="$emit('refresh')">重新分析</el-button>
    </div>
  </template>
</div>
```

**状态覆盖（必须处理）：**

| 状态 | 展示内容 |
|---|---|
| 加载中 | 骨架屏/loading 动画，带"AI 分析中..."文案 |
| 无数据 | EmptyState："暂无 AI 洞察"，引导点击"开始分析" |
| 正常 | 完整洞察展示 |
| 错误 | Alert 告警 + 重试按钮 |
| 数据过期 | 正常展示 + 顶部浅黄色提示"洞察已超过 X 小时，建议刷新" |

### 4.3 在 CustomerDetailView 中接入

在客户详情页的 status-bar 下方或 detail-grid 下方新增：
```html
<AiInsightPanel
  :insight="aiInsight"
  :loading="aiInsightLoading"
  :error="aiInsightError"
  @refresh="loadAiInsight"
/>
```

### 4.4 颜色/标签映射

| 意向趋势 | Tag 类型 | 颜色 |
|---|---|---|
| 上升 | success | `#10b981` |
| 平稳 | info | `#3b82f6` |
| 下降 | danger | `#f87171` |

| 转化概率 | Tag 类型 | 颜色 |
|---|---|---|
| 高 | success | `#10b981` |
| 中 | warning | `#f59e0b` |
| 低 | danger | `#f87171` |

---

## 五、验收清单

### 后端验收
- [ ] `GET /api/customers/{id}/ai-insight` 正常返回完整洞察 JSON
- [ ] `POST /api/customers/{id}/ai-insight/refresh` 强制刷新
- [ ] 缓存命中时响应 < 50ms（不调 LLM）
- [ ] 无跟进记录的客户正确返回默认洞察，不调 LLM
- [ ] LLM 超时/错误时降级返回旧缓存或错误提示
- [ ] 权限：用户 A 无法看到用户 B 的客户洞察

### 前端验收
- [ ] 客户详情页可见"AI 洞察"面板
- [ ] 首次打开无缓存 → 显示"开始分析"引导按钮
- [ ] 点击"开始分析" → 加载态 → 完整洞察展示
- [ ] 刷新按钮重新生成
- [ ] 意向趋势/转化概率/关注点/建议 各子组件正确展示
- [ ] 错误态：网络失败时显示告警 + 重试
- [ ] 暗色模式下样式正常

### 回归验收
- [ ] 客户详情页其他功能不受影响
- [ ] `npm run build` 通过
- [ ] `mvn test` 全部通过

---

## 六、遇到问题怎么办

1. **LLM 返回格式不稳定（JSON 解析失败）**：队长 AiService 已内置 JSON Schema 约束 + 自动重试 + 降级策略，直接调用即可
2. **Prompt 效果不好（洞察不准）**：先用 3-5 个真实客户跟进数据做 Prompt 调试，记录效果再调整，不用一次到位
3. **前端 AI 面板放在哪**：建议放在 detail-grid 下方、跟进时间线上方，作为独立 section
4. **缓存 TTL 设多久**：建议 2 小时，可在配置文件调整
5. **问题超过半天没解决**：群里说明 + @梓源，不要自己死磕

---

## 七、交付物

- [ ] 后端：2 个新端点 + 缓存表
- [ ] 前端：AiInsightPanel + 3 个子组件
- [ ] 测试：5 个后端测试场景
- [ ] 自测报告：截图 + 验收清单勾选
