export interface NotificationItem {
  id: number
  type: string
  title: string
  content: string
  businessType: string | null
  businessId: number | null
  isRead: number
  createdAt: string
}
