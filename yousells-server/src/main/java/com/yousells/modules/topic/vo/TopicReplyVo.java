package com.yousells.modules.topic.vo;

import java.time.LocalDateTime;

public record TopicReplyVo(
        Long id,
        Long userId,
        String userName,
        String content,
        Boolean isSolution,
        LocalDateTime createdAt
) {}
