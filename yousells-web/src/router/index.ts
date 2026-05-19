import { createRouter, createWebHistory } from "vue-router";
import AppLayout from "@/layouts/AppLayout.vue";
import { useAuthStore } from "@/stores/auth";
import { RouteName } from "@/router/route-names";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      redirect: { name: RouteName.Dashboard }
    },
    {
      path: "/login",
      name: RouteName.Login,
      component: () => import("@/views/login/LoginView.vue")
    },
    {
      path: "/",
      component: AppLayout,
      meta: {
        requiresAuth: true
      },
      children: [
        {
          path: "dashboard",
          name: RouteName.Dashboard,
          component: () => import("@/views/dashboard/DashboardView.vue")
        },
        {
          path: "customers",
          name: RouteName.CustomerList,
          component: () => import("@/views/customer/CustomerListView.vue")
        },
        {
          path: "customers/:id",
          name: RouteName.CustomerDetail,
          component: () => import("@/views/customer/CustomerDetailView.vue")
        },
        {
          path: "tasks",
          name: RouteName.TaskBoard,
          component: () => import("@/views/task/TaskBoardView.vue")
        },
        {
          path: "reports/daily",
          name: RouteName.DailyReport,
          component: () => import("@/views/report/DailyReportView.vue")
        },
        {
          path: "reports/weekly",
          name: RouteName.WeeklyReport,
          component: () => import("@/views/report/WeeklyReportView.vue")
        },
        {
          path: "scripts",
          name: RouteName.ScriptLibrary,
          component: () => import("@/views/script/ScriptLibraryView.vue")
        }
      ]
    }
  ]
});

router.beforeEach(async (to) => {
  const authStore = useAuthStore();

  if (!authStore.initialized) {
    try {
      await authStore.initializeAuthAction();
    } catch {
      if (to.meta.requiresAuth) {
        return {
          name: RouteName.Login,
          query: {
            redirect: to.fullPath
          }
        };
      }
    }
  }

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return {
      name: RouteName.Login,
      query: {
        redirect: to.fullPath
      }
    };
  }

  if (to.name === RouteName.Login && authStore.isLoggedIn) {
    return {
      name: RouteName.Dashboard
    };
  }

  return true;
});

export default router;
