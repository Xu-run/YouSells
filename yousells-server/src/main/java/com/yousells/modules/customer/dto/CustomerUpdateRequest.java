package com.yousells.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerUpdateRequest(
        @NotBlank(message = "nickname cannot be blank")
        String nickname,
        @NotBlank(message = "contactValue cannot be blank")
        String contactValue,
        @NotBlank(message = "sourcePlatform cannot be blank")
        String sourcePlatform,
        String expectedMajor,
        String baseLevel,
        String intentLevel,
        String currentStage,
        String currentConcern,
        String latestFeedback,
        @NotNull(message = "ownerUserId cannot be null")
        Long ownerUserId,
        Long assistantUserId,
        String remarks
) {
}
