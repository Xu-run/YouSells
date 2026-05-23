import { ref, watch } from "vue";

type Theme = "light" | "dark";

const STORAGE_KEY = "yousells-theme";

function getInitialTheme(): Theme {
  const stored = localStorage.getItem(STORAGE_KEY) as Theme | null;
  if (stored === "dark" || stored === "light") return stored;
  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
}

const theme = ref<Theme>(getInitialTheme());

export function useTheme() {
  function applyTheme(t: Theme) {
    document.documentElement.setAttribute("data-theme", t);
    localStorage.setItem(STORAGE_KEY, t);
  }

  function toggleTheme() {
    theme.value = theme.value === "light" ? "dark" : "light";
  }

  watch(theme, applyTheme, { immediate: true });

  return {
    theme,
    isDark: () => theme.value === "dark",
    toggleTheme
  };
}
