import { isAxiosError } from "axios";
import {
  CalendarDays,
  CheckCircle2,
  LogOut,
  Mail,
  RefreshCw,
  Settings as SettingsIcon,
  User,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { userApi } from "../api/userApi";
import { useAuth } from "../auth/useAuth";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { Input } from "../components/ui/Input";
import { useI18n } from "../i18n/useI18n";
import type { Usuario, UserRole } from "../types";

type ProfileSection = "personal" | "preferences";

export function ProfilePage() {
  const { user, role, logout, updateSession } = useAuth();
  const { language, country, t } = useI18n();
  const navigate = useNavigate();

  const [activeSection, setActiveSection] = useState<ProfileSection>("personal");
  const [profile, setProfile] = useState<Usuario | null>(null);
  const [nombre, setNombre] = useState(user?.nombre ?? "");
  const [email, setEmail] = useState(user?.email ?? "");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [reloadNonce, setReloadNonce] = useState(0);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    let active = true;

    async function loadProfile() {
      setLoading(true);
      setError("");

      try {
        const currentProfile = await userApi.getMe();

        if (!active) {
          return;
        }

        setProfile(currentProfile);
        setNombre(currentProfile.nombre);
        setEmail(currentProfile.email);
      } catch (error) {
        if (active) {
          setError(getProfileErrorMessage(error, t("profile.loadError"), t("profile.expired")));
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    void loadProfile();

    return () => {
      active = false;
    };
  }, [reloadNonce, t]);

  const currentName = profile?.nombre ?? user?.nombre ?? "Usuario";
  const currentEmail = profile?.email ?? user?.email ?? "";
  const currentRole = getRoleLabel(role, profile?.idRol ?? user?.idRol);
  const memberSince = formatMemberSince(
    profile?.fechaRegistro ?? user?.fechaRegistro,
    language,
    t("profile.memberRegistered"),
    t("profile.memberSince")
  );

  const hasChanges = useMemo(() => {
    return (
      nombre.trim() !== currentName.trim() ||
      email.trim().toLowerCase() !== currentEmail.trim().toLowerCase()
    );
  }, [nombre, email, currentName, currentEmail]);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setSuccess("");
    setSaving(true);

    try {
      const updatedProfile = await userApi.updateMe({
        nombre: nombre.trim(),
        email: email.trim(),
      });

      updateSession(updatedProfile);
      setProfile({
        idUsuario: updatedProfile.idUsuario,
        nombre: updatedProfile.nombre,
        email: updatedProfile.email,
        fechaRegistro: updatedProfile.fechaRegistro,
        idRol: updatedProfile.idRol,
      });
      setNombre(updatedProfile.nombre);
      setEmail(updatedProfile.email);
      setSuccess(t("profile.saved"));
    } catch (error) {
      setError(getProfileErrorMessage(error, t("profile.saveError"), t("profile.expired")));
    } finally {
      setSaving(false);
    }
  };

  const handleReset = () => {
    setNombre(currentName);
    setEmail(currentEmail);
    setError("");
    setSuccess("");
  };

  return (
    <section className="mx-auto max-w-6xl px-8 py-14">
      <div className="flex flex-col gap-6 border-b border-[#24242a] pb-10 md:flex-row md:items-center md:gap-8">
        <div className="flex h-32 w-32 shrink-0 items-center justify-center rounded-full border border-[#f5c400] text-[#8d8d98]">
          <User size={56} />
        </div>

        <div className="min-w-0">
          <h1 className="break-words text-4xl font-black">{currentName}</h1>
          <div className="mt-3 flex flex-wrap gap-x-5 gap-y-2 text-[#aaaaba]">
            <span className="inline-flex items-center gap-2">
              <Mail size={18} />
              {currentEmail || t("profile.email")}
            </span>
            <span className="inline-flex items-center gap-2">
              <CalendarDays size={18} />
              {memberSince}
            </span>
          </div>

          <div className="mt-5 flex flex-wrap gap-4">
            <span className="rounded-full border border-[#2a2a30] bg-[#17171b] px-4 py-2">
              <b className="text-[#f5c400]">ID</b> #{profile?.idUsuario ?? user?.idUsuario}
            </span>
            <span className="rounded-full border border-[#2a2a30] bg-[#17171b] px-4 py-2">
              <b className="text-[#10b981]">{currentRole}</b>
            </span>
          </div>
        </div>
      </div>

      <div className="mt-10 grid grid-cols-1 gap-8 lg:grid-cols-[280px_1fr]">
        <aside className="space-y-3">
          <ProfileNavButton
            active={activeSection === "personal"}
            icon={User}
            label={t("profile.personalInfo")}
            onClick={() => setActiveSection("personal")}
          />
          <ProfileNavButton
            active={activeSection === "preferences"}
            icon={SettingsIcon}
            label={t("profile.preferences")}
            onClick={() => setActiveSection("preferences")}
          />

          <button
            type="button"
            onClick={logout}
            className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#ef4444] hover:bg-[#ef4444]/10"
          >
            <LogOut size={22} />
            {t("profile.logout")}
          </button>
        </aside>

        {activeSection === "personal" && (
          <Card className="p-8">
            <form onSubmit={handleSubmit}>
              <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                <h2 className="text-3xl font-black">{t("profile.personalInfo")}</h2>
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => setReloadNonce((current) => current + 1)}
                  disabled={saving}
                >
                  <RefreshCw className={`mr-2 inline ${loading ? "animate-spin" : ""}`} size={17} />
                  {loading ? t("profile.refreshing") : t("profile.refresh")}
                </Button>
              </div>
              <div className="mt-6 h-px bg-[#292930]" />

              {error && (
                <div className="mt-6 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
                  {error}
                </div>
              )}

              {success && (
                <div className="mt-6 flex items-center gap-2 rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-3 text-sm text-emerald-300">
                  <CheckCircle2 size={17} />
                  {success}
                </div>
              )}

              <div className="mt-8 grid grid-cols-1 gap-6">
                <Input
                  label={t("profile.fullName")}
                  value={nombre}
                  onChange={(event) => {
                    setNombre(event.target.value);
                    setSuccess("");
                  }}
                  minLength={3}
                  maxLength={100}
                  required
                  disabled={loading || saving}
                />

                <Input
                  label={t("profile.email")}
                  type="email"
                  value={email}
                  onChange={(event) => {
                    setEmail(event.target.value);
                    setSuccess("");
                  }}
                  maxLength={120}
                  required
                  disabled={loading || saving}
                />
              </div>

              <div className="mt-10 flex flex-col justify-end gap-3 sm:flex-row">
                <Button
                  type="button"
                  variant="secondary"
                  onClick={handleReset}
                  disabled={loading || saving || !hasChanges}
                >
                  {t("profile.reset")}
                </Button>
                <Button type="submit" disabled={loading || saving || !hasChanges}>
                  {saving ? t("profile.saving") : t("profile.save")}
                </Button>
              </div>
            </form>
          </Card>
        )}

        {activeSection === "preferences" && (
          <Card className="p-8">
            <h2 className="text-3xl font-black">{t("profile.preferences")}</h2>
            <div className="mt-6 h-px bg-[#292930]" />

            <div className="mt-8 grid grid-cols-1 gap-5 md:grid-cols-2">
              <ProfileInfoTile
                title={t("settings.language")}
                value={language === "ES" ? t("settings.spanish") : t("settings.english")}
              />
              <ProfileInfoTile title={t("settings.country")} value={country} />
            </div>

            <div className="mt-10 flex justify-end">
              <Button type="button" onClick={() => navigate("/configuracion")}>
                <SettingsIcon className="mr-2 inline" size={17} />
                {t("profile.openSettings")}
              </Button>
            </div>
          </Card>
        )}
      </div>
    </section>
  );
}

function ProfileNavButton({
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

function ProfileInfoTile({ title, value }: { title: string; value: string }) {
  return (
    <div className="rounded-xl border border-[#24242a] bg-[#101013] p-5">
      <p className="text-sm font-bold text-[#aaaaba]">{title}</p>
      <p className="mt-3 break-words font-black text-white">{value}</p>
    </div>
  );
}

interface ApiErrorResponse {
  message?: string;
  errors?: Record<string, string>;
}

function getProfileErrorMessage(error: unknown, fallback: string, expiredMessage: string) {
  if (isAxiosError<ApiErrorResponse>(error)) {
    const data = error.response?.data;

    if (data?.errors) {
      return Object.values(data.errors).join(" ");
    }

    if (data?.message) {
      return data.message;
    }

    if (error.response?.status === 401) {
      return expiredMessage;
    }
  }

  return fallback;
}

function getRoleLabel(role: UserRole | null, idRol?: number) {
  if (role === "ADMIN" || idRol === 1) {
    return "ADMIN";
  }

  if (role === "DUENO" || idRol === 2) {
    return "DUENO";
  }

  return "CIUDADANO";
}

function formatMemberSince(
  fechaRegistro: string | undefined,
  language: "ES" | "EN",
  registeredLabel: string,
  memberSinceLabel: string
) {
  if (!fechaRegistro) {
    return registeredLabel;
  }

  const date = new Date(fechaRegistro);

  if (Number.isNaN(date.getTime())) {
    return registeredLabel;
  }

  return `${memberSinceLabel} ${new Intl.DateTimeFormat(language === "ES" ? "es-CL" : "en-US", {
    month: "long",
    year: "numeric",
  }).format(date)}`;
}
