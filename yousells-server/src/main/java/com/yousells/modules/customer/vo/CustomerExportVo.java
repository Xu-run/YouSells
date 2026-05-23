package com.yousells.modules.customer.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerExportVo {

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("姓名")
    private String realName;

    @ExcelProperty("年级")
    private String grade;

    @ExcelProperty("专业")
    private String major;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("进度")
    private String progress;

    @ExcelProperty("意向")
    private String intent;

    @ExcelProperty("归属人")
    private String ownerDisplayName;

    @ExcelProperty("邀约人")
    private String inviterDisplayName;

    @ExcelProperty("创建时间")
    private String createdAt;
}
