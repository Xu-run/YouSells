package com.yousells.modules.customer.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerImportDto {

    @ExcelProperty("姓名")
    private String realName;

    @ExcelProperty("年级")
    private String grade;

    @ExcelProperty("专业")
    private String major;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("微信号")
    private String wechat;

    @ExcelProperty("负责人ID")
    private Long ownerUserId;

    @ExcelProperty("邀约人ID")
    private Long inviterUserId;

    @ExcelProperty("进度")
    private String progress;

    @ExcelProperty("意向")
    private String intent;

    @ExcelProperty("来源渠道")
    private String sourceChannel;

    @ExcelProperty("邀约备注")
    private String inviterNote;
}
