package com.yousells.modules.customer.dto;

import jakarta.validation.constraints.Max;

public record CustomerQueryRequest(
        String keyword,
        String grade,
        String major,
        String progress,
        String intent,
        Long ownerUserId,
        Integer page,
        @Max(value = 500, message = "pageSize cannot exceed 500")
        Integer pageSize
) {}
