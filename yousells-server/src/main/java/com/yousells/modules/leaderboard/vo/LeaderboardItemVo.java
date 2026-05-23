package com.yousells.modules.leaderboard.vo;

import lombok.Data;

@Data
public class LeaderboardItemVo {

    private Long userId;
    private String realName;
    private String level;

    private Integer newCustomerCount;
    private Integer followUpCount;
    private Integer convertedCount;
    private Integer taskCompletedCount;
    private Double conversionRate;
}
