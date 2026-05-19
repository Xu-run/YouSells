package com.yousells.modules.customer.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CustomerTagsUpdateRequest(
        @NotEmpty(message = "tagIds cannot be empty")
        List<Long> tagIds
) {
}
