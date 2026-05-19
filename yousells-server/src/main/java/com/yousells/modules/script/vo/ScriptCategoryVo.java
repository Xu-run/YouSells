package com.yousells.modules.script.vo;

public record ScriptCategoryVo(
        Long id,
        String categoryCode,
        String categoryName,
        int sortOrder
) {
}
