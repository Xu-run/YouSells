package com.yousells.common.security;

import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class DataScopeHelper {

    private static final long CACHE_TTL_MS = 5 * 60 * 1000; // 5 minutes
    private static final ConcurrentHashMap<Long, CacheEntry> CACHE = new ConcurrentHashMap<>();

    private DataScopeHelper() {
    }

    private record CacheEntry(List<Long> subordinateIds, long timestamp) {
    }

    /**
     * 递归查询指定用户的所有下级 ID（含间接下级）。
     *
     * @param userId     当前用户 ID
     * @param userMapper 用户 Mapper
     * @return 下级用户 ID 列表（含直接下级和间接下级）
     */
    public static List<Long> getSubordinateIds(Long userId, UserMapper userMapper) {
        if (userId == null) {
            return Collections.emptyList();
        }
        long now = System.currentTimeMillis();
        CacheEntry entry = CACHE.get(userId);
        if (entry != null && now - entry.timestamp < CACHE_TTL_MS) {
            return entry.subordinateIds;
        }
        List<Long> result = computeSubordinateIds(userId, userMapper);
        CACHE.put(userId, new CacheEntry(result, now));
        return result;
    }

    private static List<Long> computeSubordinateIds(Long userId, UserMapper userMapper) {
        List<Long> directSubordinates = queryDirectSubordinates(userId, userMapper);
        List<Long> allSubordinates = new ArrayList<>(directSubordinates);
        for (Long subId : directSubordinates) {
            allSubordinates.addAll(getSubordinateIds(subId, userMapper));
        }
        return allSubordinates;
    }

    private static List<Long> queryDirectSubordinates(Long managerId, UserMapper userMapper) {
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getManagerUserId, managerId)
                .eq(UserEntity::getIsDeleted, 0);
        return userMapper.selectList(wrapper).stream()
                .map(UserEntity::getId)
                .toList();
    }

    /**
     * 清除指定用户的缓存（在用户组织结构变更时调用）。
     */
    public static void invalidateCache(Long userId) {
        if (userId != null) {
            CACHE.remove(userId);
        }
    }

    /**
     * 清除所有缓存。
     */
    public static void invalidateAllCache() {
        CACHE.clear();
    }
}
