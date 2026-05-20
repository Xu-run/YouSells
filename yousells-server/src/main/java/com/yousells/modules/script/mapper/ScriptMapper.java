package com.yousells.modules.script.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.script.entity.ScriptEntity;
import com.yousells.modules.script.vo.ScriptVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScriptMapper extends BaseMapper<ScriptEntity> {

    IPage<ScriptVo> selectPageWithCategory(Page<ScriptVo> page,
                                           @Param("categoryId") Long categoryId,
                                           @Param("keyword") String keyword);
}
