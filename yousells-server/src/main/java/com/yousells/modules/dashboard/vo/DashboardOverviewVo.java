package com.yousells.modules.dashboard.vo;

import java.util.List;

public record DashboardOverviewVo(
        int todayPendingFollowCount,
        int overdueCustomerCount,
        int recentNewCustomerCount,
        int highIntentCustomerCount,
        int totalCustomerCount,
        int monthlyClosedCount,
        List<ProgressDistributionItem> progressDistribution,
        List<IntentDistributionItem> intentDistribution,
        List<TrendDataPoint> trendData,
        List<DashboardTaskReminderVo> todayTasks,
        List<DashboardCustomerReminderVo> focusCustomers
) {
}
