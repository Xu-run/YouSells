package com.yousells.modules.followup.service.impl;

import com.yousells.common.response.PageResponse;
import com.yousells.modules.followup.dto.FollowUpCreateRequest;
import com.yousells.modules.followup.dto.FollowUpQueryRequest;
import com.yousells.modules.followup.service.FollowUpService;
import com.yousells.modules.followup.vo.FollowUpVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FollowUpServiceImpl implements FollowUpService {

    private final List<FollowUpVo> sampleFollowUps = List.of(
            new FollowUpVo(5001L, 1001L, "CHAT", "聊了学习路线和项目训练", "愿意继续了解", "担心时间安排", "发项目路线图", LocalDateTime.of(2026, 5, 19, 12, 0), "秦梓源", "秦梓源", LocalDateTime.of(2026, 5, 17, 21, 30)),
            new FollowUpVo(5002L, 1001L, "GROUP", "拉进技术交流群并介绍群文件", "对群里的项目分享感兴趣", "担心基础弱", "安排一次简单体验", LocalDateTime.of(2026, 5, 20, 19, 0), "志明", "秦梓源", LocalDateTime.of(2026, 5, 18, 10, 10)),
            new FollowUpVo(5003L, 1002L, "CHAT", "发了课程结构和增值服务", "想再看一些学长案例", "价格", "补案例截图", LocalDateTime.of(2026, 5, 20, 20, 0), "志明", "志明", LocalDateTime.of(2026, 5, 18, 20, 30))
    );

    @Override
    public PageResponse<FollowUpVo> pageFollowUps(FollowUpQueryRequest request) {
        int page = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 20 : request.pageSize();
        List<FollowUpVo> filtered = sampleFollowUps.stream()
                .filter(item -> request.customerId() == null || request.customerId().equals(item.customerId()))
                .toList();
        int fromIndex = Math.min((page - 1) * pageSize, filtered.size());
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        return PageResponse.of(filtered.subList(fromIndex, toIndex), page, pageSize, filtered.size());
    }

    @Override
    public Long createFollowUp(FollowUpCreateRequest request) {
        return 8001L;
    }
}
