<script setup lang="ts">
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

function onSearch() {
  emit("update:modelValue", { ...props.modelValue, page: 1 });
  emit("search");
}

function onReset() {
  emit("update:modelValue", { page: 1, pageSize: props.modelValue.pageSize ?? 20 });
  emit("reset");
}
</script>

<template>
  <div class="filter-bar">
    <el-input
      :model-value="modelValue.keyword ?? ''"
      :disabled="loading"
      placeholder="搜索姓名/专业"
      clearable
      style="width: 180px"
      @update:model-value="emit('update:modelValue', { ...modelValue, keyword: $event || undefined })"
      @keyup.enter="onSearch"
    />
    <el-select
      :model-value="modelValue.grade ?? ''"
      :disabled="loading"
      placeholder="年级"
      style="width: 100px"
      @update:model-value="emit('update:modelValue', { ...modelValue, grade: $event || undefined }); onSearch()"
    >
      <el-option v-for="opt in gradeOptions" :key="opt" :label="opt" :value="opt === '全部' ? '' : opt" />
    </el-select>
    <el-select
      :model-value="modelValue.progress ?? ''"
      :disabled="loading"
      placeholder="进度"
      style="width: 120px"
      @update:model-value="emit('update:modelValue', { ...modelValue, progress: $event || undefined }); onSearch()"
    >
      <el-option v-for="opt in progressOptions" :key="opt" :label="opt" :value="opt === '全部' ? '' : opt" />
    </el-select>
    <el-select
      :model-value="modelValue.intent ?? ''"
      :disabled="loading"
      placeholder="意向"
      style="width: 120px"
      @update:model-value="emit('update:modelValue', { ...modelValue, intent: $event || undefined }); onSearch()"
    >
      <el-option v-for="opt in intentOptions" :key="opt" :label="opt" :value="opt === '全部' ? '' : opt" />
    </el-select>
    <el-button type="primary" :loading="loading" @click="onSearch">搜索</el-button>
    <el-button :disabled="loading" @click="onReset">重置</el-button>
  </div>
</template>
