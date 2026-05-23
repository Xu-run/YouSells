package com.yousells.modules.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReportCommentRequest(
        @NotBlank(message = "评论内容不能为空")
        @Size(max = 500, message = "评论最多 500 字")
        String content
) {
}
