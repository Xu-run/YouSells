package com.yousells.modules.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yousells.modules.report.entity.ReportCommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReportCommentMapper extends BaseMapper<ReportCommentEntity> {

    @Select("SELECT COUNT(*) FROM report_comments WHERE report_type = #{reportType} AND report_id = #{reportId} AND is_deleted = 0")
    int countByReport(@Param("reportType") String reportType, @Param("reportId") Long reportId);
}
