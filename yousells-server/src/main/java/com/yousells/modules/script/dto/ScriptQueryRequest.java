package com.yousells.modules.script.dto;

public record ScriptQueryRequest(
        Integer page,
        Integer pageSize,
        Long categoryId,
        String keyword
) {
}
