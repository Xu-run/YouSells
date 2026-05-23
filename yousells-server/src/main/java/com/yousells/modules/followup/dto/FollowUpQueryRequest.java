package com.yousells.modules.followup.dto;

import jakarta.validation.constraints.Max;

public record FollowUpQueryRequest(
        Long customerId,
        Integer page,
        @Max(value = 500, message = "pageSize cannot exceed 500")
        Integer pageSize
) {}
