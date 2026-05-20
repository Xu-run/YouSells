package com.yousells.modules.customer.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CustomerTagsUpdateRequest(
        @NotNull(message = "tagIds cannot be null")
        List<Long> tagIds
) {
}
