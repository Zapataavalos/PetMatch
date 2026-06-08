import { createContext, useContext } from "react";

export type NotificationType = "match" | "report";

export interface AppNotification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  createdAt: string;
  href: string;
  read: boolean;
}

export interface NotificationContextValue {
  notifications: AppNotification[];
  unreadCount: number;
  loading: boolean;
  refresh: () => Promise<void>;
  markAsRead: (id: string) => void;
  markAllAsRead: () => void;
  clearAll: () => void;
}

const fallbackContext: NotificationContextValue = {
  notifications: [],
  unreadCount: 0,
  loading: false,
  refresh: async () => undefined,
  markAsRead: () => undefined,
  markAllAsRead: () => undefined,
  clearAll: () => undefined,
};

export const NotificationContext = createContext<NotificationContextValue | null>(null);

export function useNotifications() {
  return useContext(NotificationContext) ?? fallbackContext;
}

export async function requestBrowserNotificationPermission() {
  if (typeof window === "undefined" || !("Notification" in window)) {
    return "unsupported";
  }

  if (window.Notification.permission === "default") {
    return window.Notification.requestPermission();
  }

  return window.Notification.permission;
}
