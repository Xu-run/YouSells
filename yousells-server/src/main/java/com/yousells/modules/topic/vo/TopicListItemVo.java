package com.yousells.modules.topic.vo;

import java.time.LocalDateTime;

public record TopicListItemVo(
        Long id,
        String title,
        String category,
        Long authorUserId,
        String authorName,
        Boolean solved,
        int replyCount,
        LocalDateTime createdAt
) {}
