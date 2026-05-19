import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { fetchCurrentUser, login, logout, type LoginRequest } from "@/api/auth";
import type { CurrentUser } from "@/types/auth";
import { clearAccessToken, getAccessToken, setAccessToken } from "@/utils/auth-token";

export const useAuthStore = defineStore("auth", () => {
  const accessToken = ref<string | null>(getAccessToken());
  const currentUser = ref<CurrentUser | null>(null);
  const loading = ref(false);
  const initialized = ref(false);

  const isLoggedIn = computed(() => Boolean(accessToken.value && currentUser.value));

  function clearAuthState() {
    accessToken.value = null;
    currentUser.value = null;
    clearAccessToken();
  }

  function applyLoginState(token: string, userInfo: CurrentUser) {
    accessToken.value = token;
    currentUser.value = userInfo;
    setAccessToken(token);
  }

  async function initializeAuthAction() {
    if (initialized.value) {
      return currentUser.value;
    }
    accessToken.value = getAccessToken();
    if (!accessToken.value) {
      currentUser.value = null;
      initialized.value = true;
      return null;
    }
    return fetchCurrentUserAction();
  }

  async function loginAction(payload: LoginRequest) {
    loading.value = true;
    try {
      const result = await login(payload);
      applyLoginState(result.accessToken, result.userInfo);
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
    accessToken.value = getAccessToken();
    if (!accessToken.value) {
      clearAuthState();
      initialized.value = true;
      return null;
    }
    loading.value = true;
    try {
      currentUser.value = await fetchCurrentUser();
      return currentUser.value;
    } catch (error) {
      clearAuthState();
      throw error;
    } finally {
      initialized.value = true;
      loading.value = false;
    }
  }

  async function logoutAction() {
    try {
      if (accessToken.value) {
        await logout();
      }
    } finally {
      clearAuthState();
      initialized.value = true;
    }
  }

  function clearAuthStateAction() {
    clearAuthState();
    initialized.value = true;
  }

  return {
    accessToken,
    currentUser,
    loading,
    initialized,
    isLoggedIn,
    initializeAuthAction,
    loginAction,
    fetchCurrentUserAction,
    logoutAction,
    clearAuthStateAction
  };
});
