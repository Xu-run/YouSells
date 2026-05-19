package com.yousells.modules.script.service.impl;

import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.script.dto.ScriptCreateRequest;
import com.yousells.modules.script.dto.ScriptQueryRequest;
import com.yousells.modules.script.dto.ScriptUpdateRequest;
import com.yousells.modules.script.service.ScriptLibraryService;
import com.yousells.modules.script.vo.ScriptCategoryVo;
import com.yousells.modules.script.vo.ScriptVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScriptLibraryServiceImpl implements ScriptLibraryService {

    private final List<ScriptCategoryVo> categories = List.of(
            new ScriptCategoryVo(1L, "FIRST_ADD", "初次加好友", 1),
            new ScriptCategoryVo(2L, "GROUP_INVITE", "邀请进群", 2),
            new ScriptCategoryVo(3L, "HIGH_INTENT", "高意向推进", 3)
    );

    private final List<ScriptVo> scripts = List.of(
            new ScriptVo(9001L, 1L, "初次加好友", "加好友开场 01", "刚加上好友的第一轮寒暄", "ENABLED", "你好，我是学长，这段时间在整理技术方向资料，看到你也对编程感兴趣，就想和你先认识一下。", LocalDateTime.of(2026, 5, 18, 12, 0)),
            new ScriptVo(9002L, 2L, "邀请进群", "技术交流群邀请", "已经聊过 1-2 轮之后", "ENABLED", "我们这边在做一个小而精的技术交流群，平时会发项目路线、比赛信息和小活动，如果你愿意我拉你进去。", LocalDateTime.of(2026, 5, 18, 14, 0)),
            new ScriptVo(9003L, 3L, "高意向推进", "体验路径推进", "高意向客户进一步沟通", "ENABLED", "如果你愿意，我可以先给你安排一个很轻量的体验路径，你先感受一下我们的教学风格和项目节奏。", LocalDateTime.of(2026, 5, 18, 16, 0))
    );

    @Override
    public List<ScriptCategoryVo> listCategories() {
        return categories;
    }

    @Override
    public PageResponse<ScriptVo> pageScripts(ScriptQueryRequest request) {
        int page = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 20 : request.pageSize();
        List<ScriptVo> filtered = scripts.stream()
                .filter(script -> request.categoryId() == null || request.categoryId().equals(script.categoryId()))
                .filter(script -> request.keyword() == null || request.keyword().isBlank()
                        || script.title().contains(request.keyword())
                        || script.content().contains(request.keyword()))
                .toList();
        int fromIndex = Math.min((page - 1) * pageSize, filtered.size());
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        return PageResponse.of(filtered.subList(fromIndex, toIndex), page, pageSize, filtered.size());
    }

    @Override
    public ScriptVo getScript(Long id) {
        return scripts.stream()
                .filter(script -> script.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCodeConstants.NOT_FOUND, "script not found"));
    }

    @Override
    public Long createScript(ScriptCreateRequest request) {
        return 9101L;
    }

    @Override
    public void updateScript(Long id, ScriptUpdateRequest request) {
        getScript(id);
    }
}
