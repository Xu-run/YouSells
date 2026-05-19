<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import PageSection from "@/components/app/PageSection.vue";
import EmptyStateCard from "@/components/app/EmptyStateCard.vue";
import { fetchCustomerDetail } from "@/api/customer-detail";
import { fetchFollowUps } from "@/api/followup";
import type { CustomerDetail } from "@/types/customer-detail";
import type { FollowUpRecord } from "@/types/followup";

const route = useRoute();
const loading = ref(false);
const detail = ref<CustomerDetail | null>(null);
const followUps = ref<FollowUpRecord[]>([]);

async function loadData() {
  loading.value = true;
  try {
    const id = String(route.params.id ?? "1001");
    const [detailData, followUpData] = await Promise.all([
      fetchCustomerDetail(id),
      fetchFollowUps(id)
    ]);
    detail.value = detailData;
    followUps.value = followUpData.list;
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "客户详情加载失败");
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  void loadData();
});
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="客户详情与跟进记录"
      description="这一页已经把详情接口和跟进记录接口串起来了，后续许润可以直接补编辑区、时间线表单和下一次跟进联动。"
    >
      <template #extra>
        <el-button :loading="loading" @click="loadData">刷新详情</el-button>
      </template>

      <div v-if="detail" class="detail-grid">
        <div class="detail-item">
          <div class="detail-item__label">客户昵称</div>
          <div class="detail-item__value">{{ detail.nickname }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-item__label">联系方式</div>
          <div class="detail-item__value">{{ detail.contactValue }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-item__label">当前阶段</div>
          <div class="detail-item__value">{{ detail.currentStage }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-item__label">当前顾虑</div>
          <div class="detail-item__value">{{ detail.currentConcern || "暂无" }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-item__label">负责人 / 协助人</div>
          <div class="detail-item__value">{{ detail.ownerDisplayName }} / {{ detail.assistantDisplayName || "暂无" }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-item__label">下次跟进</div>
          <div class="detail-item__value">{{ detail.nextFollowAction }}｜{{ detail.nextFollowAt }}</div>
        </div>
      </div>

      <div>
        <div class="page-section__title" style="font-size: 18px;">跟进时间线</div>
        <div v-for="item in followUps" :key="item.id" class="timeline-card">
          <div class="timeline-card__meta">
            {{ item.createdAt }}｜{{ item.followType }}｜{{ item.operatorDisplayName }}
          </div>
          <div class="timeline-card__content">
            {{ item.communicatedContent }}
            <br />
            客户反馈：{{ item.customerFeedback || "暂无" }}
            <br />
            下一步：{{ item.nextAction || "暂无" }}
          </div>
        </div>
      </div>

      <EmptyStateCard
        title="联调提醒"
        description="客户详情页会同时牵涉详情接口、跟进记录接口和下一次跟进更新接口，这一页后续必须做页面联调与提交流程测试。"
        owner="许润 + 志明"
        note="重点测试详情刷新、时间线新增后回显、下一次跟进字段同步更新。"
      />
    </PageSection>
  </div>
</template>
