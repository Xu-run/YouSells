package com.yousells.modules.customer.vo;

import java.time.LocalDateTime;
import java.util.List;

public record CustomerListItemVo(
        Long id,
        String customerCode,
        String nickname,
        String customerType,
        String sourcePlatform,
        String intentLevel,
        String currentStage,
        String ownerDisplayName,
        LocalDateTime lastContactAt,
        LocalDateTime nextFollowAt,
        List<String> tags
) {
}
