<script setup lang="ts">
import { ref, watch } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import type { TopicCreateRequest } from "@/types/topic";

const props = defineProps<{
  visible: boolean;
  loading: boolean;
}>();

const emit = defineEmits<{
  (e: "update:visible", value: boolean): void;
  (e: "submit", data: TopicCreateRequest): void;
}>();

const formRef = ref<FormInstance>();

const form = ref<TopicCreateRequest>({
  title: "",
  description: "",
  category: ""
});

const categoryOptions = ["邀约", "沟通", "跟进", "转化", "其他"];

const rules: FormRules = {
  title: [{ required: true, message: "请输入问题标题", trigger: "blur" }],
  category: [{ required: true, message: "请选择分类", trigger: "change" }]
};

function resetForm() {
  form.value = { title: "", description: "", category: "" };
  formRef.value?.resetFields();
}

function handleClose() {
  emit("update:visible", false);
  resetForm();
}

async function handleSubmit() {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    emit("submit", { ...form.value });
  } catch {
    ElMessage.warning("请填写必填项");
  }
}

watch(
  () => props.visible,
  (val) => {
    if (!val) resetForm();
  }
);
</script>

<template>
  <el-dialog
    title="提问"
    :model-value="visible"
    width="520px"
    :close-on-click-modal="false"
    @update:model-value="emit('update:visible', $event)"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="简短描述你的问题" />
      </el-form-item>

      <el-form-item label="分类" prop="category">
        <el-select v-model="form.category" placeholder="选择分类" style="width: 100%">
          <el-option
            v-for="opt in categoryOptions"
            :key="opt"
            :label="opt"
            :value="opt"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="详细描述">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="补充背景、场景、已尝试的方案..."
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        提交问题
      </el-button>
    </template>
  </el-dialog>
</template>
