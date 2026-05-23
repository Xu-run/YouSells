import request from '@/utils/request'
import type { ApiResponse, PageResponse } from '@/types/api'
import type { NotificationItem } from '@/types/notification'

export function getNotifications(page = 1, pageSize = 20) {
  return request.get<ApiResponse<PageResponse<NotificationItem>>>('/notifications', {
    params: { page, pageSize }
  })
}

export function getUnreadCount() {
  return request.get<ApiResponse<number>>('/notifications/unread-count')
}

export function markRead(id: number) {
  return request.put<ApiResponse<void>>(`/notifications/${id}/read`)
}

export function markAllRead() {
  return request.put<ApiResponse<void>>('/notifications/read-all')
}
