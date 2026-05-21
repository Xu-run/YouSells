<script setup lang="ts">
import { ref, watch } from "vue";
import { ElMessage } from "element-plus";
import type { CustomerDetail } from "@/types/customer-detail";
import { stageLabel, intentLabel } from "@/constants/stage";

defineProps<{
  detail: CustomerDetail;
  loading: boolean;
}>();

const emit = defineEmits<{
  updated: [];
}>();
</script>

<template>
  <div class="profile-card">
    <div class="profile-card__header">
      <h3 class="page-section__title" style="font-size: 18px; margin: 0">
        学生档案
      </h3>
    </div>

    <div v-loading="loading" class="detail-grid">
      <div class="detail-item">
        <div class="detail-item__label">姓名</div>
        <div class="detail-item__value">{{ detail.realName }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-item__label">年级</div>
        <div class="detail-item__value">{{ detail.grade }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-item__label">专业</div>
        <div class="detail-item__value">{{ detail.major }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-item__label">班级</div>
        <div class="detail-item__value">{{ detail.className || "暂无" }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-item__label">当前进度</div>
        <div class="detail-item__value">{{ detail.progress }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-item__label">意向</div>
        <div class="detail-item__value">{{ detail.intent }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-item__label">负责人</div>
        <div class="detail-item__value">{{ detail.ownerDisplayName }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-item__label">邀约人</div>
        <div class="detail-item__value">{{ detail.inviterDisplayName || "暂无" }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-card {
  background: #fafcff;
  border-radius: 18px;
  padding: 20px;
  border: 1px solid rgba(37, 99, 235, 0.06);
}

.profile-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
