package com.yousells.modules.report.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportCommentVo {

    private Long id;
    private Long userId;
    private String userRealName;
    private String content;
    private LocalDateTime createdAt;
}
