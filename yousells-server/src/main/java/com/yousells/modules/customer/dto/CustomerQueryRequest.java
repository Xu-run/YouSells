package com.yousells.modules.customer.dto;

public record CustomerQueryRequest(
        Integer page,
        Integer pageSize,
        String keyword,
        String intentLevel,
        String currentStage,
        Long ownerUserId,
        String sourcePlatform
) {
}
