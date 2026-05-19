<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import PageSection from "@/components/app/PageSection.vue";
import EmptyStateCard from "@/components/app/EmptyStateCard.vue";
import { fetchScriptCategories, fetchScripts } from "@/api/script";
import type { ScriptCategory, ScriptItem } from "@/types/script";

const loading = ref(false);
const categories = ref<ScriptCategory[]>([]);
const scripts = ref<ScriptItem[]>([]);

async function loadScripts() {
  loading.value = true;
  try {
    const [categoryData, scriptData] = await Promise.all([
      fetchScriptCategories(),
      fetchScripts()
    ]);
    categories.value = categoryData;
    scripts.value = scriptData.list;
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "话术库加载失败");
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  void loadScripts();
});
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="话术库页面"
      description="分类接口和列表接口都已经接上，后续可以继续补编辑器、分类筛选和详情抽屉。"
    >
      <template #extra>
        <el-button :loading="loading" @click="loadScripts">刷新话术库</el-button>
      </template>

      <div class="mini-tag-row">
        <el-tag v-for="category in categories" :key="category.id" effect="plain">{{ category.categoryName }}</el-tag>
      </div>

      <el-table :data="scripts" border stripe v-loading="loading">
        <el-table-column prop="categoryName" label="分类" min-width="120" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="applicableScene" label="适用场景" min-width="220" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="updatedAt" label="更新时间" min-width="170" />
      </el-table>

      <EmptyStateCard
        title="联调提醒"
        description="话术库模块后续接编辑功能时要同步验证分类切换、详情回显和保存后的列表刷新。"
        owner="许润 + 哲涛"
        note="这是页面交互比较集中的模块，后面一定要做实际表单联调。"
      />
    </PageSection>
  </div>
</template>
