import {
  RefreshCw,
  Search,
  Shield,
  Trash2,
} from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { reportApi } from "../api/reportApi";
import { userApi } from "../api/userApi";
import { useAuth } from "../auth/useAuth";
import { StatusBadge } from "../components/reports/StatusBadge";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import type { ReportApiResponse, Usuario } from "../types";
import { buildAdminStats, buildLastSevenDaysData } from "../utils/adminMetrics";
import { formatRelativeTime } from "../utils/reportMapper";

type AdminTab = "overview" | "reports" | "users";

export function AdminPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<AdminTab>("overview");
  const [reports, setReports] = useState<ReportApiResponse[]>([]);
  const [users, setUsers] = useState<Usuario[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [deletingReportId, setDeletingReportId] = useState<number | null>(null);
  const [deletingUserId, setDeletingUserId] = useState<number | null>(null);
  const [error, setError] = useState("");

  const loadAdminData = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [reportData, userData] = await Promise.all([
        reportApi.getAll(),
        userApi.getAll(),
      ]);

      setReports(reportData);
      setUsers(userData);
    } catch {
      setError("No fue posible cargar los datos del panel de administracion.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadAdminData();
  }, [loadAdminData]);

  const stats = useMemo(() => buildAdminStats(users, reports), [reports, users]);
  const chartData = useMemo(() => buildLastSevenDaysData(reports), [reports]);
  const filteredReports = useMemo(
    () => filterReports(reports, search),
    [reports, search]
  );
  const filteredUsers = useMemo(() => filterUsers(users, search), [users, search]);

  const handleDeleteReport = async (report: ReportApiResponse) => {
    const confirmed = window.confirm(`Eliminar el reporte ${report.codigo}?`);

    if (!confirmed) {
      return;
    }

    setError("");
    setDeletingReportId(report.id);

    try {
      await reportApi.delete(report.id);
      setReports((current) => current.filter((item) => item.id !== report.id));
    } catch {
      setError("No fue posible eliminar el reporte.");
    } finally {
      setDeletingReportId(null);
    }
  };

  const handleDeleteUser = async (targetUser: Usuario) => {
    if (targetUser.idUsuario === user?.idUsuario) {
      setError("No puedes eliminar tu propio usuario desde esta vista.");
      return;
    }

    const confirmed = window.confirm(`Eliminar al usuario ${targetUser.nombre}?`);

    if (!confirmed) {
      return;
    }

    setError("");
    setDeletingUserId(targetUser.idUsuario);

    try {
      await userApi.delete(targetUser.idUsuario);
      setUsers((current) => current.filter((item) => item.idUsuario !== targetUser.idUsuario));
    } catch {
      setError("No fue posible eliminar el usuario.");
    } finally {
      setDeletingUserId(null);
    }
  };

  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="border-b border-[#24242a] pb-9">
        <div className="flex flex-col justify-between gap-6 lg:flex-row lg:items-center">
          <div>
            <div className="flex items-center gap-4">
              <Shield size={38} className="text-[#f5c400]" />
              <h1 className="text-4xl font-black">Panel de Administracion</h1>
            </div>

            <p className="mt-4 text-lg text-[#b8b8c3]">
              Monitorea actividad real del sistema, gestiona reportes y modera usuarios.
            </p>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row">
            <span className="inline-flex h-12 items-center rounded-xl border border-[#10b981]/30 bg-[#10b981]/10 px-5 text-sm font-black text-[#10b981]">
              {loading ? "SINCRONIZANDO" : "SISTEMA OPERATIVO"}
            </span>
            <Button type="button" variant="secondary" onClick={() => void loadAdminData()} disabled={loading}>
              <RefreshCw className={`mr-2 inline ${loading ? "animate-spin" : ""}`} size={18} />
              Actualizar
            </Button>
          </div>
        </div>
      </div>

      {error && (
        <div className="mt-6 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">
          {error}
        </div>
      )}

      <div className="mt-8 flex flex-wrap gap-6 border-b border-[#24242a]">
        <AdminTabButton
          active={activeTab === "overview"}
          label="Vista General"
          onClick={() => setActiveTab("overview")}
        />
        <AdminTabButton
          active={activeTab === "reports"}
          label="Gestion de Reportes"
          onClick={() => setActiveTab("reports")}
        />
        <AdminTabButton
          active={activeTab === "users"}
          label="Usuarios"
          onClick={() => setActiveTab("users")}
        />
      </div>

      <div className="mt-9 grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;

          return (
            <Card key={stat.label} className="p-7">
              <div className="flex items-start justify-between">
                <div
                  className={`flex h-14 w-14 items-center justify-center rounded-xl bg-[#0f0f12] ${stat.color}`}
                >
                  <Icon size={28} />
                </div>

                <span className="text-sm font-black text-[#85858f]">
                  {stat.detail}
                </span>
              </div>

              <p className="mt-7 font-bold text-[#aaaaba]">{stat.label}</p>
              <h3 className="mt-3 text-4xl font-black">{stat.value}</h3>
            </Card>
          );
        })}
      </div>

      {activeTab === "overview" && (
        <div className="mt-8 grid grid-cols-1 gap-8 xl:grid-cols-[1.4fr_0.8fr]">
          <Card className="min-h-[360px] p-7">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-black">Actividad de Reportes Ultimos 7 dias</h2>
              <span className="rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 py-3 text-sm font-bold text-[#aaaaba]">
                Datos reales
              </span>
            </div>

            <div className="mt-10 flex h-56 items-end gap-5">
              {chartData.map((day) => (
                <div key={day.label} className="flex flex-1 flex-col items-center gap-3">
                  <div
                    className="w-full rounded-t-xl bg-[#f5c400]/80 transition-all"
                    style={{ height: `${Math.max(8, day.percent)}%` }}
                    title={`${day.count} reportes`}
                  />
                  <span className="text-sm text-[#777783]">{day.label}</span>
                </div>
              ))}
            </div>
          </Card>

          <Card className="p-7">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-black">Reportes Recientes</h2>
              <button
                type="button"
                onClick={() => setActiveTab("reports")}
                className="font-bold text-[#f5c400]"
              >
                Ver todos
              </button>
            </div>

            <div className="mt-8 space-y-4">
              {reports.slice(0, 4).map((report) => (
                <div key={report.id} className="rounded-xl bg-[#111114] p-4">
                  <div className="flex items-center gap-3">
                    <b>{report.codigo}</b>
                    <StatusBadge status={report.estado} />
                  </div>
                  <p className="mt-2 text-sm text-[#8f8f9a]">
                    {report.nombre} - {formatRelativeTime(report.createdAt)}
                  </p>
                </div>
              ))}

              {!loading && reports.length === 0 && (
                <p className="rounded-xl border border-[#24242a] bg-[#09090b] p-4 text-sm text-[#aaaaba]">
                  No hay reportes registrados.
                </p>
              )}
            </div>
          </Card>
        </div>
      )}

      {activeTab !== "overview" && (
        <Card className="mt-8 p-7">
          <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-center">
            <h2 className="text-2xl font-black">
              {activeTab === "reports" ? "Gestion de Reportes" : "Usuarios"}
            </h2>

            <div className="flex h-12 w-full items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#09090b] px-4 lg:w-[360px]">
              <Search size={20} className="text-[#85858f]" />
              <input
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder={activeTab === "reports" ? "Buscar reporte..." : "Buscar usuario..."}
                className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
              />
            </div>
          </div>

          {activeTab === "reports" ? (
            <ReportsTable
              reports={filteredReports}
              deletingReportId={deletingReportId}
              onDeleteReport={handleDeleteReport}
            />
          ) : (
            <UsersTable
              users={filteredUsers}
              currentUserId={user?.idUsuario}
              deletingUserId={deletingUserId}
              onDeleteUser={handleDeleteUser}
            />
          )}
        </Card>
      )}
    </section>
  );
}

function AdminTabButton({
  active,
  label,
  onClick,
}: {
  active: boolean;
  label: string;
  onClick: () => void;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`border-b-2 pb-5 font-bold transition ${
        active
          ? "border-[#f5c400] text-[#f5c400]"
          : "border-transparent text-[#aaaaba] hover:text-white"
      }`}
    >
      {label}
    </button>
  );
}

function ReportsTable({
  reports,
  deletingReportId,
  onDeleteReport,
}: {
  reports: ReportApiResponse[];
  deletingReportId: number | null;
  onDeleteReport: (report: ReportApiResponse) => void;
}) {
  return (
    <div className="mt-7 overflow-x-auto">
      <table className="w-full min-w-[760px] text-left">
        <thead className="text-sm text-[#85858f]">
          <tr className="border-b border-[#24242a]">
            <th className="pb-4">Codigo</th>
            <th className="pb-4">Mascota</th>
            <th className="pb-4">Estado</th>
            <th className="pb-4">Ubicacion</th>
            <th className="pb-4">Fecha</th>
            <th className="pb-4 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {reports.map((report) => (
            <tr key={report.id} className="border-b border-[#1f1f24]">
              <td className="py-4 font-black">{report.codigo}</td>
              <td className="py-4">{report.nombre}</td>
              <td className="py-4">
                <StatusBadge status={report.estado} />
              </td>
              <td className="max-w-[260px] truncate py-4 text-[#aaaaba]">{report.ubicacion}</td>
              <td className="py-4 text-[#aaaaba]">{formatRelativeTime(report.createdAt)}</td>
              <td className="py-4 text-right">
                <button
                  type="button"
                  onClick={() => onDeleteReport(report)}
                  disabled={deletingReportId === report.id}
                  className="inline-flex h-10 items-center gap-2 rounded-lg bg-[#ef4444] px-3 text-sm font-black text-white hover:bg-[#dc2626] disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <Trash2 size={15} />
                  {deletingReportId === report.id ? "Eliminando" : "Eliminar"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {reports.length === 0 && (
        <p className="mt-6 rounded-xl border border-[#24242a] bg-[#09090b] p-4 text-sm text-[#aaaaba]">
          No hay reportes para mostrar.
        </p>
      )}
    </div>
  );
}

function UsersTable({
  users,
  currentUserId,
  deletingUserId,
  onDeleteUser,
}: {
  users: Usuario[];
  currentUserId?: number;
  deletingUserId: number | null;
  onDeleteUser: (user: Usuario) => void;
}) {
  return (
    <div className="mt-7 overflow-x-auto">
      <table className="w-full min-w-[720px] text-left">
        <thead className="text-sm text-[#85858f]">
          <tr className="border-b border-[#24242a]">
            <th className="pb-4">ID</th>
            <th className="pb-4">Nombre</th>
            <th className="pb-4">Email</th>
            <th className="pb-4">Rol</th>
            <th className="pb-4">Registro</th>
            <th className="pb-4 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.idUsuario} className="border-b border-[#1f1f24]">
              <td className="py-4 font-black">#{user.idUsuario}</td>
              <td className="py-4">{user.nombre}</td>
              <td className="py-4 text-[#aaaaba]">{user.email}</td>
              <td className="py-4">
                <span className="rounded-full border border-[#2a2a30] bg-[#101013] px-3 py-1 text-xs font-black text-[#f5c400]">
                  {getRoleLabel(user.idRol)}
                </span>
              </td>
              <td className="py-4 text-[#aaaaba]">{formatAdminDate(user.fechaRegistro)}</td>
              <td className="py-4 text-right">
                <button
                  type="button"
                  onClick={() => onDeleteUser(user)}
                  disabled={deletingUserId === user.idUsuario || currentUserId === user.idUsuario}
                  className="inline-flex h-10 items-center gap-2 rounded-lg bg-[#ef4444] px-3 text-sm font-black text-white hover:bg-[#dc2626] disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <Trash2 size={15} />
                  {deletingUserId === user.idUsuario ? "Eliminando" : "Eliminar"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {users.length === 0 && (
        <p className="mt-6 rounded-xl border border-[#24242a] bg-[#09090b] p-4 text-sm text-[#aaaaba]">
          No hay usuarios para mostrar.
        </p>
      )}
    </div>
  );
}

function filterReports(reports: ReportApiResponse[], query: string) {
  const normalizedQuery = query.trim().toLowerCase();

  if (!normalizedQuery) {
    return reports;
  }

  return reports.filter((report) =>
    [report.codigo, report.nombre, report.ubicacion, report.estado]
      .join(" ")
      .toLowerCase()
      .includes(normalizedQuery)
  );
}

function filterUsers(users: Usuario[], query: string) {
  const normalizedQuery = query.trim().toLowerCase();

  if (!normalizedQuery) {
    return users;
  }

  return users.filter((user) =>
    [user.nombre, user.email, getRoleLabel(user.idRol)]
      .join(" ")
      .toLowerCase()
      .includes(normalizedQuery)
  );
}

function getRoleLabel(idRol: number) {
  if (idRol === 1) {
    return "ADMIN";
  }

  if (idRol === 2) {
    return "DUENO";
  }

  return "CIUDADANO";
}

function formatAdminDate(value: string) {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "Sin fecha";
  }

  return new Intl.DateTimeFormat("es-CL", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(date);
}
