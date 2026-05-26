import {
  AlertTriangle,
  CheckCircle,
  Clock,
  Heart,
  MapPin,
  Plus,
  Search,
} from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { reportApi } from "../api/reportApi";
import { ReportCard } from "../components/reports/ReportCard";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import type { ReportApiResponse, ReportStatus, ReporteResumen } from "../types";

export function DashboardPage() {
  const navigate = useNavigate();
  const [reportes, setReportes] = useState<ReporteResumen[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadReports() {
      setLoading(true);
      setError("");

      try {
        const data = await reportApi.getAll();

        if (active) {
          setReportes(data.map(mapReport));
        }
      } catch {
        if (active) {
          setError("No fue posible cargar los reportes en vivo.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    void loadReports();

    return () => {
      active = false;
    };
  }, []);

  const counts = useMemo(
    () => ({
      total: reportes.length,
      perdidos: reportes.filter((reporte) => reporte.estado === "PERDIDO").length,
      refugio: reportes.filter((reporte) => reporte.estado === "EN_REFUGIO").length,
      peligro: reportes.filter((reporte) => reporte.estado === "EN_PELIGRO").length,
    }),
    [reportes]
  );

  const stats = [
    {
      label: "Reportes vivos",
      value: counts.total,
      icon: MapPin,
      color: "text-[#f5c400]",
    },
    {
      label: "Perdidos",
      value: counts.perdidos,
      icon: Heart,
      color: "text-yellow-300",
    },
    {
      label: "En refugio",
      value: counts.refugio,
      icon: CheckCircle,
      color: "text-emerald-400",
    },
    {
      label: "En peligro",
      value: counts.peligro,
      icon: AlertTriangle,
      color: "text-red-400",
    },
  ];

  const reportesFiltrados = useMemo(() => {
    const query = search.trim().toLowerCase();

    if (!query) {
      return reportes;
    }

    return reportes.filter((reporte) => {
      return (
        reporte.codigo.toLowerCase().includes(query) ||
        reporte.nombre.toLowerCase().includes(query) ||
        reporte.ubicacion.toLowerCase().includes(query) ||
        reporte.descripcion.toLowerCase().includes(query)
      );
    });
  }, [reportes, search]);

  const recientes = reportesFiltrados.slice(0, 5);
  const alertasCriticas = reportes.filter((reporte) => reporte.estado === "EN_PELIGRO").slice(0, 2);
  const actividad = reportes.slice(0, 3);

  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="flex flex-col justify-between gap-6 border-b border-[#24242a] pb-9 lg:flex-row lg:items-center">
        <div>
          <h1 className="text-4xl font-black">Panel Principal</h1>
          <p className="mt-3 text-lg text-[#aaaaba]">
            Revisa reportes activos cargados en vivo desde el servicio.
          </p>
        </div>

        <Button onClick={() => navigate("/nuevo-reporte")}>
          <Plus className="mr-2 inline" size={19} />
          Nuevo reporte
        </Button>
      </div>

      <div className="mt-9 grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;

          return (
            <Card key={stat.label} className="p-7">
              <div
                className={`flex h-14 w-14 items-center justify-center rounded-2xl bg-[#0f0f12] ${stat.color}`}
              >
                <Icon size={28} />
              </div>

              <p className="mt-7 font-bold text-[#aaaaba]">{stat.label}</p>
              <h3 className="mt-3 text-4xl font-black">{stat.value}</h3>
            </Card>
          );
        })}
      </div>

      {error && (
        <div className="mt-6 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
          {error}
        </div>
      )}

      <div className="mt-8 grid grid-cols-1 gap-8 xl:grid-cols-[1.2fr_0.8fr]">
        <Card className="p-7">
          <div className="flex items-center justify-between gap-4">
            <h2 className="text-2xl font-black">Reportes recientes</h2>

            <button
              onClick={() => navigate("/reportes")}
              className="font-bold text-[#f5c400]"
            >
              Ver todos
            </button>
          </div>

          <div className="mt-6 flex h-12 items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#09090b] px-4">
            <Search size={20} className="text-[#85858f]" />
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Buscar reporte..."
              className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
            />
          </div>

          <div className="mt-6 space-y-4">
            {loading && (
              <div className="rounded-2xl border border-[#24242a] bg-[#17171b] p-6 text-[#aaaaba]">
                Cargando reportes en vivo...
              </div>
            )}

            {!loading && recientes.length === 0 && (
              <div className="rounded-2xl border border-[#24242a] bg-[#17171b] p-6 text-[#aaaaba]">
                No hay reportes para mostrar.
              </div>
            )}

            {recientes.map((reporte) => (
              <ReportCard key={reporte.id} reporte={reporte} />
            ))}
          </div>
        </Card>

        <div className="space-y-8">
          <Card className="p-7">
            <div className="flex items-center gap-3">
              <AlertTriangle className="text-red-400" />
              <h2 className="text-2xl font-black">Alertas criticas</h2>
            </div>

            <div className="mt-6 space-y-4">
              {alertasCriticas.length === 0 && (
                <p className="text-sm text-[#aaaaba]">
                  No hay reportes en peligro activos.
                </p>
              )}

              {alertasCriticas.map((reporte) => (
                <div
                  key={reporte.id}
                  className="rounded-2xl border border-red-500/20 bg-red-500/10 p-4"
                >
                  <p className="font-black text-red-300">{reporte.nombre}</p>
                  <p className="mt-1 text-sm text-[#aaaaba]">
                    {reporte.descripcion}
                  </p>
                  <p className="mt-2 text-xs text-[#85858f]">
                    {reporte.ubicacion} · {reporte.tiempo}
                  </p>
                </div>
              ))}
            </div>
          </Card>

          <Card className="p-7">
            <div className="flex items-center gap-3">
              <Clock className="text-[#aaaaba]" />
              <h2 className="text-2xl font-black">Actividad reciente</h2>
            </div>

            <div className="mt-6 space-y-4 text-sm text-[#aaaaba]">
              {actividad.length === 0 && <p>Sin actividad reciente.</p>}

              {actividad.map((reporte) => (
                <p key={reporte.id}>
                  {reporte.codigo} · {getStatusLabel(reporte.estado)} · {reporte.tiempo}
                </p>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </section>
  );
}

function mapReport(report: ReportApiResponse): ReporteResumen {
  return {
    id: report.id,
    codigo: report.codigo,
    nombre: report.nombre,
    descripcion: report.descripcion,
    ubicacion: report.ubicacion,
    tiempo: formatRelativeTime(report.createdAt),
    estado: report.estado,
    imagenUrl: report.imagenUrl,
    latitud: report.latitud,
    longitud: report.longitud,
  };
}

function formatRelativeTime(value: string) {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "Fecha no disponible";
  }

  const diffMs = Date.now() - date.getTime();
  const diffMinutes = Math.max(0, Math.floor(diffMs / 60000));

  if (diffMinutes < 1) {
    return "Hace instantes";
  }

  if (diffMinutes < 60) {
    return `Hace ${diffMinutes} min`;
  }

  const diffHours = Math.floor(diffMinutes / 60);

  if (diffHours < 24) {
    return `Hace ${diffHours} h`;
  }

  const diffDays = Math.floor(diffHours / 24);
  return `Hace ${diffDays} d`;
}

function getStatusLabel(status: ReportStatus) {
  if (status === "PERDIDO") {
    return "Perdido";
  }

  if (status === "EN_REFUGIO") {
    return "En refugio";
  }

  return "En peligro";
}
