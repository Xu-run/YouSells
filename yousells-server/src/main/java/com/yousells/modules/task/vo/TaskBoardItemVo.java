package com.yousells.modules.task.vo;

import java.time.LocalDateTime;

public record TaskBoardItemVo(
        Long id,
        String taskTitle,
        String taskType,
        String status,
        String priority,
        String ownerDisplayName,
        String assistantDisplayName,
        LocalDateTime dueAt,
        String nextAction
) {
}
