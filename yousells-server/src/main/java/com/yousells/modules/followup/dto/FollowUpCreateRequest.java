package com.yousells.modules.followup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FollowUpCreateRequest(
        @NotNull(message = "customerId cannot be null") Long customerId,
        @NotBlank(message = "progress cannot be blank") String progress,
        @NotBlank(message = "content cannot be blank") String content,
        String feedback,
        String nextAction
) {}
