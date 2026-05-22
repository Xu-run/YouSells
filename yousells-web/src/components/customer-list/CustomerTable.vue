<script setup lang="ts">
import type { CustomerListItem } from "@/types/customer-list";

defineProps<{
  customers: CustomerListItem[];
  loading: boolean;
  total: number;
  page: number;
  pageSize: number;
}>();

const emit = defineEmits<{
  (e: "page-change", page: number): void;
  (e: "row-click", row: CustomerListItem): void;
}>();

function intentTagType(intent: string): "success" | "warning" | "info" | "danger" {
  const map: Record<string, "success" | "warning" | "info" | "danger"> = {
    "很稳": "success", "可跟": "warning", "观望": "info", "冷淡": "danger"
  };
  return map[intent] || "info";
}

function progressTagType(progress: string): "success" | "warning" | "info" {
  const map: Record<string, "success" | "warning" | "info"> = {
    "课程": "success", "技术栈": "warning", "职规": "info"
  };
  return map[progress] || "info";
}
</script>

<template>
  <div class="customer-table-wrapper">
    <el-table
      :data="customers"
      v-loading="loading"
      highlight-current-row
      @row-click="(row: CustomerListItem) => emit('row-click', row)"
    >
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="grade" label="年级" width="80">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ row.grade }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="major" label="专业" min-width="140" />
      <el-table-column prop="className" label="班级" width="110">
        <template #default="{ row }">
          {{ row.className || '—' }}
        </template>
      </el-table-column>
      <el-table-column prop="progress" label="进度" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="progressTagType(row.progress)">{{ row.progress }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="intent" label="意向" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="intentTagType(row.intent)">{{ row.intent }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="ownerDisplayName" label="负责人" width="110" />
      <el-table-column prop="inviterDisplayName" label="邀约人" width="110" />
    </el-table>

    <el-pagination
      v-if="total > pageSize"
      :current-page="page"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      style="margin-top: 16px; justify-content: flex-end"
      @current-change="(p: number) => emit('page-change', p)"
    />
  </div>
</template>
