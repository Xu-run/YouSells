package com.yousells.modules.topic.dto;

public record TopicQueryRequest(
        String category,
        String keyword,
        Integer page,
        Integer pageSize
) {}
