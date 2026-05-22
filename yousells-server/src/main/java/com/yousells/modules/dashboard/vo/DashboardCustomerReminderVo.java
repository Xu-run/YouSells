package com.yousells.modules.dashboard.vo;

import java.time.LocalDateTime;

public record DashboardCustomerReminderVo(
        Long customerId,
        String realName,
        String intent,
        String progress,
        LocalDateTime nextFollowAt
) {
}
