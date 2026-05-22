package com.yousells.modules.topic.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicReplyCreateRequest(
        @NotBlank(message = "content cannot be blank") String content
) {}
