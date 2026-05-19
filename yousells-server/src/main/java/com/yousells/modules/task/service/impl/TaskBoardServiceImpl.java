package com.yousells.modules.task.service.impl;

import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.task.dto.TaskCreateRequest;
import com.yousells.modules.task.dto.TaskQueryRequest;
import com.yousells.modules.task.dto.TaskUpdateRequest;
import com.yousells.modules.task.service.TaskBoardService;
import com.yousells.modules.task.vo.TaskBoardColumnVo;
import com.yousells.modules.task.vo.TaskBoardItemVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskBoardServiceImpl implements TaskBoardService {

    private final List<TaskBoardItemVo> sampleTasks = List.of(
            new TaskBoardItemVo(301L, "整理高意向客户回访清单", "CUSTOMER", "IN_PROGRESS", "HIGH", "秦梓源", "志明", LocalDateTime.of(2026, 5, 18, 20, 0), "今晚补齐名单"),
            new TaskBoardItemVo(302L, "补齐日报模板字段说明", "REPORT", "TODO", "MEDIUM", "志明", null, LocalDateTime.of(2026, 5, 18, 22, 0), "写完后发群里确认"),
            new TaskBoardItemVo(303L, "整理话术库首版分类", "SCRIPT", "DONE", "MEDIUM", "哲涛", "许润", LocalDateTime.of(2026, 5, 17, 18, 0), "已进入评审")
    );

    @Override
    public PageResponse<TaskBoardItemVo> pageTasks(TaskQueryRequest request) {
        int page = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 20 : request.pageSize();
        List<TaskBoardItemVo> filtered = sampleTasks.stream()
                .filter(item -> request.status() == null || request.status().isBlank() || request.status().equals(item.status()))
                .toList();
        int fromIndex = Math.min((page - 1) * pageSize, filtered.size());
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        return PageResponse.of(filtered.subList(fromIndex, toIndex), page, pageSize, filtered.size());
    }

    @Override
    public List<TaskBoardColumnVo> listBoard() {
        return List.of(
                new TaskBoardColumnVo("TODO", "待开始", sampleTasks.stream().filter(item -> "TODO".equals(item.status())).toList()),
                new TaskBoardColumnVo("IN_PROGRESS", "进行中", sampleTasks.stream().filter(item -> "IN_PROGRESS".equals(item.status())).toList()),
                new TaskBoardColumnVo("DONE", "已完成", sampleTasks.stream().filter(item -> "DONE".equals(item.status())).toList())
        );
    }

    @Override
    public Long createTask(TaskCreateRequest request) {
        return 9301L;
    }

    @Override
    public void updateTask(Long id, TaskUpdateRequest request) {
        boolean exists = sampleTasks.stream().anyMatch(item -> item.id().equals(id));
        if (!exists) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "task not found");
        }
    }
}
