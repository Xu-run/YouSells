package com.yousells.modules.dashboard.vo;

import java.time.LocalDateTime;

public record DashboardCustomerReminderVo(
        Long customerId,
        String nickname,
        String intentLevel,
        String currentStage,
        LocalDateTime nextFollowAt
) {
}
