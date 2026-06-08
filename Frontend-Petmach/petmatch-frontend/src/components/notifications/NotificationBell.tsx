import { Bell, CheckCheck, RefreshCw, Trash2 } from "lucide-react";
import { useMemo, useState } from "react";
import type { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import {
  type AppNotification,
  useNotifications,
} from "../../notifications/NotificationContextCore";

export function NotificationBell() {
  const navigate = useNavigate();
  const {
    notifications,
    unreadCount,
    loading,
    refresh,
    markAsRead,
    markAllAsRead,
    clearAll,
  } = useNotifications();
  const [open, setOpen] = useState(false);
  const visibleNotifications = useMemo(
    () =>
      [...notifications].sort(
        (left, right) =>
          new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime()
      ),
    [notifications]
  );

  const handleOpen = () => {
    setOpen((current) => !current);
    void refresh();
  };

  const handleNotificationClick = (notification: AppNotification) => {
    markAsRead(notification.id);
    setOpen(false);
    navigate(notification.href);
  };

  return (
    <div className="relative">
      <button
        type="button"
        onClick={handleOpen}
        className="relative flex h-10 w-10 items-center justify-center rounded-full text-[#a7a7b2] transition hover:bg-[#17171b] hover:text-white"
        aria-label={`Notificaciones${unreadCount > 0 ? ` (${unreadCount})` : ""}`}
        aria-expanded={open}
      >
        <Bell size={20} />
        {unreadCount > 0 && (
          <span className="absolute right-1 top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-[#ef4444] px-1 text-[11px] font-black text-white">
            {unreadCount > 9 ? "9+" : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 top-12 z-[900] w-[min(360px,calc(100vw-2rem))] overflow-hidden rounded-2xl border border-[#24242a] bg-[#17171b] shadow-2xl">
          <div className="flex items-center justify-between gap-3 border-b border-[#24242a] px-4 py-3">
            <div>
              <p className="font-black text-white">Notificaciones</p>
              <p className="text-xs font-semibold text-[#85858f]">
                {unreadCount > 0 ? `${unreadCount} sin leer` : "Todo al dia"}
              </p>
            </div>

            <div className="flex items-center gap-1">
              <IconButton label="Actualizar" onClick={() => void refresh()}>
                <RefreshCw size={16} className={loading ? "animate-spin" : ""} />
              </IconButton>
              <IconButton label="Marcar todas como leidas" onClick={markAllAsRead}>
                <CheckCheck size={16} />
              </IconButton>
              <IconButton label="Limpiar notificaciones" onClick={clearAll}>
                <Trash2 size={16} />
              </IconButton>
            </div>
          </div>

          <div className="max-h-[420px] overflow-y-auto">
            {visibleNotifications.length === 0 ? (
              <div className="px-5 py-8 text-center">
                <p className="font-black text-white">Sin notificaciones</p>
                <p className="mt-2 text-sm text-[#85858f]">
                  Las coincidencias y reportes nuevos apareceran aqui.
                </p>
              </div>
            ) : (
              visibleNotifications.map((notification) => (
                <button
                  key={notification.id}
                  type="button"
                  onClick={() => handleNotificationClick(notification)}
                  className={`block w-full border-b border-[#24242a] px-4 py-4 text-left transition last:border-b-0 hover:bg-[#242429] ${
                    notification.read ? "bg-transparent" : "bg-[#f5c400]/10"
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <span
                      className={`mt-1 h-2.5 w-2.5 shrink-0 rounded-full ${
                        notification.read ? "bg-[#33333a]" : "bg-[#f5c400]"
                      }`}
                    />
                    <div className="min-w-0">
                      <p className="line-clamp-1 font-black text-white">
                        {notification.title}
                      </p>
                      <p className="mt-1 line-clamp-2 text-sm leading-relaxed text-[#aaaaba]">
                        {notification.message}
                      </p>
                      <p className="mt-2 text-xs font-bold text-[#85858f]">
                        {formatNotificationTime(notification.createdAt)}
                      </p>
                    </div>
                  </div>
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}

function IconButton({
  label,
  onClick,
  children,
}: {
  label: string;
  onClick: () => void;
  children: ReactNode;
}) {
  return (
    <button
      type="button"
      aria-label={label}
      title={label}
      onClick={onClick}
      className="flex h-8 w-8 items-center justify-center rounded-lg text-[#a7a7b2] transition hover:bg-[#242429] hover:text-white"
    >
      {children}
    </button>
  );
}

function formatNotificationTime(value: string) {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "Ahora";
  }

  const diffMinutes = Math.max(0, Math.floor((Date.now() - date.getTime()) / 60000));

  if (diffMinutes < 1) {
    return "Ahora";
  }

  if (diffMinutes < 60) {
    return `Hace ${diffMinutes} min`;
  }

  const diffHours = Math.floor(diffMinutes / 60);

  if (diffHours < 24) {
    return `Hace ${diffHours} h`;
  }

  return `Hace ${Math.floor(diffHours / 24)} d`;
}
