<script setup lang="ts">
import { ref, computed } from "vue";
import { Search, RefreshRight, Filter } from "@element-plus/icons-vue";
import type { CustomerQuery } from "@/types/customer-list";

const props = defineProps<{
  modelValue: CustomerQuery;
  loading: boolean;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: CustomerQuery): void;
  (e: "search"): void;
  (e: "reset"): void;
}>();

const gradeOptions = ["全部", "大一", "大二", "大三", "大四"];
const progressOptions = ["全部", "职规", "技术栈", "课程"];
const intentOptions = ["全部", "很稳", "可跟", "观望", "冷淡"];

const filterPopoverVisible = ref(false);

const hasActiveFilters = computed(() =>
  !!(props.modelValue.progress || props.modelValue.intent)
);

function onSearch() {
  emit("update:modelValue", { ...props.modelValue, page: 1 });
  emit("search");
}

function onReset() {
  emit("update:modelValue", { page: 1, pageSize: props.modelValue.pageSize ?? 20 });
  emit("reset");
}

function updateKeyword(v: string) {
  emit("update:modelValue", { ...props.modelValue, keyword: v || undefined });
}

function updateGrade(v: string) {
  emit("update:modelValue", { ...props.modelValue, grade: v || undefined, page: 1 });
  emit("search");
}

function updateProgress(v: string) {
  emit("update:modelValue", { ...props.modelValue, progress: v || undefined, page: 1 });
  emit("search");
}

function updateIntent(v: string) {
  emit("update:modelValue", { ...props.modelValue, intent: v || undefined, page: 1 });
  emit("search");
}

function resetFilters() {
  emit("update:modelValue", {
    ...props.modelValue,
    progress: undefined,
    intent: undefined,
    page: 1
  });
  filterPopoverVisible.value = false;
  emit("search");
}
</script>

<template>
  <div class="filter-bar">
    <el-input
      :model-value="modelValue.keyword ?? ''"
      :disabled="loading"
      placeholder="搜索姓名、专业..."
      clearable
      :prefix-icon="Search"
      style="width: 260px"
      @update:model-value="updateKeyword"
      @keyup.enter="onSearch"
      @clear="onSearch"
    />

    <el-select
      :model-value="modelValue.grade ?? ''"
      :disabled="loading"
      placeholder="年级"
      style="width: 110px"
      @update:model-value="updateGrade"
    >
      <el-option v-for="opt in gradeOptions" :key="opt" :label="opt" :value="opt === '全部' ? '' : opt" />
    </el-select>

    <el-popover
      v-model:visible="filterPopoverVisible"
      trigger="click"
      :width="240"
      popper-class="filter-popover"
    >
      <template #reference>
        <el-button
          text
          :type="hasActiveFilters ? 'primary' : undefined"
        >
          <el-icon><Filter /></el-icon>
          筛选
          <el-badge v-if="hasActiveFilters" is-dot style="margin-left: 4px" />
        </el-button>
      </template>
      <div class="filter-popover__content">
        <div class="filter-popover__field">
          <span class="filter-popover__label">进度</span>
          <el-select
            :model-value="modelValue.progress ?? ''"
            placeholder="全部进度"
            style="width: 100%"
            @update:model-value="updateProgress"
          >
            <el-option v-for="opt in progressOptions" :key="opt" :label="opt" :value="opt === '全部' ? '' : opt" />
          </el-select>
        </div>
        <div class="filter-popover__field">
          <span class="filter-popover__label">意向</span>
          <el-select
            :model-value="modelValue.intent ?? ''"
            placeholder="全部意向"
            style="width: 100%"
            @update:model-value="updateIntent"
          >
            <el-option v-for="opt in intentOptions" :key="opt" :label="opt" :value="opt === '全部' ? '' : opt" />
          </el-select>
        </div>
        <el-button text size="small" @click="resetFilters">
          <el-icon><RefreshRight /></el-icon> 重置筛选
        </el-button>
      </div>
    </el-popover>

    <el-button
      text
      circle
      :icon="RefreshRight"
      :disabled="loading"
      title="重置全部"
      @click="onReset"
    />
  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}

.filter-popover__content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-popover__field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.filter-popover__label {
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 500;
}
</style>
