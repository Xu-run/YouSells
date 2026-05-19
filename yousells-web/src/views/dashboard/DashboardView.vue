<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import PageSection from "@/components/app/PageSection.vue";
import { fetchDashboardOverview } from "@/api/dashboard";
import type { DashboardOverview } from "@/types/dashboard";

const loading = ref(false);
const overview = ref<DashboardOverview | null>(null);

const stats = computed(() => {
  if (!overview.value) {
    return [];
  }
  return [
    { label: "今日待跟进", value: overview.value.todayPendingFollowCount },
    { label: "逾期客户", value: overview.value.overdueCustomerCount },
    { label: "最近新增", value: overview.value.recentNewCustomerCount },
    { label: "高意向客户", value: overview.value.highIntentCustomerCount }
  ];
});

async function loadOverview() {
  loading.value = true;
  try {
    overview.value = await fetchDashboardOverview();
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "首页看板加载失败");
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  void loadOverview();
});
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="首页看板"
      description="这里已经接上了首页概览接口，后续嘉诚可以直接在这个壳子里继续补仪表卡片、预警列表和更多联动数据。"
    >
      <template #extra>
        <el-button :loading="loading" @click="loadOverview">刷新数据</el-button>
      </template>

      <div class="stats-grid">
        <div v-for="item in stats" :key="item.label" class="stat-card">
          <div class="stat-card__label">{{ item.label }}</div>
          <div class="stat-card__value">{{ item.value }}</div>
        </div>
      </div>

      <div class="split-grid" v-if="overview">
        <div class="list-card">
          <h3>今日公共安排</h3>
          <ul>
            <li v-for="task in overview.todayTasks" :key="task.taskId">
              {{ task.taskTitle }}｜{{ task.status }}｜{{ task.ownerDisplayName }}｜截止 {{ task.dueAt }}
            </li>
          </ul>
        </div>

        <div class="list-card">
          <h3>重点客户</h3>
          <ul>
            <li v-for="customer in overview.focusCustomers" :key="customer.customerId">
              {{ customer.nickname }}｜{{ customer.intentLevel }} 类｜{{ customer.currentStage }}｜下次 {{ customer.nextFollowAt }}
            </li>
          </ul>
        </div>
      </div>
    </PageSection>
  </div>
</template>
