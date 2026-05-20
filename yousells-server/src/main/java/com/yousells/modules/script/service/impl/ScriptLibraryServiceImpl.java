package com.yousells.modules.script.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.script.convert.ScriptConvert;
import com.yousells.modules.script.dto.ScriptCreateRequest;
import com.yousells.modules.script.dto.ScriptQueryRequest;
import com.yousells.modules.script.dto.ScriptUpdateRequest;
import com.yousells.modules.script.entity.ScriptCategoryEntity;
import com.yousells.modules.script.entity.ScriptEntity;
import com.yousells.modules.script.mapper.ScriptCategoryMapper;
import com.yousells.modules.script.mapper.ScriptMapper;
import com.yousells.modules.script.service.ScriptLibraryService;
import com.yousells.modules.script.vo.ScriptCategoryVo;
import com.yousells.modules.script.vo.ScriptVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScriptLibraryServiceImpl implements ScriptLibraryService {

    private final ScriptCategoryMapper scriptCategoryMapper;
    private final ScriptMapper scriptMapper;

    public ScriptLibraryServiceImpl(ScriptCategoryMapper scriptCategoryMapper,
                                    ScriptMapper scriptMapper) {
        this.scriptCategoryMapper = scriptCategoryMapper;
        this.scriptMapper = scriptMapper;
    }

    @Override
    public List<ScriptCategoryVo> listCategories() {
        List<ScriptCategoryEntity> entities = scriptCategoryMapper.selectList(
                new LambdaQueryWrapper<ScriptCategoryEntity>()
                        .eq(ScriptCategoryEntity::getIsDeleted, 0)
                        .orderByAsc(ScriptCategoryEntity::getSortOrder));
        return entities.stream()
                .map(e -> new ScriptCategoryVo(e.getId(), e.getCategoryCode(),
                        e.getCategoryName(), e.getSortOrder()))
                .toList();
    }

    @Override
    public PageResponse<ScriptVo> pageScripts(ScriptQueryRequest request) {
        int page = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 20 : request.pageSize();
        IPage<ScriptVo> result = scriptMapper.selectPageWithCategory(
                Page.of(page, pageSize), request.categoryId(), request.keyword());
        return PageResponse.of(result.getRecords(), page, pageSize, result.getTotal());
    }

    @Override
    public ScriptVo getScript(Long id) {
        ScriptEntity entity = scriptMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "话术不存在");
        }
        ScriptCategoryEntity category = scriptCategoryMapper.selectById(entity.getCategoryId());
        String categoryName = category != null ? category.getCategoryName() : "未知分类";
        return new ScriptVo(
                entity.getId(),
                entity.getCategoryId(),
                categoryName,
                entity.getTitle(),
                entity.getApplicableScene(),
                entity.getStatus(),
                entity.getContent(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public Long createScript(ScriptCreateRequest request) {
        ScriptEntity entity = ScriptConvert.toEntity(request);
        scriptMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateScript(Long id, ScriptUpdateRequest request) {
        ScriptEntity entity = scriptMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "话术不存在");
        }
        ScriptConvert.applyUpdate(entity, request);
        scriptMapper.updateById(entity);
    }
}
