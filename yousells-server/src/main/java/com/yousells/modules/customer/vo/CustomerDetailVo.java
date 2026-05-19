package com.yousells.modules.customer.vo;

import java.time.LocalDateTime;
import java.util.List;

public record CustomerDetailVo(
        Long id,
        String customerCode,
        String customerType,
        String nickname,
        String contactValue,
        String sourcePlatform,
        String expectedMajor,
        String baseLevel,
        String interestDirection,
        String intentLevel,
        String currentStage,
        String currentConcern,
        String latestFeedback,
        LocalDateTime lastContactAt,
        String nextFollowAction,
        LocalDateTime nextFollowAt,
        String ownerDisplayName,
        String assistantDisplayName,
        List<String> tags,
        String remarks
) {
}
