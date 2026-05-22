package com.yousells.modules.topic.vo;

import java.time.LocalDateTime;
import java.util.List;

public record TopicDetailVo(
        Long id,
        String title,
        String description,
        String category,
        Long authorUserId,
        String authorName,
        Boolean solved,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<TopicReplyVo> replies
) {}
