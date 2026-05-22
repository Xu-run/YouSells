package com.yousells.modules.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.customer.entity.CustomerEntity;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper extends BaseMapper<CustomerEntity> {

    IPage<CustomerEntity> pageCustomers(Page<?> page,
                                        @Param("keyword") String keyword,
                                        @Param("grade") String grade,
                                        @Param("major") String major,
                                        @Param("progress") String progress,
                                        @Param("intent") String intent,
                                        @Param("ownerUserId") Long ownerUserId,
                                        @Param("visibleUserIds") List<Long> visibleUserIds);

    List<UserDisplayName> selectUserDisplayNames(@Param("userIds") List<Long> userIds);

    @Data
    class UserDisplayName {
        private Long userId;
        private String displayName;
    }
}
