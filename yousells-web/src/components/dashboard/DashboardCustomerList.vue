<script setup lang="ts">
import { useRouter } from "vue-router";
import type { DashboardCustomerReminder } from "@/types/dashboard";
import { stageLabel, intentLabel } from "@/constants/stage";
import { relativeDate } from "@/utils/format";
import { RouteName } from "@/router/route-names";

defineProps<{
  customers: DashboardCustomerReminder[];
  loading: boolean;
}>();

const router = useRouter();

function goToCustomer(customerId: number) {
  router.push({ name: RouteName.CustomerDetail, params: { id: customerId } });
}
</script>

<template>
  <div class="list-card">
    <h3>重点客户</h3>
    <p v-if="loading" class="list-card__placeholder">加载中...</p>
    <p v-else-if="customers.length === 0" class="list-card__placeholder">暂无重点客户</p>
    <ul v-else>
      <li
        v-for="customer in customers"
        :key="customer.customerId"
        class="list-card__item"
        @click="goToCustomer(customer.customerId)"
      >
        {{ customer.nickname }}｜{{ intentLabel(customer.intentLevel) }}｜{{ stageLabel(customer.currentStage) }}｜{{ relativeDate(customer.nextFollowAt) }}
      </li>
    </ul>
  </div>
</template>
