package com.yousells.modules.customer.vo;

import java.time.LocalDateTime;

public record CustomerDetailVo(
        Long id,
        String realName,
        String grade,
        String major,
        String className,
        Long inviterUserId,
        String inviterDisplayName,
        Long ownerUserId,
        String ownerDisplayName,
        String progress,
        String intent,
        String inviterNote,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
