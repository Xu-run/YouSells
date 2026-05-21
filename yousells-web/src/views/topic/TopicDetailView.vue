<script setup lang="ts">
import PageSection from "@/components/app/PageSection.vue";
import EmptyStateCard from "@/components/app/EmptyStateCard.vue";
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="攻略详情"
      description="问题详情、回答列表与最佳方案。P1 由嘉诚负责前端实现。"
    >
      <EmptyStateCard
        title="P1 待开发"
        description="攻略详情页将在 P1 Phase 3 由嘉诚实现。"
        owner="嘉诚"
      />
    </PageSection>
  </div>
</template>
