package com.yousells.modules.followup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FollowUpCreateRequest(
        @NotNull(message = "customerId cannot be null")
        Long customerId,
        @NotBlank(message = "followType cannot be blank")
        String followType,
        @NotBlank(message = "communicatedContent cannot be blank")
        String communicatedContent,
        String customerFeedback,
        String currentConcern,
        String nextAction,
        LocalDateTime nextFollowAt
) {
}
