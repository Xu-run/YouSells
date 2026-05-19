package com.yousells.modules.followup.controller;

import com.yousells.common.response.ApiResponse;
import com.yousells.common.response.IdResponse;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.followup.dto.FollowUpCreateRequest;
import com.yousells.modules.followup.dto.FollowUpQueryRequest;
import com.yousells.modules.followup.service.FollowUpService;
import com.yousells.modules.followup.vo.FollowUpVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpController {

    private final FollowUpService followUpService;

    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    @GetMapping
    public ApiResponse<PageResponse<FollowUpVo>> page(FollowUpQueryRequest request) {
        return ApiResponse.success(followUpService.pageFollowUps(request));
    }

    @PostMapping
    public ApiResponse<IdResponse> create(@Valid @RequestBody FollowUpCreateRequest request) {
        return ApiResponse.success(new IdResponse(followUpService.createFollowUp(request)));
    }
}
