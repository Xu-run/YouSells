package com.yousells.modules.task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.constant.TaskStatusConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.task.dto.TaskCreateRequest;
import com.yousells.modules.task.dto.TaskQueryRequest;
import com.yousells.modules.task.dto.TaskUpdateRequest;
import com.yousells.modules.task.entity.TaskBoardEntity;
import com.yousells.modules.task.mapper.TaskBoardMapper;
import com.yousells.modules.task.service.impl.TaskBoardServiceImpl;
import com.yousells.modules.task.vo.TaskBoardColumnVo;
import com.yousells.modules.task.vo.TaskBoardItemVo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;

class TaskBoardServiceTest {

    private final TaskBoardMapper taskBoardMapper = mock(TaskBoardMapper.class);
    private final TaskBoardServiceImpl service = new TaskBoardServiceImpl(taskBoardMapper);

    private static final List<TaskBoardItemVo> SAMPLE_TASKS = List.of(
            new TaskBoardItemVo(301L, "整理高意向客户回访清单", "CUSTOMER", "IN_PROGRESS", "HIGH",
                    "秦梓源", "志明", LocalDateTime.of(2026, 5, 18, 20, 0), "今晚补齐名单"),
            new TaskBoardItemVo(302L, "补齐日报模板字段说明", "REPORT", "TODO", "MEDIUM",
                    "志明", null, LocalDateTime.of(2026, 5, 18, 22, 0), "写完后发群里确认"),
            new TaskBoardItemVo(303L, "整理话术库首版分类", "SCRIPT", "DONE", "MEDIUM",
                    "哲涛", "许润", LocalDateTime.of(2026, 5, 17, 18, 0), "已进入评审"),
            new TaskBoardItemVo(304L, "确认小镇Coders海报文案", "GROUP", "BLOCKED", "HIGH",
                    "嘉诚", "许润", LocalDateTime.of(2026, 5, 18, 16, 0), "等设计稿"),
            new TaskBoardItemVo(305L, "联调首页看板字段", "DASHBOARD", "TODO", "HIGH",
                    "秦梓源", "嘉诚", null, "等后端接口真实化")
    );

    @Test
    void shouldPageTasks() {
        Page<TaskBoardItemVo> pageResult = new Page<>(1, 20, 5);
        pageResult.setRecords(SAMPLE_TASKS);
        when(taskBoardMapper.selectPageWithOwner(any(), any()))
                .thenReturn(pageResult);

        PageResponse<TaskBoardItemVo> result = service.pageTasks(new TaskQueryRequest(1, 20, null));

        assertEquals(5, result.total());
        assertEquals(1, result.page());
        assertEquals(20, result.pageSize());
        assertEquals(5, result.list().size());
    }

    @Test
    void shouldPageTasksWithStatusFilter() {
        List<TaskBoardItemVo> todoTasks = SAMPLE_TASKS.stream()
                .filter(t -> "TODO".equals(t.status()))
                .toList();
        Page<TaskBoardItemVo> pageResult = new Page<>(1, 20, todoTasks.size());
        pageResult.setRecords(todoTasks);
        when(taskBoardMapper.selectPageWithOwner(any(), any()))
                .thenReturn(pageResult);

        PageResponse<TaskBoardItemVo> result = service.pageTasks(
                new TaskQueryRequest(1, 20, TaskStatusConstants.TODO));

        assertEquals(2, result.total());
        assertTrue(result.list().stream().allMatch(t -> "TODO".equals(t.status())));
    }

    @Test
    void shouldReturnEmptyPageForNoResults() {
        Page<TaskBoardItemVo> pageResult = new Page<>(1, 20, 0);
        pageResult.setRecords(List.of());
        when(taskBoardMapper.selectPageWithOwner(any(), any()))
                .thenReturn(pageResult);

        PageResponse<TaskBoardItemVo> result = service.pageTasks(new TaskQueryRequest(1, 20, null));

        assertEquals(0, result.total());
        assertTrue(result.list().isEmpty());
    }

    @Test
    void shouldUseDefaultPageParamsWhenInvalid() {
        Page<TaskBoardItemVo> pageResult = new Page<>(1, 20, 5);
        pageResult.setRecords(SAMPLE_TASKS);
        when(taskBoardMapper.selectPageWithOwner(any(), any()))
                .thenReturn(pageResult);

        service.pageTasks(new TaskQueryRequest(0, -1, null));

        verify(taskBoardMapper).selectPageWithOwner(argThat(p -> p.getCurrent() == 1 && p.getSize() == 20), any());
    }

    @Test
    void shouldListBoardGroupedByStatus() {
        when(taskBoardMapper.selectAllWithOwner())
                .thenReturn(SAMPLE_TASKS);

        List<TaskBoardColumnVo> board = service.listBoard();

        assertEquals(4, board.size());
        assertEquals("TODO", board.get(0).status());
        assertEquals("待开始", board.get(0).title());
        assertEquals(2, board.get(0).items().size());
        assertEquals("IN_PROGRESS", board.get(1).status());
        assertEquals("进行中", board.get(1).title());
        assertEquals(1, board.get(1).items().size());
        assertEquals("BLOCKED", board.get(2).status());
        assertEquals("阻塞中", board.get(2).title());
        assertEquals(1, board.get(2).items().size());
        assertEquals("DONE", board.get(3).status());
        assertEquals("已完成", board.get(3).title());
        assertEquals(1, board.get(3).items().size());
    }

    @Test
    void shouldListBoardWithEmptyColumnsWhenNoTasks() {
        when(taskBoardMapper.selectAllWithOwner())
                .thenReturn(List.of());

        List<TaskBoardColumnVo> board = service.listBoard();

        assertEquals(4, board.size());
        assertTrue(board.stream().allMatch(col -> col.items().isEmpty()));
    }

    @Test
    void shouldCreateTaskWithDefaultValues() {
        TaskCreateRequest request = new TaskCreateRequest(
                "新任务", "CUSTOMER", "描述内容", null, 1L, null, null
        );
        when(taskBoardMapper.insert(Mockito.<TaskBoardEntity>any())).thenAnswer(invocation -> {
            TaskBoardEntity entity = invocation.getArgument(0);
            entity.setId(100L);
            return 1;
        });

        Long id = service.createTask(request);

        assertEquals(100L, id);
        verify(taskBoardMapper).insert(Mockito.<TaskBoardEntity>argThat(entity ->
                "新任务".equals(entity.getTaskTitle())
                        && TaskStatusConstants.TODO.equals(entity.getStatus())
                        && "MEDIUM".equals(entity.getPriority())
                        && entity.getOwnerUserId().equals(1L)
        ));
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        TaskBoardEntity existing = new TaskBoardEntity();
        existing.setId(301L);
        existing.setTaskTitle("旧标题");
        existing.setStatus("TODO");
        existing.setPriority("LOW");
        existing.setOwnerUserId(1L);

        when(taskBoardMapper.selectById(301L)).thenReturn(existing);
        when(taskBoardMapper.updateById(Mockito.<TaskBoardEntity>any())).thenReturn(1);

        TaskUpdateRequest request = new TaskUpdateRequest(
                "新标题", "IN_PROGRESS", "新描述", "HIGH", 2L, 3L,
                LocalDateTime.of(2026, 5, 25, 12, 0), "下一步"
        );
        service.updateTask(301L, request);

        verify(taskBoardMapper).updateById(Mockito.<TaskBoardEntity>argThat(entity ->
                "新标题".equals(entity.getTaskTitle())
                        && "IN_PROGRESS".equals(entity.getStatus())
                        && "HIGH".equals(entity.getPriority())
                        && entity.getOwnerUserId().equals(2L)
                        && entity.getAssistantUserId().equals(3L)
                        && "下一步".equals(entity.getNextAction())
        ));
    }

    @Test
    void shouldThrowNotFoundWhenUpdateMissingTask() {
        when(taskBoardMapper.selectById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.updateTask(999L, new TaskUpdateRequest(
                        "标题", "TODO", null, null, 1L, null, null, null
                )));

        assertEquals(ErrorCodeConstants.NOT_FOUND, exception.getCode());
        assertTrue(exception.getMessage().contains("task not found"));
    }
}
