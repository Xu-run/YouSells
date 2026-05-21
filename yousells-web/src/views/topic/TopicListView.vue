<script setup lang="ts">
import PageSection from "@/components/app/PageSection.vue";
import EmptyStateCard from "@/components/app/EmptyStateCard.vue";
</script>

<template>
  <div class="page-shell">
    <PageSection
      title="攻略区"
      description="团队协作知识库，全员提问、全员出方案。P1 由嘉诚负责前端实现。"
    >
      <EmptyStateCard
        title="P1 待开发"
        description="攻略区前端页面将在 P1 Phase 3 由嘉诚实现，包含问题列表、分类筛选、问题详情、回答列表和最佳方案标记。"
        owner="嘉诚"
        note="等待 BE-106 攻略区后端完成后联调"
      />
    </PageSection>
  </div>
</template>
