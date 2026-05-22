<script setup lang="ts">
import {
  UserFilled,
  Bell,
  Warning,
  Plus,
  Star,
  CircleCheck
} from "@element-plus/icons-vue";

interface StatItem {
  label: string;
  value: number;
  icon: string;
}

defineProps<{
  stats: StatItem[];
  loading: boolean;
}>();

const iconMap: Record<string, any> = {
  Users: UserFilled,
  Bell: Bell,
  Warning: Warning,
  Plus: Plus,
  Star: Star,
  Success: CircleCheck
};

const colorMap: Record<string, string> = {
  Users: "#2563eb",
  Bell: "#f59e0b",
  Warning: "#ef4444",
  Plus: "#10b981",
  Star: "#8b5cf6",
  Success: "#06b6d4"
};
</script>

<template>
  <div class="stats-grid">
    <div v-for="item in stats" :key="item.label" class="stat-card">
      <div class="stat-card__top">
        <div
          class="stat-card__icon"
          :style="{ background: (colorMap[item.icon] ?? '#2563eb') + '12', color: colorMap[item.icon] ?? '#2563eb' }"
        >
          <el-icon size="18"><component :is="iconMap[item.icon]" /></el-icon>
        </div>
        <div class="stat-card__label">{{ item.label }}</div>
      </div>
      <div class="stat-card__value">{{ loading ? "-" : item.value }}</div>
    </div>
  </div>
</template>

<style scoped>
.stat-card__top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.stat-card__icon {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: var(--radius-md);
}

.stat-card__label {
  color: var(--color-text-muted);
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.02em;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  letter-spacing: -0.02em;
  line-height: 1;
}
</style>
