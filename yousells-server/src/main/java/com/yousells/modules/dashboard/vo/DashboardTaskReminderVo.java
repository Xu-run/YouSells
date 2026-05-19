package com.yousells.modules.dashboard.vo;

import java.time.LocalDateTime;

public record DashboardTaskReminderVo(
        Long taskId,
        String taskTitle,
        String status,
        String ownerDisplayName,
        LocalDateTime dueAt
) {
}
