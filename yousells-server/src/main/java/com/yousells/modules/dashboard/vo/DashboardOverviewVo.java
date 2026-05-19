package com.yousells.modules.dashboard.vo;

import java.util.List;

public record DashboardOverviewVo(
        int todayPendingFollowCount,
        int overdueCustomerCount,
        int recentNewCustomerCount,
        int highIntentCustomerCount,
        List<DashboardTaskReminderVo> todayTasks,
        List<DashboardCustomerReminderVo> focusCustomers
) {
}
