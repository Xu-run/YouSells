<script setup lang="ts">
defineProps<{
  title: string;
  description?: string;
}>();
</script>

<template>
  <section class="page-card">
    <header class="page-section__header">
      <div>
        <h2 class="page-section__title">{{ title }}</h2>
        <p v-if="description" class="page-section__description">{{ description }}</p>
      </div>
      <slot name="extra" />
    </header>
    <div class="page-section__body">
      <slot />
    </div>
  </section>
</template>
