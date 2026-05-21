<script setup lang="ts">
import { RouteName } from "@/router/route-names";
import { House, User, List, Document, ChatLineSquare } from "@element-plus/icons-vue";

interface NavItem {
  name: string;
  label: string;
  icon: unknown;
}

const items: NavItem[] = [
  { name: RouteName.Dashboard, label: "首页看板", icon: House },
  { name: RouteName.CustomerList, label: "客户管理", icon: User },
  { name: RouteName.TaskBoard, label: "公共安排", icon: List },
  { name: RouteName.DailyReport, label: "日报周报", icon: Document },
  { name: RouteName.TopicList, label: "攻略区", icon: ChatLineSquare }
];
</script>

<template>
  <aside class="app-sidebar">
    <div class="sidebar-brand">
      <div class="brand-mark">YS</div>
      <div>
        <div class="brand-name">YouSells</div>
        <div class="brand-subtitle">P0 工作台</div>
      </div>
    </div>

    <nav class="sidebar-nav">
      <RouterLink
        v-for="item in items"
        :key="item.name"
        :to="{ name: item.name }"
        class="sidebar-link"
      >
        <el-icon><component :is="item.icon" /></el-icon>
        <span>{{ item.label }}</span>
      </RouterLink>
    </nav>
  </aside>
</template>
