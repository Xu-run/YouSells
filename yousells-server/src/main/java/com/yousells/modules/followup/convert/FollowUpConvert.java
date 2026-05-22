package com.yousells.modules.followup.convert;

import com.yousells.modules.followup.dto.FollowUpCreateRequest;
import com.yousells.modules.followup.entity.FollowUpEntity;
import com.yousells.modules.followup.vo.FollowUpVo;

public class FollowUpConvert {

    private FollowUpConvert() {
    }

    public static FollowUpVo toVo(FollowUpEntity entity, String userDisplayName) {
        return new FollowUpVo(
                entity.getId(),
                entity.getCustomerId(),
                entity.getUserId(),
                userDisplayName,
                entity.getProgress(),
                entity.getContent(),
                entity.getFeedback(),
                entity.getNextAction(),
                entity.getCreatedAt()
        );
    }

    public static FollowUpEntity toEntity(FollowUpCreateRequest request, Long userId) {
        FollowUpEntity entity = new FollowUpEntity();
        entity.setCustomerId(request.customerId());
        entity.setUserId(userId);
        entity.setProgress(request.progress());
        entity.setContent(request.content());
        entity.setFeedback(request.feedback());
        entity.setNextAction(request.nextAction());
        return entity;
    }
}
