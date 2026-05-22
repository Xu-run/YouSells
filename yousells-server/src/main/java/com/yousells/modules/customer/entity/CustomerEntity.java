package com.yousells.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yousells.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customers")
public class CustomerEntity extends BaseEntity {

    private String realName;

    private String grade;

    private String major;

    private String className;

    private Long inviterUserId;

    private Long ownerUserId;

    private String progress;

    private String intent;

    private String inviterNote;
}
