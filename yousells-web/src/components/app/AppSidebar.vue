<script setup lang="ts">
import { computed } from "vue";
import { RouteName } from "@/router/route-names";
import { useAuthStore } from "@/stores/auth";
import {
  House,
  User,
  List,
  Document,
  ChatLineSquare,
  Setting,
  UserFilled
} from "@element-plus/icons-vue";

const authStore = useAuthStore();

const isT3 = computed(() => authStore.currentUser?.level === "T3");

interface NavItem {
  name: string;
  label: string;
  icon: unknown;
}

const mainItems: NavItem[] = [
  { name: RouteName.Dashboard, label: "首页看板", icon: House },
  { name: RouteName.CustomerList, label: "客户管理", icon: User },
  { name: RouteName.TaskBoard, label: "公共安排", icon: List },
  { name: RouteName.DailyReport, label: "日报周报", icon: Document },
  { name: RouteName.TopicList, label: "攻略区", icon: ChatLineSquare }
];

const settingItems: NavItem[] = [
  { name: RouteName.Profile, label: "个人设置", icon: Setting }
];

if (isT3.value) {
  settingItems.push({ name: RouteName.MemberManage, label: "成员管理", icon: UserFilled });
}
</script>

<template>
  <aside class="app-sidebar">
    <div class="sidebar-brand">
      <div class="brand-mark">YS</div>
      <div>
        <div class="brand-name">YouSells</div>
        <div class="brand-subtitle">学生客户管理平台</div>
      </div>
    </div>

    <nav class="sidebar-nav">
      <RouterLink
        v-for="item in mainItems"
        :key="item.name"
        :to="{ name: item.name }"
        class="sidebar-link"
      >
        <el-icon><component :is="item.icon" /></el-icon>
        <span>{{ item.label }}</span>
      </RouterLink>
    </nav>

    <div class="sidebar-divider" />

    <nav class="sidebar-nav">
      <RouterLink
        v-for="item in settingItems"
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
