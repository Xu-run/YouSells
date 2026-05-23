package com.yousells.modules.report.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReportPlazaItemVo {

    private Long id;
    private String type;
    private LocalDate reportDate;
    private String weekKey;
    private Long userId;
    private String userRealName;
    private String userLevel;
    private String summary;
    private String issues;
    private String tomorrowPlan;
    private String nextWeekPlan;
    private Integer newCustomerCount;
    private Integer followUpCount;
    private Integer progressAdvanceCount;
    private Integer convertedCount;
    private Integer taskCompletedCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean likedByMe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean edited;
}
