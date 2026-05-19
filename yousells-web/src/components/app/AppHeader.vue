<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { RouteName } from "@/router/route-names";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const router = useRouter();

const userLabel = computed(() => authStore.currentUser?.displayName ?? "未登录");

async function handleLogout() {
  await authStore.logoutAction();
  await router.replace({ name: RouteName.Login });
}
</script>

<template>
  <header class="app-header">
    <div>
      <div class="app-header__title">团队客户管理平台</div>
      <div class="app-header__subtitle">客户、跟进、协作与汇报统一沉淀</div>
    </div>

    <div class="app-header__actions">
      <div class="app-user-chip">
        <span class="app-user-chip__name">{{ userLabel }}</span>
        <span class="app-user-chip__role">{{ authStore.currentUser?.roles?.join(" / ") ?? "游客" }}</span>
      </div>
      <el-button type="primary" plain @click="handleLogout">退出登录</el-button>
    </div>
  </header>
</template>
