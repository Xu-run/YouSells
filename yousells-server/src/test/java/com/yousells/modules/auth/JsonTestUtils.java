package com.yousells.modules.auth;

import com.jayway.jsonpath.JsonPath;

public final class JsonTestUtils {

    private JsonTestUtils() {
    }

    public static String readJsonPath(String json, String path) {
        return JsonPath.read(json, path);
    }
}
