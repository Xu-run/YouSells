package com.yousells.modules.topic.service;

import com.yousells.common.response.PageResponse;
import com.yousells.modules.topic.dto.TopicCreateRequest;
import com.yousells.modules.topic.dto.TopicQueryRequest;
import com.yousells.modules.topic.dto.TopicReplyCreateRequest;
import com.yousells.modules.topic.vo.TopicDetailVo;
import com.yousells.modules.topic.vo.TopicListItemVo;

public interface TopicService {

    PageResponse<TopicListItemVo> pageTopics(TopicQueryRequest request);

    TopicDetailVo getTopicDetail(Long id);

    Long createTopic(TopicCreateRequest request);

    Long createReply(Long topicId, TopicReplyCreateRequest request);

    void markSolved(Long id);

    void markBestSolution(Long topicId, Long replyId);
}
