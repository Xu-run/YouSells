<script setup lang="ts">
import AppHeader from "@/components/app/AppHeader.vue";
import AppSidebar from "@/components/app/AppSidebar.vue";
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
