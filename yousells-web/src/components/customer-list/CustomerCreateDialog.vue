<script setup lang="ts">
import { ref, watch, onMounted, computed } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { useAuthStore } from "@/stores/auth";
import { useUserStore } from "@/stores/user";
import type { CustomerCreateRequest } from "@/types/customer-list";

const props = defineProps<{
  visible: boolean;
  loading: boolean;
}>();

const emit = defineEmits<{
  (e: "update:visible", value: boolean): void;
  (e: "submit", data: CustomerCreateRequest): void;
}>();

const authStore = useAuthStore();
const userStore = useUserStore();
const formRef = ref<FormInstance>();

const defaultUserId = authStore.currentUser?.userId ?? 1;

const form = ref<CustomerCreateRequest>({
  realName: "",
  grade: "",
  major: "",
  className: undefined,
  inviterUserId: defaultUserId,
  ownerUserId: defaultUserId,
  progress: "",
  intent: "",
  inviterNote: undefined
});

const gradeOptions = ["大一", "大二", "大三", "大四"];
const progressOptions = ["职规", "技术栈", "课程"];
const intentOptions = ["很稳", "可跟", "观望", "冷淡"];

const userOptions = computed(() => userStore.userOptions);

const rules: FormRules = {
  realName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  grade: [{ required: true, message: "请选择年级", trigger: "change" }],
  major: [{ required: true, message: "请输入专业", trigger: "blur" }],
  inviterUserId: [{ required: true, message: "请选择邀约人", trigger: "change" }],
  ownerUserId: [{ required: true, message: "请选择负责人", trigger: "change" }],
  progress: [{ required: true, message: "请选择进度", trigger: "change" }],
  intent: [{ required: true, message: "请选择意向", trigger: "change" }]
};

function resetForm() {
  form.value = {
    realName: "",
    grade: "",
    major: "",
    className: undefined,
    inviterUserId: defaultUserId,
    ownerUserId: defaultUserId,
    progress: "",
    intent: "",
    inviterNote: undefined
  };
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

onMounted(() => {
  void userStore.loadUsers();
});

watch(
  () => props.visible,
  (val) => {
    if (!val) resetForm();
  }
);
</script>

<template>
  <el-dialog
    title="新建学生客户"
    :model-value="visible"
    width="520px"
    :close-on-click-modal="false"
    @update:model-value="emit('update:visible', $event)"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="姓名" prop="realName">
        <el-input v-model="form.realName" placeholder="请输入姓名" />
      </el-form-item>

      <el-form-item label="年级" prop="grade">
        <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
          <el-option v-for="opt in gradeOptions" :key="opt" :label="opt" :value="opt" />
        </el-select>
      </el-form-item>

      <el-form-item label="专业" prop="major">
        <el-input v-model="form.major" placeholder="请输入专业" />
      </el-form-item>

      <el-form-item label="班级">
        <el-input v-model="form.className" placeholder="请输入班级（可选）" />
      </el-form-item>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="邀约人" prop="inviterUserId">
            <el-select v-model="form.inviterUserId" placeholder="请选择邀约人" style="width: 100%">
              <el-option
                v-for="opt in userOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="负责人" prop="ownerUserId">
            <el-select v-model="form.ownerUserId" placeholder="请选择负责人" style="width: 100%">
              <el-option
                v-for="opt in userOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="进度" prop="progress">
            <el-select v-model="form.progress" placeholder="请选择进度" style="width: 100%">
              <el-option v-for="opt in progressOptions" :key="opt" :label="opt" :value="opt" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="意向" prop="intent">
            <el-select v-model="form.intent" placeholder="请选择意向" style="width: 100%">
              <el-option v-for="opt in intentOptions" :key="opt" :label="opt" :value="opt" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="邀约备注">
        <el-input
          v-model="form.inviterNote"
          type="textarea"
          :rows="3"
          placeholder="怎么认识、什么场景、初步印象、关键背景..."
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        确认创建
      </el-button>
    </template>
  </el-dialog>
</template>
