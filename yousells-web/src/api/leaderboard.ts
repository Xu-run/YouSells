import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'
import type { LeaderboardItem } from '@/types/leaderboard'

export function getLeaderboard() {
  return request.get<ApiResponse<LeaderboardItem[]>>('/leaderboard')
}
