package com.yousells.modules.customer.dto;

public record CustomerQueryRequest(
        String keyword,
        String grade,
        String major,
        String progress,
        String intent,
        Long ownerUserId,
        Integer page,
        Integer pageSize
) {}
