import request from "@/utils/request";
import type { ApiResponse, PageResponse, IdResponse } from "@/types/api";
import type {
  TopicListItem,
  TopicDetail,
  TopicQuery,
  TopicCreateRequest,
  TopicReplyCreateRequest
} from "@/types/topic";

export async function fetchTopics(
  params: TopicQuery
): Promise<PageResponse<TopicListItem>> {
  const response = await request.get<ApiResponse<PageResponse<TopicListItem>>>("/topics", {
    params
  });
  return response.data.data;
}

export async function fetchTopicDetail(id: number | string): Promise<TopicDetail> {
  const response = await request.get<ApiResponse<TopicDetail>>(`/topics/${id}`);
  return response.data.data;
}

export async function createTopic(data: TopicCreateRequest): Promise<IdResponse> {
  const response = await request.post<ApiResponse<IdResponse>>("/topics", data);
  return response.data.data;
}

export async function createReply(
  topicId: number | string,
  data: TopicReplyCreateRequest
): Promise<void> {
  await request.post<ApiResponse<void>>(`/topics/${topicId}/replies`, data);
}

export async function markSolved(topicId: number | string): Promise<void> {
  await request.patch<ApiResponse<void>>(`/topics/${topicId}/solved`);
}

export async function markSolution(
  topicId: number | string,
  replyId: number | string
): Promise<void> {
  await request.patch<ApiResponse<void>>(`/topics/${topicId}/replies/${replyId}/solution`);
}
