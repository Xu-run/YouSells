<script setup lang="ts">
defineProps<{
  title: string;
  description: string;
  owner?: string;
  note?: string;
}>();
</script>

<template>
  <div class="empty-state-card">
    <div class="empty-state-card__badge">P0 骨架</div>
    <h3 class="empty-state-card__title">{{ title }}</h3>
    <p class="empty-state-card__description">{{ description }}</p>
    <p v-if="owner" class="empty-state-card__meta">模块负责人：{{ owner }}</p>
    <p v-if="note" class="empty-state-card__meta">下一步：{{ note }}</p>
  </div>
</template>
