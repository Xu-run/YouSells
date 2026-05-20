package com.yousells.modules.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.report.entity.WeeklyReportEntity;
import com.yousells.modules.report.vo.WeeklyReportVo;

public interface WeeklyReportMapper extends BaseMapper<WeeklyReportEntity> {

    IPage<WeeklyReportVo> selectPageWithUser(Page<WeeklyReportVo> page);
}
