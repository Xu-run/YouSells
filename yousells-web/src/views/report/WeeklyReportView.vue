<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import PageSection from "@/components/app/PageSection.vue";
import EmptyStateCard from "@/components/app/EmptyStateCard.vue";
import { fetchWeeklyReports } from "@/api/report";
import type { WeeklyReport } from "@/types/report";

const loading = ref(false);
const reports = ref<WeeklyReport[]>([]);

async function loadReports() {
  loading.value = true;
  try {
    const data = await fetchWeeklyReports();
    reports.value = data.list;
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "周报加载失败");
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  void loadReports();
});
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="周报页面"
      description="周报查询接口已经接好，后续可以在这里继续补成员视图、管理汇总和复盘入口。"
    >
      <template #extra>
        <el-button :loading="loading" @click="loadReports">刷新周报</el-button>
      </template>

      <div class="list-card">
        <ul>
          <li v-for="item in reports" :key="item.id">
            {{ item.weekKey }}｜{{ item.userDisplayName }}｜本周总结：{{ item.weeklySummary }}
          </li>
        </ul>
      </div>

      <EmptyStateCard
        title="下一步建议"
        description="补齐周报提交表单与汇总看板后，日报周报模块就能覆盖 P0 的团队汇报主流程。"
        owner="许润 + 哲涛"
        note="这一页后续也需要做联调，尤其是周标识去重和管理端查看汇总。"
      />
    </PageSection>
  </div>
</template>
