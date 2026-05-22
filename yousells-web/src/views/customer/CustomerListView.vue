<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import PageSection from "@/components/app/PageSection.vue";
import CustomerFilterBar from "@/components/customer-list/CustomerFilterBar.vue";
import CustomerTable from "@/components/customer-list/CustomerTable.vue";
import { fetchCustomers } from "@/api/customer-list";
import type { CustomerListItem, CustomerQuery } from "@/types/customer-list";
import { RouteName } from "@/router/route-names";
import { isUnauthorizedError } from "@/utils/request-error";

const router = useRouter();
const loading = ref(false);
const error = ref(false);
const customers = ref<CustomerListItem[]>([]);
const total = ref(0);

const query = reactive<CustomerQuery>({
  page: 1,
  pageSize: 20,
  keyword: ""
});

async function loadCustomers() {
  error.value = false;
  loading.value = true;
  try {
    const data = await fetchCustomers(query);
    customers.value = data.list;
    total.value = data.total;
  } catch (e) {
    if (isUnauthorizedError(e)) {
      return;
    }
    error.value = true;
    ElMessage.error(e instanceof Error ? e.message : "客户列表加载失败");
  } finally {
    loading.value = false;
  }
}

function onSearch() {
  void loadCustomers();
}

function onReset() {
  void loadCustomers();
}

function onPageChange(page: number) {
  query.page = page;
  void loadCustomers();
}

function onRowClick(row: CustomerListItem) {
  router.push({ name: RouteName.CustomerDetail, params: { id: row.id } });
}

function onQueryUpdate(v: CustomerQuery) {
  Object.keys(query).forEach(k => delete (query as Record<string, unknown>)[k]);
  Object.assign(query, v);
}

onMounted(() => {
  void loadCustomers();
});
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="客户管理"
      description="学生客户总表，支持按年级/进度/意向筛选"
    >
      <template #extra>
        <el-button :loading="loading" @click="loadCustomers">刷新列表</el-button>
      </template>

      <CustomerFilterBar :model-value="query" :loading @update:model-value="onQueryUpdate" @search="onSearch" @reset="onReset" />

      <div v-if="error" class="dashboard-error">
        <p>数据加载失败，请点击刷新重试</p>
      </div>

      <CustomerTable
        :customers
        :loading
        :total
        :page="query.page ?? 1"
        :page-size="query.pageSize ?? 20"
        @page-change="onPageChange"
        @row-click="onRowClick"
      />
    </PageSection>
  </div>
</template>
