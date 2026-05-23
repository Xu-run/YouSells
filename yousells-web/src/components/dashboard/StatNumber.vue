<script setup lang="ts">
import { watch, ref, onUnmounted } from "vue";

const props = defineProps<{
  value: number;
  loading: boolean;
}>();

const display = ref(0);
let rafId: number | null = null;

function animate(from: number, to: number) {
  const startTime = performance.now();
  const duration = 800;

  function step(now: number) {
    const elapsed = now - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const ease = 1 - Math.pow(1 - progress, 4);
    display.value = Math.round(from + (to - from) * ease);

    if (progress < 1) {
      rafId = requestAnimationFrame(step);
    }
  }

  if (rafId) cancelAnimationFrame(rafId);
  rafId = requestAnimationFrame(step);
}

watch(
  () => props.value,
  (newVal, oldVal) => {
    if (!props.loading) {
      animate(oldVal ?? 0, newVal);
    }
  },
  { immediate: true }
);

watch(() => props.loading, (v) => {
  if (!v) {
    animate(0, props.value);
  }
});

onUnmounted(() => {
  if (rafId) cancelAnimationFrame(rafId);
});
</script>

<template>
  <span class="stat-number">{{ loading ? "-" : display }}</span>
</template>

<style scoped>
.stat-number {
  font-variant-numeric: tabular-nums;
}
</style>
