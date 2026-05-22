package com.yousells.modules.topic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.topic.entity.TopicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TopicMapper extends BaseMapper<TopicEntity> {

    IPage<TopicEntity> pageTopics(Page<?> page,
                                   @Param("category") String category,
                                   @Param("keyword") String keyword);
}
