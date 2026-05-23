package com.yousells.modules.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yousells.modules.notification.entity.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NotificationMapper extends BaseMapper<NotificationEntity> {

    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    int countUnread(@Param("userId") Long userId);

    @Update("UPDATE notifications SET is_read = 1, updated_at = NOW() WHERE user_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    int markAllRead(@Param("userId") Long userId);
}
