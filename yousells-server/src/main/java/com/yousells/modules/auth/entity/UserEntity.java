package com.yousells.modules.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yousells.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class UserEntity extends BaseEntity {

    private String username;

    private String passwordHash;

    private String displayName;

    private String realName;

    private String phone;

    private String email;

    private String status;

    private LocalDateTime lastLoginAt;
}
