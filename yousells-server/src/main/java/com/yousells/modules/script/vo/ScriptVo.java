package com.yousells.modules.script.vo;

import java.time.LocalDateTime;

public record ScriptVo(
        Long id,
        Long categoryId,
        String categoryName,
        String title,
        String applicableScene,
        String status,
        String content,
        LocalDateTime updatedAt
) {
}
