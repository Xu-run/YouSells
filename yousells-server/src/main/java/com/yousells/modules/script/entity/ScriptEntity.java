package com.yousells.modules.script.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yousells.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("scripts")
public class ScriptEntity extends BaseEntity {

    @TableField("category_id")
    private Long categoryId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("applicable_scene")
    private String applicableScene;

    @TableField("status")
    private String status;
}
