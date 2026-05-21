import request from "@/utils/request";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { TopicListItem, TopicDetail, TopicQuery } from "@/types/topic";

export async function fetchTopics(params: TopicQuery): Promise<PageResponse<TopicListItem>> {
  const response = await request.get<ApiResponse<PageResponse<TopicListItem>>>("/topics", { params });
  return response.data.data;
}

export async function fetchTopicDetail(id: number | string): Promise<TopicDetail> {
  const response = await request.get<ApiResponse<TopicDetail>>(`/topics/${id}`);
  return response.data.data;
}
