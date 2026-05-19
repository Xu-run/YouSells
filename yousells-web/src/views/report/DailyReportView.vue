<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import PageSection from "@/components/app/PageSection.vue";
import EmptyStateCard from "@/components/app/EmptyStateCard.vue";
import { fetchDailyReports } from "@/api/report";
import type { DailyReport } from "@/types/report";

const loading = ref(false);
const reports = ref<DailyReport[]>([]);

async function loadReports() {
  loading.value = true;
  try {
    const data = await fetchDailyReports();
    reports.value = data.list;
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "日报加载失败");
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
      title="日报页面"
      description="日报查询接口已经可用，后续直接补提交表单、列表筛选和管理员汇总视图。"
    >
      <template #extra>
        <el-button :loading="loading" @click="loadReports">刷新日报</el-button>
      </template>

      <div class="list-card">
        <ul>
          <li v-for="item in reports" :key="item.id">
            {{ item.reportDate }}｜{{ item.userDisplayName }}｜今日完成：{{ item.todayWork }}
          </li>
        </ul>
      </div>

      <EmptyStateCard
        title="联调提醒"
        description="日报模块后续涉及提交、编辑、查看三条链路，完成表单后记得立刻做页面联调和提交流程测试。"
        owner="许润 + 哲涛"
        note="重点看提交后的列表刷新、重复日期限制和权限边界。"
      />
    </PageSection>
  </div>
</template>
