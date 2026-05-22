package com.yousells.modules.customer.vo;

import java.time.LocalDateTime;

public record CustomerListItemVo(
        Long id,
        String realName,
        String grade,
        String major,
        String className,
        String progress,
        String intent,
        String ownerDisplayName,
        String inviterDisplayName,
        LocalDateTime createdAt
) {}
