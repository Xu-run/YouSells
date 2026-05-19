package com.yousells.modules.followup.service;

import com.yousells.common.response.PageResponse;
import com.yousells.modules.followup.dto.FollowUpCreateRequest;
import com.yousells.modules.followup.dto.FollowUpQueryRequest;
import com.yousells.modules.followup.vo.FollowUpVo;

public interface FollowUpService {

    PageResponse<FollowUpVo> pageFollowUps(FollowUpQueryRequest request);

    Long createFollowUp(FollowUpCreateRequest request);
}
