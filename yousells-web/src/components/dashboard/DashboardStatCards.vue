<script setup lang="ts">
import {
  UserFilled,
  Bell,
  Warning,
  Plus,
  Star,
  CircleCheck
} from "@element-plus/icons-vue";
import StatNumber from "./StatNumber.vue";

export interface StatItem {
  label: string;
  value: number;
  icon: string;
}

defineProps<{
  stats: StatItem[];
  loading: boolean;
}>();

const emit = defineEmits<{
  click: [label: string];
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
    <div
      v-for="item in stats"
      :key="item.label"
      class="stat-card"
      @click="emit('click', item.label)"
    >
      <div class="stat-card__top">
        <div
          class="stat-card__icon"
          :style="{ background: (colorMap[item.icon] ?? '#2563eb') + '12', color: colorMap[item.icon] ?? '#2563eb' }"
        >
          <el-icon size="18"><component :is="iconMap[item.icon]" /></el-icon>
        </div>
        <div class="stat-card__label">{{ item.label }}</div>
      </div>
      <div
        class="stat-card__value"
        :style="{ color: colorMap[item.icon] ?? '#2563eb' }"
      >
        <StatNumber :value="item.value" :loading="loading" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.stat-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 16px 20px;
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: box-shadow 0.25s cubic-bezier(0.2, 0, 0, 1),
              transform 0.25s cubic-bezier(0.2, 0, 0, 1);
}

.stat-card:hover {
  box-shadow: 0 8px 24px rgba(20, 32, 61, 0.1);
  transform: translateY(-2px);
}

.stat-card:active {
  transform: translateY(0) scale(0.99);
}

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
  transition: transform 0.3s cubic-bezier(0.2, 0, 0, 1);
}

.stat-card:hover .stat-card__icon {
  transform: rotate(8deg) scale(1.08);
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
  letter-spacing: -0.02em;
  line-height: 1;
  transition: transform 0.2s;
}

.stat-card:hover .stat-card__value {
  transform: scale(1.03);
}
</style>
