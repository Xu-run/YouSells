package com.yousells.common.response;

import java.util.List;

public record PageResponse<T>(List<T> list, long page, long pageSize, long total) {

    public static <T> PageResponse<T> of(List<T> list, long page, long pageSize, long total) {
        return new PageResponse<>(list, page, pageSize, total);
    }
}
