<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import PageSection from "@/components/app/PageSection.vue";
import EmptyStateCard from "@/components/app/EmptyStateCard.vue";
import { fetchCustomers } from "@/api/customer-list";
import type { CustomerListItem, CustomerQuery } from "@/types/customer-list";

const loading = ref(false);
const customers = ref<CustomerListItem[]>([]);
const total = ref(0);

const query = reactive<CustomerQuery>({
  page: 1,
  pageSize: 20,
  keyword: ""
});

async function loadCustomers() {
  loading.value = true;
  try {
    const data = await fetchCustomers(query);
    customers.value = data.list;
    total.value = data.total;
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "客户列表加载失败");
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  void loadCustomers();
});
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="客户总表"
      description="客户列表、筛选和分页基线已经接上接口占位，志明和嘉诚后续可以直接往表格编辑、标签筛选和负责人分配上继续扩。"
    >
      <template #extra>
        <el-button :loading="loading" @click="loadCustomers">刷新列表</el-button>
      </template>

      <div class="table-summary">
        <span>当前展示：{{ customers.length }} 条</span>
        <span>总数：{{ total }}</span>
        <span>P0 默认分页：20 条/页</span>
      </div>

      <el-table :data="customers" border stripe v-loading="loading">
        <el-table-column prop="customerCode" label="客户编号" min-width="150" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="sourcePlatform" label="来源平台" min-width="110" />
        <el-table-column prop="intentLevel" label="意向等级" width="100" />
        <el-table-column prop="currentStage" label="当前阶段" min-width="140" />
        <el-table-column prop="ownerDisplayName" label="负责人" width="110" />
        <el-table-column prop="nextFollowAt" label="下次跟进" min-width="170" />
      </el-table>

      <div class="mini-tag-row">
        <el-tag v-for="item in customers[0]?.tags ?? []" :key="item" type="info" effect="plain">{{ item }}</el-tag>
      </div>

      <EmptyStateCard
        title="下一步建议"
        description="把筛选条件、客户新建弹窗、编辑抽屉和跳转详情页补齐，这个页面就能承接 P0 的客户主流程。"
        owner="嘉诚 + 志明"
        note="这一页后续一定要做前后端联调测试，尤其是筛选、分页和编辑后的回显。"
      />
    </PageSection>
  </div>
</template>
