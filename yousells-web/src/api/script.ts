import request from "@/utils/request";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { ScriptCategory, ScriptItem } from "@/types/script";

export async function fetchScriptCategories(): Promise<ScriptCategory[]> {
  const response = await request.get<ApiResponse<ScriptCategory[]>>("/api/scripts/categories");
  return response.data.data;
}

export async function fetchScripts(page = 1, pageSize = 20): Promise<PageResponse<ScriptItem>> {
  const response = await request.get<ApiResponse<PageResponse<ScriptItem>>>("/api/scripts", {
    params: { page, pageSize }
  });
  return response.data.data;
}
