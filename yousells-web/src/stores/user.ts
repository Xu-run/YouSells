import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { fetchUserList } from "@/api/user";
import type { UserListItem } from "@/types/user";

export const useUserStore = defineStore("user", () => {
  const users = ref<UserListItem[]>([]);
  const loading = ref(false);
  const loaded = ref(false);

  const activeUsers = computed(() => users.value.filter(u => u.status === "ACTIVE"));

  const userOptions = computed(() =>
    activeUsers.value.map(u => ({
      label: u.realName,
      value: u.userId
    }))
  );

  async function loadUsers(force = false) {
    if (loaded.value && !force) {
      return users.value;
    }
    loading.value = true;
    try {
      users.value = await fetchUserList();
      loaded.value = true;
      return users.value;
    } catch {
      return [];
    } finally {
      loading.value = false;
    }
  }

  function getUserName(userId: number | null | undefined): string {
    if (!userId) return "-";
    const user = users.value.find(u => u.userId === userId);
    return user?.realName ?? String(userId);
  }

  return {
    users,
    loading,
    loaded,
    activeUsers,
    userOptions,
    loadUsers,
    getUserName
  };
});
