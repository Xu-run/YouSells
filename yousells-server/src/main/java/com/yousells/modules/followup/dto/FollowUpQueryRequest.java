package com.yousells.modules.followup.dto;

public record FollowUpQueryRequest(
        Long customerId,
        Integer page,
        Integer pageSize
) {
}
