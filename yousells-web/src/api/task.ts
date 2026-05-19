import request from "@/utils/request";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { TaskBoardColumn, TaskBoardItem } from "@/types/task";

export async function fetchTasks(params: { page?: number; pageSize?: number; status?: string } = {}): Promise<PageResponse<TaskBoardItem>> {
  const response = await request.get<ApiResponse<PageResponse<TaskBoardItem>>>("/api/tasks", { params });
  return response.data.data;
}

export async function fetchTaskBoard(): Promise<TaskBoardColumn[]> {
  const response = await request.get<ApiResponse<TaskBoardColumn[]>>("/api/tasks/board");
  return response.data.data;
}
