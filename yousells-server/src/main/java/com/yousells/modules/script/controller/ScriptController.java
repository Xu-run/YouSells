package com.yousells.modules.script.controller;

import com.yousells.common.response.ApiResponse;
import com.yousells.common.response.IdResponse;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.script.dto.ScriptCreateRequest;
import com.yousells.modules.script.dto.ScriptQueryRequest;
import com.yousells.modules.script.dto.ScriptUpdateRequest;
import com.yousells.modules.script.service.ScriptLibraryService;
import com.yousells.modules.script.vo.ScriptCategoryVo;
import com.yousells.modules.script.vo.ScriptVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    private final ScriptLibraryService scriptLibraryService;

    public ScriptController(ScriptLibraryService scriptLibraryService) {
        this.scriptLibraryService = scriptLibraryService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<ScriptCategoryVo>> categories() {
        return ApiResponse.success(scriptLibraryService.listCategories());
    }

    @GetMapping
    public ApiResponse<PageResponse<ScriptVo>> page(ScriptQueryRequest request) {
        return ApiResponse.success(scriptLibraryService.pageScripts(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ScriptVo> detail(@PathVariable Long id) {
        return ApiResponse.success(scriptLibraryService.getScript(id));
    }

    @PostMapping
    public ApiResponse<IdResponse> create(@Valid @RequestBody ScriptCreateRequest request) {
        return ApiResponse.success(new IdResponse(scriptLibraryService.createScript(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ScriptUpdateRequest request) {
        scriptLibraryService.updateScript(id, request);
        return ApiResponse.success();
    }
}
