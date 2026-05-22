package com.yousells.modules.topic.convert;

import com.yousells.modules.topic.dto.TopicCreateRequest;
import com.yousells.modules.topic.dto.TopicReplyCreateRequest;
import com.yousells.modules.topic.entity.TopicEntity;
import com.yousells.modules.topic.entity.TopicReplyEntity;
import com.yousells.modules.topic.vo.TopicDetailVo;
import com.yousells.modules.topic.vo.TopicListItemVo;
import com.yousells.modules.topic.vo.TopicReplyVo;

import java.util.List;

public class TopicConvert {

    private TopicConvert() {
    }

    public static TopicListItemVo toListItemVo(TopicEntity entity, String authorName, int replyCount) {
        return new TopicListItemVo(
                entity.getId(),
                entity.getTitle(),
                entity.getCategory(),
                entity.getAuthorUserId(),
                authorName,
                entity.getSolved() != null && entity.getSolved() == 1,
                replyCount,
                entity.getCreatedAt()
        );
    }

    public static TopicDetailVo toDetailVo(TopicEntity entity, String authorName, List<TopicReplyVo> replies) {
        return new TopicDetailVo(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getAuthorUserId(),
                authorName,
                entity.getSolved() != null && entity.getSolved() == 1,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                replies
        );
    }

    public static TopicReplyVo toReplyVo(TopicReplyEntity entity, String userName) {
        return new TopicReplyVo(
                entity.getId(),
                entity.getUserId(),
                userName,
                entity.getContent(),
                entity.getIsSolution() != null && entity.getIsSolution() == 1,
                entity.getCreatedAt()
        );
    }

    public static TopicEntity toEntity(TopicCreateRequest request, Long authorUserId) {
        TopicEntity entity = new TopicEntity();
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setCategory(request.category());
        entity.setAuthorUserId(authorUserId);
        entity.setSolved(0);
        return entity;
    }

    public static TopicReplyEntity toReplyEntity(TopicReplyCreateRequest request, Long topicId, Long userId) {
        TopicReplyEntity entity = new TopicReplyEntity();
        entity.setTopicId(topicId);
        entity.setUserId(userId);
        entity.setContent(request.content());
        entity.setIsSolution(0);
        return entity;
    }
}
