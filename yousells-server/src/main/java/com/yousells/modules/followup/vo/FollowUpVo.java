package com.yousells.modules.followup.vo;

import java.time.LocalDateTime;

public record FollowUpVo(
        Long id,
        Long customerId,
        String followType,
        String communicatedContent,
        String customerFeedback,
        String currentConcern,
        String nextAction,
        LocalDateTime nextFollowAt,
        String operatorDisplayName,
        String ownerDisplayName,
        LocalDateTime createdAt
) {
}
