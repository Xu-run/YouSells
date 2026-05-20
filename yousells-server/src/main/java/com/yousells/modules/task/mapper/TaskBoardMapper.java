package com.yousells.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.task.entity.TaskBoardEntity;
import com.yousells.modules.task.vo.TaskBoardItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskBoardMapper extends BaseMapper<TaskBoardEntity> {

    IPage<TaskBoardItemVo> selectPageWithOwner(Page<TaskBoardItemVo> page, @Param("status") String status);

    List<TaskBoardItemVo> selectAllWithOwner();
}
