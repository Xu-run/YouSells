package com.yousells.modules.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.report.vo.ReportPlazaItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportPlazaMapper extends BaseMapper<ReportPlazaItemVo> {

    IPage<ReportPlazaItemVo> selectDailyPlaza(Page<?> page,
                                               @Param("filterUserId") Long filterUserId,
                                               @Param("currentUserId") Long currentUserId);

    IPage<ReportPlazaItemVo> selectWeeklyPlaza(Page<?> page,
                                                @Param("filterUserId") Long filterUserId,
                                                @Param("currentUserId") Long currentUserId);
}
