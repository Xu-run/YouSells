package com.yousells.modules.auth;

import com.jayway.jsonpath.JsonPath;

public final class JsonTestUtils {

    private JsonTestUtils() {
    }

    public static String readJsonPath(String json, String path) {
        return JsonPath.read(json, path);
    }

    public static Long readJsonPathAsLong(String json, String path) {
        Object value = JsonPath.read(json, path);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }
}
