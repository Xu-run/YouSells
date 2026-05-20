package com.yousells.modules.script;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.script.dto.ScriptCreateRequest;
import com.yousells.modules.script.dto.ScriptQueryRequest;
import com.yousells.modules.script.dto.ScriptUpdateRequest;
import com.yousells.modules.script.entity.ScriptCategoryEntity;
import com.yousells.modules.script.entity.ScriptEntity;
import com.yousells.modules.script.mapper.ScriptCategoryMapper;
import com.yousells.modules.script.mapper.ScriptMapper;
import com.yousells.modules.script.service.impl.ScriptLibraryServiceImpl;
import com.yousells.modules.script.vo.ScriptCategoryVo;
import com.yousells.modules.script.vo.ScriptVo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScriptServiceTest {

    private final ScriptCategoryMapper scriptCategoryMapper = mock(ScriptCategoryMapper.class);
    private final ScriptMapper scriptMapper = mock(ScriptMapper.class);
    private final ScriptLibraryServiceImpl service = new ScriptLibraryServiceImpl(scriptCategoryMapper, scriptMapper);

    private static final List<ScriptCategoryEntity> SAMPLE_CATEGORIES = List.of(
            buildCategory(1L, "FIRST_ADD", "初次加好友", 1),
            buildCategory(2L, "GROUP_INVITE", "邀请进群", 2),
            buildCategory(3L, "HIGH_INTENT", "高意向推进", 3)
    );

    private static final List<ScriptVo> SAMPLE_SCRIPTS = List.of(
            new ScriptVo(9001L, 1L, "初次加好友", "开场话术01", "刚加好友", "ENABLED",
                    "你好，我是学长...", LocalDateTime.of(2026, 5, 18, 12, 0)),
            new ScriptVo(9002L, 2L, "邀请进群", "邀请话术01", "已聊过", "ENABLED",
                    "我们这边在做技术交流群...", LocalDateTime.of(2026, 5, 18, 14, 0))
    );

    private static ScriptCategoryEntity buildCategory(Long id, String code, String name, int sortOrder) {
        ScriptCategoryEntity entity = new ScriptCategoryEntity();
        entity.setId(id);
        entity.setCategoryCode(code);
        entity.setCategoryName(name);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    private static ScriptEntity buildScriptEntity(Long id, Long categoryId, String title) {
        ScriptEntity entity = new ScriptEntity();
        entity.setId(id);
        entity.setCategoryId(categoryId);
        entity.setTitle(title);
        entity.setContent("话术内容");
        entity.setApplicableScene("场景");
        entity.setStatus("ENABLED");
        entity.setUpdatedAt(LocalDateTime.of(2026, 5, 20, 10, 0));
        return entity;
    }

    @Test
    void shouldListCategories() {
        when(scriptCategoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(SAMPLE_CATEGORIES);

        List<ScriptCategoryVo> result = service.listCategories();

        assertEquals(3, result.size());
        assertEquals("FIRST_ADD", result.get(0).categoryCode());
        assertEquals("初次加好友", result.get(0).categoryName());
        assertEquals(1, result.get(0).sortOrder());
        assertEquals("HIGH_INTENT", result.get(2).categoryCode());
    }

    @Test
    void shouldReturnEmptyCategories() {
        when(scriptCategoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of());

        List<ScriptCategoryVo> result = service.listCategories();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldPageScripts() {
        Page<ScriptVo> pageResult = new Page<>(1, 20, 2);
        pageResult.setRecords(SAMPLE_SCRIPTS);
        when(scriptMapper.selectPageWithCategory(any(), eq(null), eq(null)))
                .thenReturn(pageResult);

        PageResponse<ScriptVo> result = service.pageScripts(new ScriptQueryRequest(null, null, null, null));

        assertEquals(2, result.total());
        assertEquals(2, result.list().size());
    }

    @Test
    void shouldPageScriptsWithFilters() {
        Page<ScriptVo> pageResult = new Page<>(1, 20, 1);
        pageResult.setRecords(SAMPLE_SCRIPTS.subList(0, 1));
        when(scriptMapper.selectPageWithCategory(any(), eq(1L), eq("开场")))
                .thenReturn(pageResult);

        PageResponse<ScriptVo> result = service.pageScripts(
                new ScriptQueryRequest(1, 20, 1L, "开场"));

        assertEquals(1, result.total());
        assertEquals(1, result.list().size());
        assertEquals(1L, result.list().get(0).categoryId());
    }

    @Test
    void shouldReturnEmptyPageWhenNoMatch() {
        Page<ScriptVo> pageResult = new Page<>(1, 20, 0);
        pageResult.setRecords(List.of());
        when(scriptMapper.selectPageWithCategory(any(), any(), any()))
                .thenReturn(pageResult);

        PageResponse<ScriptVo> result = service.pageScripts(
                new ScriptQueryRequest(1, 20, 999L, "nosuch"));

        assertEquals(0, result.total());
        assertTrue(result.list().isEmpty());
    }

    @Test
    void shouldGetScript() {
        ScriptEntity entity = buildScriptEntity(100L, 2L, "测试话术");
        ScriptCategoryEntity category = SAMPLE_CATEGORIES.get(1);
        when(scriptMapper.selectById(100L)).thenReturn(entity);
        when(scriptCategoryMapper.selectById(2L)).thenReturn(category);

        ScriptVo result = service.getScript(100L);

        assertEquals(100L, result.id());
        assertEquals("测试话术", result.title());
        assertEquals("邀请进群", result.categoryName());
    }

    @Test
    void shouldThrowNotFoundWhenGetScriptMissing() {
        when(scriptMapper.selectById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.getScript(999L));

        assertEquals(ErrorCodeConstants.NOT_FOUND, exception.getCode());
        assertTrue(exception.getMessage().contains("话术"));
    }

    @Test
    void shouldCreateScript() {
        ScriptCreateRequest request = new ScriptCreateRequest(1L, "新话术", "内容内容",
                "新场景", null);
        when(scriptMapper.insert(Mockito.<ScriptEntity>any())).thenAnswer(invocation -> {
            ScriptEntity entity = invocation.getArgument(0);
            entity.setId(200L);
            return 1;
        });

        Long id = service.createScript(request);

        assertEquals(200L, id);
        verify(scriptMapper).insert(Mockito.<ScriptEntity>argThat(entity ->
                entity.getCategoryId().equals(1L)
                        && "新话术".equals(entity.getTitle())
                        && "ENABLED".equals(entity.getStatus())
        ));
    }

    @Test
    void shouldUpdateScript() {
        ScriptEntity entity = buildScriptEntity(300L, 1L, "旧标题");
        when(scriptMapper.selectById(300L)).thenReturn(entity);

        ScriptUpdateRequest request = new ScriptUpdateRequest(2L, "新标题", "新内容",
                "新场景", "DISABLED");
        service.updateScript(300L, request);

        assertEquals(2L, entity.getCategoryId());
        assertEquals("新标题", entity.getTitle());
        assertEquals("DISABLED", entity.getStatus());
        verify(scriptMapper).updateById(entity);
    }

    @Test
    void shouldThrowNotFoundWhenUpdateScriptMissing() {
        when(scriptMapper.selectById(999L)).thenReturn(null);

        ScriptUpdateRequest request = new ScriptUpdateRequest(1L, "标题", "内容",
                "场景", "ENABLED");
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.updateScript(999L, request));

        assertEquals(ErrorCodeConstants.NOT_FOUND, exception.getCode());
    }
}
