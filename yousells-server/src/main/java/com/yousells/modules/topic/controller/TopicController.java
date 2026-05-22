package com.yousells.modules.topic.controller;

import com.yousells.common.response.ApiResponse;
import com.yousells.common.response.IdResponse;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.topic.dto.TopicCreateRequest;
import com.yousells.modules.topic.dto.TopicQueryRequest;
import com.yousells.modules.topic.dto.TopicReplyCreateRequest;
import com.yousells.modules.topic.service.TopicService;
import com.yousells.modules.topic.vo.TopicDetailVo;
import com.yousells.modules.topic.vo.TopicListItemVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ApiResponse<PageResponse<TopicListItemVo>> page(TopicQueryRequest request) {
        return ApiResponse.success(topicService.pageTopics(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<TopicDetailVo> detail(@PathVariable Long id) {
        return ApiResponse.success(topicService.getTopicDetail(id));
    }

    @PostMapping
    public ApiResponse<IdResponse> create(@Valid @RequestBody TopicCreateRequest request) {
        return ApiResponse.success(new IdResponse(topicService.createTopic(request)));
    }

    @PostMapping("/{id}/replies")
    public ApiResponse<IdResponse> createReply(@PathVariable Long id,
                                               @Valid @RequestBody TopicReplyCreateRequest request) {
        return ApiResponse.success(new IdResponse(topicService.createReply(id, request)));
    }

    @PatchMapping("/{id}/solved")
    public ApiResponse<Void> markSolved(@PathVariable Long id) {
        topicService.markSolved(id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/replies/{replyId}/solution")
    public ApiResponse<Void> markBestSolution(@PathVariable Long id,
                                              @PathVariable Long replyId) {
        topicService.markBestSolution(id, replyId);
        return ApiResponse.success();
    }
}
