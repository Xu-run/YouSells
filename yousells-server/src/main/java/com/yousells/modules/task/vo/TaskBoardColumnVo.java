package com.yousells.modules.task.vo;

import java.util.List;

public record TaskBoardColumnVo(
        String status,
        String title,
        List<TaskBoardItemVo> items
) {
}
