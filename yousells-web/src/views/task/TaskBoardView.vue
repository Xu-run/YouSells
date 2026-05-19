<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import PageSection from "@/components/app/PageSection.vue";
import EmptyStateCard from "@/components/app/EmptyStateCard.vue";
import { fetchTaskBoard } from "@/api/task";
import type { TaskBoardColumn } from "@/types/task";

const loading = ref(false);
const columns = ref<TaskBoardColumn[]>([]);

async function loadBoard() {
  loading.value = true;
  try {
    columns.value = await fetchTaskBoard();
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "公共安排看板加载失败");
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  void loadBoard();
});
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="公共安排区"
      description="任务看板接口已经接好，后续可以继续补拖拽状态流转、负责人筛选和优先级视图。"
    >
      <template #extra>
        <el-button :loading="loading" @click="loadBoard">刷新看板</el-button>
      </template>

      <div class="split-grid">
        <div v-for="column in columns" :key="column.status" class="list-card">
          <h3>{{ column.title }}</h3>
          <ul>
            <li v-for="item in column.items" :key="item.id">
              {{ item.taskTitle }}｜{{ item.priority }}｜{{ item.ownerDisplayName }}｜{{ item.dueAt || "未设置截止时间" }}
            </li>
          </ul>
        </div>
      </div>

      <EmptyStateCard
        title="下一步建议"
        description="把新增任务、编辑任务、状态变更和看板拖拽补齐后，这个模块就能承接团队公共安排。"
        owner="许润 + 哲涛"
        note="这个模块后续需要做前后端联调，尤其是状态流转、截止时间和负责人变更。"
      />
    </PageSection>
  </div>
</template>
