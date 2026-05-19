package com.yousells.modules.script.service;

import com.yousells.common.response.PageResponse;
import com.yousells.modules.script.dto.ScriptCreateRequest;
import com.yousells.modules.script.dto.ScriptQueryRequest;
import com.yousells.modules.script.dto.ScriptUpdateRequest;
import com.yousells.modules.script.vo.ScriptCategoryVo;
import com.yousells.modules.script.vo.ScriptVo;

import java.util.List;

public interface ScriptLibraryService {

    List<ScriptCategoryVo> listCategories();

    PageResponse<ScriptVo> pageScripts(ScriptQueryRequest request);

    ScriptVo getScript(Long id);

    Long createScript(ScriptCreateRequest request);

    void updateScript(Long id, ScriptUpdateRequest request);
}
