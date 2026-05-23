import { ref, watch, onUnmounted } from "vue";

export function useCountUp(target: number, duration = 800) {
  const display = ref(0);
  let rafId: number | null = null;

  function animate(from: number, to: number) {
    const startTime = performance.now();

    function step(now: number) {
      const elapsed = now - startTime;
      const progress = Math.min(elapsed / duration, 1);
      // easeOutQuart
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
    () => target,
    (newVal, oldVal) => {
      animate(oldVal ?? 0, newVal);
    },
    { immediate: true }
  );

  onUnmounted(() => {
    if (rafId) cancelAnimationFrame(rafId);
  });

  return display;
}
