import { isAxiosError } from "axios";
import {
  AlertTriangle,
  Bell,
  CheckCircle2,
  Globe,
  Lock,
  Moon,
  Palette,
  Save,
  Shield,
  Wifi,
  WifiOff,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { useEffect, useState } from "react";
import type { ReactNode } from "react";
import { configuracionApi } from "../api/configuracionApi";
import { useAuth } from "../auth/useAuth";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { CHILE_COUNTRY } from "../i18n/I18nContextCore";
import { useI18n } from "../i18n/useI18n";
import { requestBrowserNotificationPermission } from "../notifications/NotificationContextCore";
import type { AppLanguage, ColorCatalogo, ConfiguracionUsuarioResponse } from "../types";
import {
  applyThemePreference,
  USER_SETTINGS_KEY,
  USER_SETTINGS_UPDATED_EVENT,
} from "../utils/theme";

const FALLBACK_COLORS: ColorCatalogo[] = [
  { idColor: 1, nombreColor: "AMARILLO PETMATCH", codigoHexadecimal: "#F5C400" },
  { idColor: 2, nombreColor: "VERDE RESCATE", codigoHexadecimal: "#10B981" },
  { idColor: 3, nombreColor: "AZUL COMUNIDAD", codigoHexadecimal: "#60A5FA" },
];

type SettingsTab = "preferences" | "notifications" | "security";
type NoticeType = "success" | "warning" | "error";
type NoticeKey = "settings.saved" | "settings.localSaved" | "settings.loadWarning";

interface LocalSettings {
  idColor: number;
  modoOscuro: boolean;
  idioma: AppLanguage;
  pais: typeof CHILE_COUNTRY;
  matchAlerts: boolean;
  nearbyReports: boolean;
  emailUpdates: boolean;
}

interface Notice {
  type: NoticeType;
  key: NoticeKey;
  detail?: string;
}

export function SettingsPage() {
  const { user } = useAuth();
  const { language, setLanguage, t } = useI18n();
  const [activeTab, setActiveTab] = useState<SettingsTab>("preferences");
  const [settings, setSettings] = useState<LocalSettings>(() => readLocalSettings(language));
  const [configurationId, setConfigurationId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [backendOnline, setBackendOnline] = useState(true);
  const [notice, setNotice] = useState<Notice | null>(null);

  useEffect(() => {
    localStorage.setItem(USER_SETTINGS_KEY, JSON.stringify(settings));
    window.dispatchEvent(new Event(USER_SETTINGS_UPDATED_EVENT));
  }, [settings]);

  useEffect(() => {
    applyThemePreference(settings.modoOscuro);
  }, [settings.modoOscuro]);

  useEffect(() => {
    setSettings((current) =>
      current.idioma === language ? current : { ...current, idioma: language }
    );
  }, [language]);

  useEffect(() => {
    let active = true;

    async function loadSettings() {
      setLoading(true);
      setNotice(null);

      let nextColors = FALLBACK_COLORS;
      let remoteSettings: ConfiguracionUsuarioResponse | null = null;
      let serviceWarning = false;

      try {
        const colors = await configuracionApi.getColors();
        nextColors = colors.length > 0 ? colors : FALLBACK_COLORS;
      } catch {
        serviceWarning = true;
      }

      if (user?.idUsuario) {
        try {
          remoteSettings = await configuracionApi.getByUser(user.idUsuario);
        } catch (error) {
          if (!isNotFound(error)) {
            serviceWarning = true;
          }
        }
      }

      if (!active) {
        return;
      }

      setBackendOnline(!serviceWarning);

      setSettings((current) => {
        const merged = remoteSettings ? mergeRemoteSettings(current, remoteSettings) : current;
        return ensureValidColor(merged, nextColors);
      });

      if (remoteSettings) {
        setConfigurationId(remoteSettings.idConfiguracionUsuario);
        setLanguage(remoteSettings.idioma);
      }

      if (serviceWarning) {
        setNotice({ type: "warning", key: "settings.loadWarning" });
      }

      setLoading(false);
    }

    void loadSettings();

    return () => {
      active = false;
    };
  }, [setLanguage, user?.idUsuario]);

  const notificationsActive =
    settings.matchAlerts || settings.nearbyReports || settings.emailUpdates;

  const updateSettings = (patch: Partial<LocalSettings>) => {
    setSettings((current) => ({
      ...current,
      ...patch,
      pais: CHILE_COUNTRY,
    }));
    setNotice(null);
  };

  const handleLanguageChange = (nextLanguage: AppLanguage) => {
    setLanguage(nextLanguage);
    updateSettings({ idioma: nextLanguage });
  };

  const handleNotificationSettingChange = (
    key: "matchAlerts" | "nearbyReports" | "emailUpdates",
    checked: boolean
  ) => {
    if (key === "matchAlerts") {
      updateSettings({ matchAlerts: checked });
    } else if (key === "nearbyReports") {
      updateSettings({ nearbyReports: checked });
    } else {
      updateSettings({ emailUpdates: checked });
    }

    if (checked && key !== "emailUpdates") {
      void requestBrowserNotificationPermission();
    }
  };

  const handleSave = async () => {
    setSaving(true);
    setNotice(null);

    if (!user?.idUsuario) {
      setBackendOnline(false);
      setNotice({ type: "warning", key: "settings.localSaved" });
      setSaving(false);
      return;
    }

    try {
      const payload = {
        idUsuario: user.idUsuario,
        idColor: settings.idColor,
        notificacionesActivas: notificationsActive,
        modoOscuro: settings.modoOscuro,
        idioma: settings.idioma,
      };

      const saved = configurationId
        ? await configuracionApi.update(configurationId, payload)
        : await configuracionApi.create(payload);

      setConfigurationId(saved.idConfiguracionUsuario);
      setBackendOnline(true);
      setLanguage(saved.idioma);
      setNotice({ type: "success", key: "settings.saved" });
    } catch (error) {
      setBackendOnline(false);
      setNotice({
        type: "warning",
        key: "settings.localSaved",
        detail: getSettingsErrorMessage(error),
      });
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="mx-auto max-w-6xl px-8 py-12">
      <div className="border-b border-[#24242a] pb-9">
        <div className="flex items-center gap-4">
          <Shield size={38} className="text-[#f5c400]" />
          <h1 className="text-4xl font-black">{t("settings.title")}</h1>
        </div>

        <p className="mt-4 text-lg text-[#aaaaba]">{t("settings.subtitle")}</p>
      </div>

      <div className="mt-10 grid grid-cols-1 gap-8 lg:grid-cols-[280px_1fr]">
        <aside className="space-y-3">
          <SettingsNavButton
            active={activeTab === "preferences"}
            icon={Palette}
            label={t("settings.preferences")}
            onClick={() => setActiveTab("preferences")}
          />
          <SettingsNavButton
            active={activeTab === "notifications"}
            icon={Bell}
            label={t("settings.notifications")}
            onClick={() => setActiveTab("notifications")}
          />
          <SettingsNavButton
            active={activeTab === "security"}
            icon={Lock}
            label={t("settings.security")}
            onClick={() => setActiveTab("security")}
          />
        </aside>

        <div className="space-y-8">
          {notice && (
            <NoticeBanner
              type={notice.type}
              message={`${t(notice.key)} ${notice.detail ?? ""}`.trim()}
            />
          )}

          {activeTab === "preferences" && (
            <>
              <Card className="p-8">
                <div className="flex items-center gap-3">
                  <Palette className="text-[#f5c400]" />
                  <h2 className="text-2xl font-black">{t("settings.appearance")}</h2>
                </div>

                <div className="mt-8 max-w-md">
                  <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                    {t("settings.theme")}
                  </span>

                  <SettingToggle
                    title={t("settings.darkMode")}
                    description={settings.modoOscuro ? t("settings.darkMode") : t("settings.theme")}
                    checked={settings.modoOscuro}
                    disabled={loading || saving}
                    onChange={(checked) => updateSettings({ modoOscuro: checked })}
                    compact
                    icon={<Moon size={20} className="text-[#f5c400]" />}
                  />
                </div>
              </Card>

              <Card className="p-8">
                <div className="flex items-center gap-3">
                  <Globe className="text-[#f5c400]" />
                  <h2 className="text-2xl font-black">{t("settings.languageRegion")}</h2>
                </div>

                <div className="mt-8 grid grid-cols-1 gap-6 md:grid-cols-2">
                  <label className="block">
                    <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                      {t("settings.language")}
                    </span>

                    <select
                      value={settings.idioma}
                      onChange={(event) =>
                        handleLanguageChange(event.target.value as AppLanguage)
                      }
                      disabled={loading || saving}
                      className="h-14 w-full rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 text-white outline-none focus:border-[#f5c400]"
                    >
                      <option value="ES">{t("settings.spanish")}</option>
                      <option value="EN">{t("settings.english")}</option>
                    </select>
                  </label>

                  <label className="block">
                    <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                      {t("settings.country")}
                    </span>

                    <select
                      value={CHILE_COUNTRY}
                      disabled
                      className="h-14 w-full rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 text-white outline-none disabled:opacity-100"
                    >
                      <option value={CHILE_COUNTRY}>{t("settings.chile")}</option>
                    </select>

                    <p className="mt-3 text-sm font-semibold text-[#85858f]">
                      {t("settings.countryLocked")}
                    </p>
                  </label>
                </div>
              </Card>
            </>
          )}

          {activeTab === "notifications" && (
            <Card className="p-8">
              <div className="flex items-center gap-3">
                <Bell className="text-[#f5c400]" />
                <h2 className="text-2xl font-black">{t("settings.notifications")}</h2>
              </div>

              <div className="mt-8 space-y-5">
                <SettingToggle
                  title={t("settings.matchAlerts")}
                  description={t("settings.matchAlertsDescription")}
                  checked={settings.matchAlerts}
                  disabled={loading || saving}
                  onChange={(checked) =>
                    handleNotificationSettingChange("matchAlerts", checked)
                  }
                />

                <SettingToggle
                  title={t("settings.nearbyReports")}
                  description={t("settings.nearbyReportsDescription")}
                  checked={settings.nearbyReports}
                  disabled={loading || saving}
                  onChange={(checked) =>
                    handleNotificationSettingChange("nearbyReports", checked)
                  }
                />

                <SettingToggle
                  title={t("settings.emailUpdates")}
                  description={t("settings.emailUpdatesDescription")}
                  checked={settings.emailUpdates}
                  disabled={loading || saving}
                  onChange={(checked) =>
                    handleNotificationSettingChange("emailUpdates", checked)
                  }
                />
              </div>
            </Card>
          )}

          {activeTab === "security" && (
            <Card className="p-8">
              <div className="flex items-center gap-3">
                <Lock className="text-[#f5c400]" />
                <h2 className="text-2xl font-black">{t("settings.security")}</h2>
              </div>

              <div className="mt-8 grid grid-cols-1 gap-5 md:grid-cols-2">
                <StatusPanel
                  title={t("settings.backendState")}
                  value={backendOnline ? t("settings.backendOnline") : t("settings.backendLocal")}
                  online={backendOnline}
                />
                <StatusPanel
                  title={t("settings.country")}
                  value={t("settings.chile")}
                  online
                />
              </div>
            </Card>
          )}

          <div className="flex justify-end">
            <Button type="button" onClick={() => void handleSave()} disabled={loading || saving}>
              <Save className="mr-2 inline" size={18} />
              {saving ? t("settings.saving") : t("settings.save")}
            </Button>
          </div>
        </div>
      </div>
    </section>
  );
}

function SettingsNavButton({
  active,
  icon: Icon,
  label,
  onClick,
}: {
  active: boolean;
  icon: LucideIcon;
  label: string;
  onClick: () => void;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold transition ${
        active
          ? "border border-[#f5c400]/30 bg-[#f5c400]/10 text-[#f5c400]"
          : "text-[#aaaaba] hover:bg-[#17171b]"
      }`}
    >
      <Icon size={22} />
      {label}
    </button>
  );
}

function SettingToggle({
  title,
  description,
  checked,
  disabled = false,
  compact = false,
  icon,
  onChange,
}: {
  title: string;
  description: string;
  checked: boolean;
  disabled?: boolean;
  compact?: boolean;
  icon?: ReactNode;
  onChange: (checked: boolean) => void;
}) {
  return (
    <div
      className={`flex items-center justify-between gap-6 rounded-xl border border-[#24242a] bg-[#101013] ${
        compact ? "h-14 px-4" : "p-5"
      }`}
    >
      <div className="flex min-w-0 items-center gap-3">
        {icon}
        <div className="min-w-0">
          <p className="truncate font-black text-white">{title}</p>
          {!compact && <p className="mt-1 text-sm text-[#aaaaba]">{description}</p>}
        </div>
      </div>

      <button
        type="button"
        role="switch"
        aria-label={title}
        aria-checked={checked}
        disabled={disabled}
        onClick={() => onChange(!checked)}
        className={`relative h-7 w-12 shrink-0 rounded-full transition disabled:cursor-not-allowed disabled:opacity-60 ${
          checked ? "bg-[#f5c400]" : "bg-[#33333a]"
        }`}
      >
        <span
          className={`absolute top-1 h-5 w-5 rounded-full bg-white transition ${
            checked ? "left-6" : "left-1"
          }`}
        />
      </button>
    </div>
  );
}

function NoticeBanner({ type, message }: { type: NoticeType; message: string }) {
  const styles = {
    success: "border-emerald-500/30 bg-emerald-500/10 text-emerald-300",
    warning: "border-[#f5c400]/30 bg-[#f5c400]/10 text-[#f5c400]",
    error: "border-red-500/30 bg-red-500/10 text-red-300",
  };

  const Icon = type === "success" ? CheckCircle2 : AlertTriangle;

  return (
    <div className={`flex items-center gap-3 rounded-xl border p-4 text-sm font-bold ${styles[type]}`}>
      <Icon size={18} />
      {message}
    </div>
  );
}

function StatusPanel({
  title,
  value,
  online,
}: {
  title: string;
  value: string;
  online: boolean;
}) {
  return (
    <div className="rounded-xl border border-[#24242a] bg-[#101013] p-5">
      <p className="text-sm font-bold text-[#aaaaba]">{title}</p>
      <p className="mt-3 flex items-center gap-2 font-black text-white">
        {online ? <Wifi size={18} className="text-[#10b981]" /> : <WifiOff size={18} className="text-[#f5c400]" />}
        {value}
      </p>
    </div>
  );
}

function readLocalSettings(language: AppLanguage): LocalSettings {
  const fallback = createDefaultSettings(language);
  const stored = localStorage.getItem(USER_SETTINGS_KEY);

  if (!stored) {
    return fallback;
  }

  try {
    const parsed: unknown = JSON.parse(stored);

    if (!isRecord(parsed)) {
      return fallback;
    }

    return {
      idColor: getNumber(parsed.idColor, fallback.idColor),
      modoOscuro: getBoolean(parsed.modoOscuro, fallback.modoOscuro),
      idioma: parsed.idioma === "EN" ? "EN" : parsed.idioma === "ES" ? "ES" : fallback.idioma,
      pais: CHILE_COUNTRY,
      matchAlerts: getBoolean(parsed.matchAlerts, fallback.matchAlerts),
      nearbyReports: getBoolean(parsed.nearbyReports, fallback.nearbyReports),
      emailUpdates: getBoolean(parsed.emailUpdates, fallback.emailUpdates),
    };
  } catch {
    return fallback;
  }
}

function createDefaultSettings(language: AppLanguage): LocalSettings {
  return {
    idColor: FALLBACK_COLORS[0].idColor,
    modoOscuro: true,
    idioma: language,
    pais: CHILE_COUNTRY,
    matchAlerts: true,
    nearbyReports: true,
    emailUpdates: false,
  };
}

function mergeRemoteSettings(
  current: LocalSettings,
  remote: ConfiguracionUsuarioResponse
): LocalSettings {
  const hasLocalNotifications =
    current.matchAlerts || current.nearbyReports || current.emailUpdates;

  return {
    ...current,
    idColor: remote.idColor,
    modoOscuro: remote.modoOscuro,
    idioma: remote.idioma,
    pais: CHILE_COUNTRY,
    matchAlerts: remote.notificacionesActivas
      ? current.matchAlerts || !hasLocalNotifications
      : false,
    nearbyReports: remote.notificacionesActivas
      ? current.nearbyReports || !hasLocalNotifications
      : false,
    emailUpdates: remote.notificacionesActivas ? current.emailUpdates : false,
  };
}

function ensureValidColor(settings: LocalSettings, colors: ColorCatalogo[]): LocalSettings {
  if (colors.some((color) => color.idColor === settings.idColor)) {
    return settings;
  }

  return {
    ...settings,
    idColor: colors[0]?.idColor ?? FALLBACK_COLORS[0].idColor,
  };
}

function isNotFound(error: unknown) {
  return isAxiosError(error) && error.response?.status === 404;
}

function getSettingsErrorMessage(error: unknown) {
  if (isAxiosError<ApiErrorResponse>(error)) {
    const data = error.response?.data;

    if (data?.errors) {
      return Object.values(data.errors).join(" ");
    }

    if (data?.message) {
      return data.message;
    }
  }

  return "";
}

function getNumber(value: unknown, fallback: number) {
  return typeof value === "number" && Number.isFinite(value) ? value : fallback;
}

function getBoolean(value: unknown, fallback: boolean) {
  return typeof value === "boolean" ? value : fallback;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}

interface ApiErrorResponse {
  message?: string;
  errors?: Record<string, string>;
}
