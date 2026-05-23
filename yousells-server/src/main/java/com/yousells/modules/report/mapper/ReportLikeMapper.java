package com.yousells.modules.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yousells.modules.report.entity.ReportLikeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReportLikeMapper extends BaseMapper<ReportLikeEntity> {

    @Select("SELECT COUNT(*) FROM report_likes WHERE report_type = #{reportType} AND report_id = #{reportId}")
    int countByReport(@Param("reportType") String reportType, @Param("reportId") Long reportId);

    @Select("SELECT id FROM report_likes WHERE user_id = #{userId} AND report_type = #{reportType} AND report_id = #{reportId} LIMIT 1")
    Long findByUserAndReport(@Param("userId") Long userId, @Param("reportType") String reportType, @Param("reportId") Long reportId);
}
