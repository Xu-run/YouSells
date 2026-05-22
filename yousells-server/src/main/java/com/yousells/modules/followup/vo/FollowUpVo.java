package com.yousells.modules.followup.vo;

import java.time.LocalDateTime;

public record FollowUpVo(
        Long id,
        Long customerId,
        Long userId,
        String userDisplayName,
        String progress,
        String content,
        String feedback,
        String nextAction,
        LocalDateTime createdAt
) {}
