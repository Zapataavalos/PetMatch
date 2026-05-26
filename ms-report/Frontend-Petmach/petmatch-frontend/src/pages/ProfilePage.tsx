import { CalendarDays, LogOut, Mail, Settings, Shield, User } from "lucide-react";
import { isAxiosError } from "axios";
import { useEffect, useMemo, useState } from "react";
import type { FormEvent } from "react";
import { userApi } from "../api/userApi";
import { useAuth } from "../auth/useAuth";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { Input } from "../components/ui/Input";
import type { Usuario, UserRole } from "../types";

export function ProfilePage() {
  const { user, role, logout, updateSession } = useAuth();

  const [profile, setProfile] = useState<Usuario | null>(null);
  const [nombre, setNombre] = useState(user?.nombre ?? "");
  const [email, setEmail] = useState(user?.email ?? "");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
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
          setError(getProfileErrorMessage(error));
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
  }, []);

  const currentName = profile?.nombre ?? user?.nombre ?? "Usuario";
  const currentEmail = profile?.email ?? user?.email ?? "";
  const currentRole = getRoleLabel(role, profile?.idRol ?? user?.idRol);
  const memberSince = formatMemberSince(profile?.fechaRegistro ?? user?.fechaRegistro);

  const hasChanges = useMemo(() => {
    return nombre.trim() !== currentName || email.trim().toLowerCase() !== currentEmail;
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
      setSuccess("Perfil actualizado.");
    } catch (error) {
      setError(getProfileErrorMessage(error));
    } finally {
      setSaving(false);
    }
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
              {currentEmail}
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
          <button className="flex h-14 w-full items-center gap-4 rounded-xl border border-[#f5c400]/30 bg-[#f5c400]/10 px-5 font-bold text-[#f5c400]">
            <User size={22} />
            Informacion personal
          </button>

          <button className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#aaaaba] hover:bg-[#17171b]">
            <Shield size={22} />
            Privacidad y seguridad
          </button>

          <button className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#aaaaba] hover:bg-[#17171b]">
            <Settings size={22} />
            Preferencias
          </button>

          <button
            type="button"
            onClick={logout}
            className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#ef4444] hover:bg-[#ef4444]/10"
          >
            <LogOut size={22} />
            Cerrar sesion
          </button>
        </aside>

        <Card className="p-8">
          <form onSubmit={handleSubmit}>
            <h2 className="text-3xl font-black">Informacion personal</h2>
            <div className="mt-6 h-px bg-[#292930]" />

            {error && (
              <div className="mt-6 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
                {error}
              </div>
            )}

            {success && (
              <div className="mt-6 rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-3 text-sm text-emerald-300">
                {success}
              </div>
            )}

            <div className="mt-8 grid grid-cols-1 gap-6">
              <Input
                label="Nombre completo"
                value={nombre}
                onChange={(event) => setNombre(event.target.value)}
                minLength={3}
                maxLength={100}
                required
                disabled={loading || saving}
              />

              <Input
                label="Correo electronico"
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                maxLength={120}
                required
                disabled={loading || saving}
              />
            </div>

            <div className="mt-10 flex justify-end">
              <Button type="submit" disabled={loading || saving || !hasChanges}>
                {saving ? "Guardando..." : "Guardar cambios"}
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </section>
  );
}

interface ApiErrorResponse {
  message?: string;
  errors?: Record<string, string>;
}

function getProfileErrorMessage(error: unknown) {
  if (isAxiosError<ApiErrorResponse>(error)) {
    const data = error.response?.data;

    if (data?.errors) {
      return Object.values(data.errors).join(" ");
    }

    if (data?.message) {
      return data.message;
    }

    if (error.response?.status === 401) {
      return "Tu sesion expiro. Inicia sesion nuevamente.";
    }
  }

  return "No fue posible actualizar el perfil.";
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

function formatMemberSince(fechaRegistro?: string) {
  if (!fechaRegistro) {
    return "Miembro registrado";
  }

  const date = new Date(fechaRegistro);

  if (Number.isNaN(date.getTime())) {
    return "Miembro registrado";
  }

  return `Miembro desde ${new Intl.DateTimeFormat("es-CL", {
    month: "long",
    year: "numeric",
  }).format(date)}`;
}
