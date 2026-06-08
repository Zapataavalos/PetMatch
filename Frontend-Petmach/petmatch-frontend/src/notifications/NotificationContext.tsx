import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import type { ReactNode } from "react";
import { reportApi } from "../api/reportApi";
import { useAuth } from "../auth/useAuth";
import type { ReportApiResponse } from "../types";
import { buildReportMatches, formatDistance } from "../utils/matchEngine";
import { USER_SETTINGS_KEY, USER_SETTINGS_UPDATED_EVENT } from "../utils/theme";
import {
  type AppNotification,
  NotificationContext,
  type NotificationContextValue,
} from "./NotificationContextCore";

const POLL_INTERVAL_MS = 60000;
const NOTIFICATIONS_STORAGE_PREFIX = "petmatch_notifications";
const MAX_NOTIFICATIONS = 40;

interface NotificationSettings {
  matchAlerts: boolean;
  nearbyReports: boolean;
}

export function NotificationProvider({ children }: { children: ReactNode }) {
  const { user, isAuthenticated } = useAuth();
  const storageKey = `${NOTIFICATIONS_STORAGE_PREFIX}_${user?.idUsuario ?? "anon"}`;
  const [settings, setSettings] = useState<NotificationSettings>(() => readNotificationSettings());
  const [notifications, setNotifications] = useState<AppNotification[]>(() =>
    readStoredNotifications(storageKey)
  );
  const [loading, setLoading] = useState(false);
  const notificationsRef = useRef(notifications);
  const firstSyncDoneRef = useRef(false);

  useEffect(() => {
    notificationsRef.current = notifications;
  }, [notifications]);

  useEffect(() => {
    firstSyncDoneRef.current = false;
    setNotifications(readStoredNotifications(storageKey));
  }, [storageKey]);

  useEffect(() => {
    writeStoredNotifications(storageKey, notifications);
  }, [notifications, storageKey]);

  useEffect(() => {
    const handleSettingsChange = () => setSettings(readNotificationSettings());

    window.addEventListener(USER_SETTINGS_UPDATED_EVENT, handleSettingsChange);
    window.addEventListener("storage", handleSettingsChange);

    return () => {
      window.removeEventListener(USER_SETTINGS_UPDATED_EVENT, handleSettingsChange);
      window.removeEventListener("storage", handleSettingsChange);
    };
  }, []);

  const refresh = useCallback(async () => {
    if (!isAuthenticated || (!settings.matchAlerts && !settings.nearbyReports)) {
      setLoading(false);
      return;
    }

    setLoading(true);

    try {
      const reports = await reportApi.getAll();
      const generated = buildNotifications(reports, settings);
      const knownIds = new Set(notificationsRef.current.map((notification) => notification.id));
      const newNotifications = generated.filter((notification) => !knownIds.has(notification.id));

      setNotifications((current) =>
        mergeNotifications(current, generated).slice(0, MAX_NOTIFICATIONS)
      );

      if (firstSyncDoneRef.current) {
        newNotifications.forEach(showBrowserNotification);
      } else {
        firstSyncDoneRef.current = true;
      }
    } catch {
      firstSyncDoneRef.current = true;
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, settings]);

  useEffect(() => {
    void refresh();

    const intervalId = window.setInterval(() => {
      void refresh();
    }, POLL_INTERVAL_MS);

    return () => window.clearInterval(intervalId);
  }, [refresh]);

  const markAsRead = useCallback((id: string) => {
    setNotifications((current) =>
      current.map((notification) =>
        notification.id === id ? { ...notification, read: true } : notification
      )
    );
  }, []);

  const markAllAsRead = useCallback(() => {
    setNotifications((current) =>
      current.map((notification) => ({ ...notification, read: true }))
    );
  }, []);

  const clearAll = useCallback(() => {
    setNotifications([]);
  }, []);

  const value = useMemo<NotificationContextValue>(
    () => ({
      notifications,
      unreadCount: notifications.filter((notification) => !notification.read).length,
      loading,
      refresh,
      markAsRead,
      markAllAsRead,
      clearAll,
    }),
    [clearAll, loading, markAllAsRead, markAsRead, notifications, refresh]
  );

  return <NotificationContext.Provider value={value}>{children}</NotificationContext.Provider>;
}

function buildNotifications(
  reports: ReportApiResponse[],
  settings: NotificationSettings
): AppNotification[] {
  const notifications: AppNotification[] = [];
  const reportDates = new Map(reports.map((report) => [report.id, report.createdAt]));

  if (settings.nearbyReports) {
    reports
      .filter((report) => report.estado === "PERDIDO")
      .forEach((report) => {
        notifications.push({
          id: `report-${report.id}`,
          type: "report",
          title: `Reporte perdido: ${report.nombre}`,
          message: `${report.ubicacion} - ${report.descripcion}`,
          createdAt: toIsoDate(report.createdAt),
          href: "/reportes",
          read: false,
        });
      });
  }

  if (settings.matchAlerts) {
    buildReportMatches(reports).forEach((match) => {
      notifications.push({
        id: `match-${match.id}`,
        type: "match",
        title: `Coincidencia ${match.nivel.toLowerCase()}: ${match.porcentaje}%`,
        message: `${match.perdido.nombre} y ${match.encontrado.nombre} a ${formatDistance(
          match.distanciaKm
        )}`,
        createdAt: toIsoDate(
          newestDate(
            reportDates.get(match.perdido.id) ?? "",
            reportDates.get(match.encontrado.id) ?? ""
          )
        ),
        href: "/coincidencias",
        read: false,
      });
    });
  }

  return notifications.sort(sortByDateDesc);
}

function mergeNotifications(
  current: AppNotification[],
  generated: AppNotification[]
): AppNotification[] {
  const currentById = new Map(current.map((notification) => [notification.id, notification]));
  const generatedIds = new Set(generated.map((notification) => notification.id));
  const updated = generated.map((notification) => {
    const existing = currentById.get(notification.id);

    return existing
      ? { ...notification, createdAt: existing.createdAt, read: existing.read }
      : notification;
  });
  const history = current.filter((notification) => !generatedIds.has(notification.id));

  return [...updated, ...history].sort(sortByDateDesc);
}

function showBrowserNotification(notification: AppNotification) {
  if (typeof window === "undefined" || !("Notification" in window)) {
    return;
  }

  if (window.Notification.permission !== "granted") {
    return;
  }

  new window.Notification(notification.title, {
    body: notification.message,
    tag: notification.id,
  });
}

function readNotificationSettings(): NotificationSettings {
  const fallback: NotificationSettings = {
    matchAlerts: true,
    nearbyReports: true,
  };

  try {
    const stored = localStorage.getItem(USER_SETTINGS_KEY);

    if (!stored) {
      return fallback;
    }

    const parsed: unknown = JSON.parse(stored);

    if (!isRecord(parsed)) {
      return fallback;
    }

    return {
      matchAlerts: getBoolean(parsed.matchAlerts, fallback.matchAlerts),
      nearbyReports: getBoolean(parsed.nearbyReports, fallback.nearbyReports),
    };
  } catch {
    return fallback;
  }
}

function readStoredNotifications(storageKey: string) {
  try {
    const stored = localStorage.getItem(storageKey);

    if (!stored) {
      return [];
    }

    const parsed: unknown = JSON.parse(stored);

    if (!Array.isArray(parsed)) {
      return [];
    }

    return parsed.filter(isNotification).slice(0, MAX_NOTIFICATIONS);
  } catch {
    return [];
  }
}

function writeStoredNotifications(storageKey: string, notifications: AppNotification[]) {
  localStorage.setItem(storageKey, JSON.stringify(notifications.slice(0, MAX_NOTIFICATIONS)));
}

function isNotification(value: unknown): value is AppNotification {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.id === "string" &&
    (value.type === "match" || value.type === "report") &&
    typeof value.title === "string" &&
    typeof value.message === "string" &&
    typeof value.createdAt === "string" &&
    typeof value.href === "string" &&
    typeof value.read === "boolean"
  );
}

function newestDate(left: string, right: string) {
  const leftTime = new Date(left).getTime();
  const rightTime = new Date(right).getTime();

  if (Number.isFinite(leftTime) && (!Number.isFinite(rightTime) || leftTime > rightTime)) {
    return left;
  }

  return Number.isFinite(rightTime) ? right : new Date().toISOString();
}

function toIsoDate(value: string) {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return new Date().toISOString();
  }

  return date.toISOString();
}

function sortByDateDesc(a: AppNotification, b: AppNotification) {
  return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
}

function getBoolean(value: unknown, fallback: boolean) {
  return typeof value === "boolean" ? value : fallback;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}
