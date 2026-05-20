package com.yousells.modules.task.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.constant.TaskStatusConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.task.convert.TaskBoardConvert;
import com.yousells.modules.task.dto.TaskCreateRequest;
import com.yousells.modules.task.dto.TaskQueryRequest;
import com.yousells.modules.task.dto.TaskUpdateRequest;
import com.yousells.modules.task.entity.TaskBoardEntity;
import com.yousells.modules.task.mapper.TaskBoardMapper;
import com.yousells.modules.task.service.TaskBoardService;
import com.yousells.modules.task.vo.TaskBoardColumnVo;
import com.yousells.modules.task.vo.TaskBoardItemVo;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskBoardServiceImpl implements TaskBoardService {

    private static final Map<String, String> STATUS_TITLE_MAP = new LinkedHashMap<>();

    static {
        STATUS_TITLE_MAP.put(TaskStatusConstants.TODO, "待开始");
        STATUS_TITLE_MAP.put(TaskStatusConstants.IN_PROGRESS, "进行中");
        STATUS_TITLE_MAP.put(TaskStatusConstants.BLOCKED, "阻塞中");
        STATUS_TITLE_MAP.put(TaskStatusConstants.DONE, "已完成");
    }

    private final TaskBoardMapper taskBoardMapper;

    public TaskBoardServiceImpl(TaskBoardMapper taskBoardMapper) {
        this.taskBoardMapper = taskBoardMapper;
    }

    @Override
    public PageResponse<TaskBoardItemVo> pageTasks(TaskQueryRequest request) {
        int page = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 20 : request.pageSize();
        IPage<TaskBoardItemVo> result = taskBoardMapper.selectPageWithOwner(
                Page.of(page, pageSize),
                request.status()
        );
        return PageResponse.of(result.getRecords(), page, pageSize, result.getTotal());
    }

    @Override
    public List<TaskBoardColumnVo> listBoard() {
        List<TaskBoardItemVo> allTasks = taskBoardMapper.selectAllWithOwner();

        return STATUS_TITLE_MAP.entrySet().stream()
                .map(entry -> {
                    List<TaskBoardItemVo> items = allTasks.stream()
                            .filter(task -> entry.getKey().equals(task.status()))
                            .toList();
                    return new TaskBoardColumnVo(entry.getKey(), entry.getValue(), items);
                })
                .toList();
    }

    @Override
    public Long createTask(TaskCreateRequest request) {
        TaskBoardEntity entity = TaskBoardConvert.toEntity(request);
        taskBoardMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateTask(Long id, TaskUpdateRequest request) {
        TaskBoardEntity entity = taskBoardMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "task not found");
        }
        TaskBoardConvert.applyUpdate(entity, request);
        taskBoardMapper.updateById(entity);
    }
}
