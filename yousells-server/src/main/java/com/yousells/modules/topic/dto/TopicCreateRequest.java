package com.yousells.modules.topic.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicCreateRequest(
        @NotBlank(message = "title cannot be blank") String title,
        String description,
        @NotBlank(message = "category cannot be blank") String category
) {}
