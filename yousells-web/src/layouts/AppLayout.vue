<script setup lang="ts">
import { onMounted } from "vue";
import AppHeader from "@/components/app/AppHeader.vue";
import AppSidebar from "@/components/app/AppSidebar.vue";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

onMounted(() => {
  void userStore.loadUsers();
});
</script>

<template>
  <div class="app-layout">
    <AppSidebar />
    <div class="app-layout__main">
      <AppHeader />
      <main class="app-layout__content">
        <RouterView />
      </main>
    </div>
  </div>
</template>
