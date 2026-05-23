package com.yousells.modules.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report_likes")
public class ReportLikeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String reportType;

    private Long reportId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
