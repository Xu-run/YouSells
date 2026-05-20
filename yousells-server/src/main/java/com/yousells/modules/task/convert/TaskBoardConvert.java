package com.yousells.modules.task.convert;

import com.yousells.common.constant.TaskStatusConstants;
import com.yousells.modules.task.dto.TaskCreateRequest;
import com.yousells.modules.task.dto.TaskUpdateRequest;
import com.yousells.modules.task.entity.TaskBoardEntity;

public final class TaskBoardConvert {

    private TaskBoardConvert() {
    }

    public static TaskBoardEntity toEntity(TaskCreateRequest request) {
        TaskBoardEntity entity = new TaskBoardEntity();
        entity.setTaskTitle(request.taskTitle());
        entity.setTaskType(request.taskType());
        entity.setTaskDescription(request.taskDescription());
        entity.setPriority(request.priority() != null ? request.priority() : "MEDIUM");
        entity.setStatus(TaskStatusConstants.TODO);
        entity.setOwnerUserId(request.ownerUserId());
        entity.setAssistantUserId(request.assistantUserId());
        entity.setDueAt(request.dueAt());
        return entity;
    }

    public static void applyUpdate(TaskBoardEntity entity, TaskUpdateRequest request) {
        entity.setTaskTitle(request.taskTitle());
        entity.setStatus(request.status());
        if (request.taskDescription() != null) {
            entity.setTaskDescription(request.taskDescription());
        }
        if (request.priority() != null) {
            entity.setPriority(request.priority());
        }
        entity.setOwnerUserId(request.ownerUserId());
        entity.setAssistantUserId(request.assistantUserId());
        entity.setDueAt(request.dueAt());
        if (request.nextAction() != null) {
            entity.setNextAction(request.nextAction());
        }
    }
}
