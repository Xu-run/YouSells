package com.yousells.modules.topic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.common.security.SecurityUserContext;
import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;
import com.yousells.modules.topic.convert.TopicConvert;
import com.yousells.modules.topic.dto.TopicCreateRequest;
import com.yousells.modules.topic.dto.TopicQueryRequest;
import com.yousells.modules.topic.dto.TopicReplyCreateRequest;
import com.yousells.modules.topic.entity.TopicEntity;
import com.yousells.modules.topic.entity.TopicReplyEntity;
import com.yousells.modules.topic.mapper.TopicMapper;
import com.yousells.modules.topic.mapper.TopicReplyMapper;
import com.yousells.modules.topic.service.TopicService;
import com.yousells.modules.topic.vo.TopicDetailVo;
import com.yousells.modules.topic.vo.TopicListItemVo;
import com.yousells.modules.topic.vo.TopicReplyVo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicMapper topicMapper;
    private final TopicReplyMapper topicReplyMapper;
    private final UserMapper userMapper;

    public TopicServiceImpl(TopicMapper topicMapper,
                            TopicReplyMapper topicReplyMapper,
                            UserMapper userMapper) {
        this.topicMapper = topicMapper;
        this.topicReplyMapper = topicReplyMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResponse<TopicListItemVo> pageTopics(TopicQueryRequest request) {
        int pageNum = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 20 : request.pageSize();

        Page<TopicEntity> page = new Page<>(pageNum, pageSize);
        IPage<TopicEntity> result = topicMapper.pageTopics(page, request.category(), request.keyword());

        List<TopicEntity> entities = result.getRecords();
        if (entities.isEmpty()) {
            return PageResponse.of(List.of(), pageNum, pageSize, result.getTotal());
        }

        Map<Long, String> authorNameMap = buildAuthorNameMap(entities);
        Map<Long, Integer> replyCountMap = buildReplyCountMap(entities);

        List<TopicListItemVo> list = entities.stream()
                .map(e -> TopicConvert.toListItemVo(e,
                        authorNameMap.getOrDefault(e.getAuthorUserId(), ""),
                        replyCountMap.getOrDefault(e.getId(), 0)))
                .toList();

        return PageResponse.of(list, result.getCurrent(), result.getSize(), result.getTotal());
    }

    @Override
    public TopicDetailVo getTopicDetail(Long id) {
        TopicEntity entity = topicMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "topic not found");
        }

        String authorName = lookupDisplayName(entity.getAuthorUserId());

        LambdaQueryWrapper<TopicReplyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicReplyEntity::getTopicId, id)
                .orderByAsc(TopicReplyEntity::getCreatedAt);
        List<TopicReplyEntity> replies = topicReplyMapper.selectList(wrapper);

        Set<Long> userIds = replies.stream()
                .map(TopicReplyEntity::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> userNameMap = buildUserNameMap(userIds);

        List<TopicReplyVo> replyVos = replies.stream()
                .map(r -> TopicConvert.toReplyVo(r, userNameMap.getOrDefault(r.getUserId(), "")))
                .toList();

        return TopicConvert.toDetailVo(entity, authorName, replyVos);
    }

    @Override
    public Long createTopic(TopicCreateRequest request) {
        Long authorUserId = SecurityUserContext.requireCurrentUser().userId();
        TopicEntity entity = TopicConvert.toEntity(request, authorUserId);
        topicMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public Long createReply(Long topicId, TopicReplyCreateRequest request) {
        TopicEntity topic = topicMapper.selectById(topicId);
        if (topic == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "topic not found");
        }

        Long userId = SecurityUserContext.requireCurrentUser().userId();
        TopicReplyEntity entity = TopicConvert.toReplyEntity(request, topicId, userId);
        topicReplyMapper.insert(entity);

        topicMapper.updateById(topic);

        return entity.getId();
    }

    @Override
    public void markSolved(Long id) {
        TopicEntity entity = topicMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "topic not found");
        }

        Long currentUserId = SecurityUserContext.requireCurrentUser().userId();
        if (!entity.getAuthorUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCodeConstants.BAD_REQUEST, "only the topic author can mark as solved");
        }

        entity.setSolved(1);
        topicMapper.updateById(entity);
    }

    @Override
    public void markBestSolution(Long topicId, Long replyId) {
        TopicEntity topic = topicMapper.selectById(topicId);
        if (topic == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "topic not found");
        }

        Long currentUserId = SecurityUserContext.requireCurrentUser().userId();
        if (!topic.getAuthorUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCodeConstants.BAD_REQUEST, "only the topic author can mark best solution");
        }

        TopicReplyEntity reply = topicReplyMapper.selectById(replyId);
        if (reply == null || !reply.getTopicId().equals(topicId)) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "reply not found");
        }

        LambdaUpdateWrapper<TopicReplyEntity> clearWrapper = new LambdaUpdateWrapper<>();
        clearWrapper.eq(TopicReplyEntity::getTopicId, topicId)
                .set(TopicReplyEntity::getIsSolution, 0);
        topicReplyMapper.update(clearWrapper);

        reply.setIsSolution(1);
        topicReplyMapper.updateById(reply);
    }

    private Map<Long, String> buildAuthorNameMap(List<TopicEntity> entities) {
        Set<Long> authorIds = entities.stream()
                .map(TopicEntity::getAuthorUserId)
                .collect(Collectors.toSet());
        return buildUserNameMap(authorIds);
    }

    private Map<Long, String> buildUserNameMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getRealName, (a, b) -> a));
    }

    private Map<Long, Integer> buildReplyCountMap(List<TopicEntity> entities) {
        List<Long> topicIds = entities.stream()
                .map(TopicEntity::getId)
                .toList();
        if (topicIds.isEmpty()) {
            return Map.of();
        }

        LambdaQueryWrapper<TopicReplyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TopicReplyEntity::getTopicId, topicIds);
        List<TopicReplyEntity> allReplies = topicReplyMapper.selectList(wrapper);

        Map<Long, Long> countMap = allReplies.stream()
                .collect(Collectors.groupingBy(TopicReplyEntity::getTopicId, Collectors.counting()));

        return countMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
    }

    private String lookupDisplayName(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        return user != null ? user.getRealName() : "";
    }
}
