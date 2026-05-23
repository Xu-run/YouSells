package com.yousells.modules.task.dto;

import jakarta.validation.constraints.Max;

public record TaskQueryRequest(
        Integer page,
        @Max(value = 500, message = "pageSize cannot exceed 500")
        Integer pageSize,
        String status,
        Long ownerUserId,
        String direction
) {
}
