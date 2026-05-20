package com.yousells.modules.script.convert;

import com.yousells.modules.script.dto.ScriptCreateRequest;
import com.yousells.modules.script.dto.ScriptUpdateRequest;
import com.yousells.modules.script.entity.ScriptEntity;

public final class ScriptConvert {

    private ScriptConvert() {
    }

    public static ScriptEntity toEntity(ScriptCreateRequest request) {
        ScriptEntity entity = new ScriptEntity();
        entity.setCategoryId(request.categoryId());
        entity.setTitle(request.title());
        entity.setContent(request.content());
        entity.setApplicableScene(request.applicableScene());
        entity.setStatus(request.status() == null ? "ENABLED" : request.status());
        return entity;
    }

    public static void applyUpdate(ScriptEntity entity, ScriptUpdateRequest request) {
        entity.setCategoryId(request.categoryId());
        entity.setTitle(request.title());
        entity.setContent(request.content());
        entity.setApplicableScene(request.applicableScene());
        entity.setStatus(request.status() == null ? "ENABLED" : request.status());
    }
}
