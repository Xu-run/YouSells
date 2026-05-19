package com.yousells.modules.task.service;

import com.yousells.common.response.PageResponse;
import com.yousells.modules.task.dto.TaskCreateRequest;
import com.yousells.modules.task.dto.TaskQueryRequest;
import com.yousells.modules.task.dto.TaskUpdateRequest;
import com.yousells.modules.task.vo.TaskBoardColumnVo;
import com.yousells.modules.task.vo.TaskBoardItemVo;

import java.util.List;

public interface TaskBoardService {

    PageResponse<TaskBoardItemVo> pageTasks(TaskQueryRequest request);

    List<TaskBoardColumnVo> listBoard();

    Long createTask(TaskCreateRequest request);

    void updateTask(Long id, TaskUpdateRequest request);
}
