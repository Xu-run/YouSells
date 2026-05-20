package com.yousells.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yousells.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customers")
public class CustomerEntity extends BaseEntity {

    private String customerCode;

    private String customerType;

    private String nickname;

    private String contactValue;

    private String sourcePlatform;

    private LocalDateTime addedAt;

    private Integer isNuistFreshman;

    private String expectedMajor;

    private String baseLevel;

    private String interestDirection;

    private String intentLevel;

    private String currentStage;

    private String currentConcern;

    private String latestFeedback;

    private LocalDateTime lastContactAt;

    private String nextFollowAction;

    private LocalDateTime nextFollowAt;

    private Long ownerUserId;

    private Long assistantUserId;

    private Integer needsSupport;

    private String conversionResult;

    private String remarks;
}
