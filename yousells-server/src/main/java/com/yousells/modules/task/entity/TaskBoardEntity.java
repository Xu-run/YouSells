package com.yousells.modules.task.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yousells.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("task_boards")
public class TaskBoardEntity extends BaseEntity {

    @TableField("task_title")
    private String taskTitle;

    @TableField("task_type")
    private String taskType;

    @TableField("task_description")
    private String taskDescription;

    @TableField("status")
    private String status;

    @TableField("priority")
    private String priority;

    @TableField("owner_user_id")
    private Long ownerUserId;

    @TableField("assistant_user_id")
    private Long assistantUserId;

    @TableField("start_at")
    private LocalDateTime startAt;

    @TableField("due_at")
    private LocalDateTime dueAt;

    @TableField("next_action")
    private String nextAction;
}
