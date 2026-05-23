<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  House,
  User,
  List,
  Document,
  Grid
} from "@element-plus/icons-vue";
import { RouteName } from "@/router/route-names";

const route = useRoute();
const router = useRouter();

const items = [
  { name: RouteName.Dashboard, label: "首页", icon: House },
  { name: RouteName.CustomerList, label: "客户", icon: User },
  { name: RouteName.TaskBoard, label: "任务", icon: List },
  { name: RouteName.DailyReport, label: "报告", icon: Document },
  { name: RouteName.ReportPlaza, label: "广场", icon: Grid }
];

const active = computed(() => route.name as string);

function go(name: string) {
  router.push({ name });
}
</script>

<template>
  <nav class="app-mobile-nav">
    <button
      v-for="item in items"
      :key="item.name"
      class="mobile-nav-item"
      :class="{ 'mobile-nav-item--active': active === item.name }"
      @click="go(item.name)"
    >
      <el-icon size="20"><component :is="item.icon" /></el-icon>
      <span class="mobile-nav-item__label">{{ item.label }}</span>
    </button>
  </nav>
</template>

<style scoped>
.app-mobile-nav {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: var(--color-bg-surface);
  border-top: 1px solid var(--color-border);
  z-index: 100;
  justify-content: space-around;
  align-items: center;
  padding: 0 8px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.04);
}

.mobile-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 6px 12px;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  font-size: 11px;
  cursor: pointer;
  transition: color 0.2s;
}

.mobile-nav-item--active {
  color: var(--color-primary);
}

@media (max-width: 700px) {
  .app-mobile-nav {
    display: flex;
  }
}
</style>
