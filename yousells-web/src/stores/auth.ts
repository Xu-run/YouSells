import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { fetchCurrentUser, login, logout, type LoginRequest } from "@/api/auth";
import type { CurrentUser } from "@/types/auth";

export const useAuthStore = defineStore("auth", () => {
  const currentUser = ref<CurrentUser | null>(null);
  const loading = ref(false);
  const initialized = ref(false);

  const isLoggedIn = computed(() => currentUser.value !== null);

  async function loginAction(payload: LoginRequest) {
    loading.value = true;
    try {
      currentUser.value = await login(payload);
      initialized.value = true;
      return currentUser.value;
    } finally {
      loading.value = false;
    }
  }

  async function fetchCurrentUserAction() {
    if (loading.value) {
      return currentUser.value;
    }
    loading.value = true;
    try {
      currentUser.value = await fetchCurrentUser();
      return currentUser.value;
    } catch (error) {
      currentUser.value = null;
      throw error;
    } finally {
      initialized.value = true;
      loading.value = false;
    }
  }

  async function logoutAction() {
    try {
      await logout();
    } finally {
      currentUser.value = null;
      initialized.value = true;
    }
  }

  return {
    currentUser,
    loading,
    initialized,
    isLoggedIn,
    loginAction,
    fetchCurrentUserAction,
    logoutAction
  };
});
