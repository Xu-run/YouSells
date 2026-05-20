package com.yousells.modules.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.report.entity.DailyReportEntity;
import com.yousells.modules.report.vo.DailyReportVo;

public interface DailyReportMapper extends BaseMapper<DailyReportEntity> {

    IPage<DailyReportVo> selectPageWithUser(Page<DailyReportVo> page);
}
