import { AlertTriangle, FileText, Plus, Search } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { useLocation } from "react-router-dom";
import { reportApi } from "../api/reportApi";
import { ReportCard } from "../components/reports/ReportCard";
import { NewReportModal } from "../components/reports/NewReportModal";
import { StatusDot } from "../components/reports/StatusBadge";
import { Button } from "../components/ui/Button";
import { mapReport } from "../utils/reportMapper";
import type { ReportApiResponse, ReportStatus, ReporteResumen } from "../types";

export function ReportsPage() {
  const location = useLocation();
  const [filter, setFilter] = useState<ReportStatus | "TODOS">("TODOS");
  const [modalOpen, setModalOpen] = useState(location.pathname === "/nuevo-reporte");
  const [search, setSearch] = useState("");
  const [reportes, setReportes] = useState<ReporteResumen[]>([]);
  const [rescuingIds, setRescuingIds] = useState<Set<number>>(new Set());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (location.pathname === "/nuevo-reporte") {
      setModalOpen(true);
    }
  }, [location.pathname]);

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
          setError("No fue posible cargar los reportes de mascotas.");
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

  const reportesFiltrados = useMemo(() => {
    const query = search.trim().toLowerCase();

    return reportes.filter((reporte) => {
      const matchesFilter = filter === "TODOS" || reporte.estado === filter;
      const matchesSearch =
        !query ||
        reporte.codigo.toLowerCase().includes(query) ||
        reporte.nombre.toLowerCase().includes(query) ||
        reporte.ubicacion.toLowerCase().includes(query);

      return matchesFilter && matchesSearch;
    });
  }, [filter, reportes, search]);

  const counts = useMemo(
    () => ({
      PERDIDO: reportes.filter((reporte) => reporte.estado === "PERDIDO").length,
      EN_REFUGIO: reportes.filter((reporte) => reporte.estado === "EN_REFUGIO").length,
      EN_PELIGRO: reportes.filter((reporte) => reporte.estado === "EN_PELIGRO").length,
      ENCONTRADO: reportes.filter((reporte) => reporte.estado === "ENCONTRADO").length,
    }),
    [reportes]
  );

  const handleCreated = (report: ReportApiResponse) => {
    setReportes((current) => [mapReport(report), ...current]);
  };

  const handleRescued = async (reporte: ReporteResumen) => {
    setError("");
    setRescuingIds((current) => new Set(current).add(reporte.id));

    try {
      const updated = await reportApi.markFound(reporte.id);
      setReportes((current) =>
        current.map((item) => (item.id === updated.id ? mapReport(updated) : item))
      );
    } catch {
      setError("No fue posible marcar la mascota como encontrada.");
    } finally {
      setRescuingIds((current) => {
        const next = new Set(current);
        next.delete(reporte.id);
        return next;
      });
    }
  };

  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="flex flex-col justify-between gap-6 border-b border-[#24242a] pb-9 lg:flex-row lg:items-center">
        <div>
          <div className="flex items-center gap-4">
            <FileText size={38} className="text-[#f5c400]" />
            <h1 className="text-4xl font-black">Reportes de mascotas</h1>
          </div>

          <p className="mt-4 text-lg text-[#aaaaba]">
            Reportes reales registrados en el servicio de mascotas.
          </p>
        </div>

        <Button onClick={() => setModalOpen(true)}>
          <Plus className="mr-2 inline" size={19} />
          Nuevo reporte
        </Button>
      </div>

      <div className="mt-8 flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">
        <div className="flex h-12 w-full items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#17171b] px-4 lg:w-[420px]">
          <Search size={20} className="text-[#85858f]" />
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Buscar por codigo, mascota o ubicacion..."
            className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
          />
        </div>

        <div className="flex flex-wrap gap-2">
          <FilterButton active={filter === "TODOS"} onClick={() => setFilter("TODOS")}>
            Todos
          </FilterButton>
          <FilterButton active={filter === "PERDIDO"} onClick={() => setFilter("PERDIDO")}>
            <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#f5c400]" />
            Perdidos
          </FilterButton>
          <FilterButton
            active={filter === "EN_REFUGIO"}
            onClick={() => setFilter("EN_REFUGIO")}
          >
            <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#10b981]" />
            En refugio
          </FilterButton>
          <FilterButton
            active={filter === "EN_PELIGRO"}
            onClick={() => setFilter("EN_PELIGRO")}
          >
            <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#ef4444]" />
            En peligro
          </FilterButton>
          <FilterButton
            active={filter === "ENCONTRADO"}
            onClick={() => setFilter("ENCONTRADO")}
          >
            <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#60a5fa]" />
            Encontrados
          </FilterButton>
        </div>
      </div>

      {error && (
        <div className="mt-6 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
          {error}
        </div>
      )}

      <div className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-[1fr_320px]">
        <div className="space-y-5">
          {loading && (
            <div className="rounded-2xl border border-[#24242a] bg-[#17171b] p-6 text-[#aaaaba]">
              Cargando reportes...
            </div>
          )}

          {!loading && reportesFiltrados.length === 0 && (
            <div className="rounded-2xl border border-[#24242a] bg-[#17171b] p-6 text-[#aaaaba]">
              No hay reportes para mostrar.
            </div>
          )}

          {reportesFiltrados.map((reporte) => (
            <ReportCard
              key={reporte.id}
              reporte={reporte}
              onRescued={handleRescued}
              rescuing={rescuingIds.has(reporte.id)}
            />
          ))}
        </div>

        <aside className="h-fit rounded-2xl border border-[#24242a] bg-[#17171b] p-6">
          <div className="flex items-center gap-3">
            <AlertTriangle className="text-[#f5c400]" />
            <h2 className="text-xl font-black">Resumen</h2>
          </div>

          <div className="mt-6 space-y-4 text-sm">
            <SummaryRow status="PERDIDO" label="Perdidos" value={counts.PERDIDO} />
            <SummaryRow status="EN_REFUGIO" label="En refugio" value={counts.EN_REFUGIO} />
            <SummaryRow status="EN_PELIGRO" label="En peligro" value={counts.EN_PELIGRO} />
            <SummaryRow status="ENCONTRADO" label="Encontrados" value={counts.ENCONTRADO} />
          </div>
        </aside>
      </div>

      <NewReportModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onCreated={handleCreated}
      />
    </section>
  );
}

function FilterButton({
  active,
  onClick,
  children,
}: {
  active: boolean;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      onClick={onClick}
      className={`rounded-full border px-4 py-2 text-sm font-bold ${
        active
          ? "border-[#383840] bg-[#242429] text-white"
          : "border-[#2a2a30] text-[#9c9ca8]"
      }`}
    >
      {children}
    </button>
  );
}

function SummaryRow({
  status,
  label,
  value,
}: {
  status: ReportStatus;
  label: string;
  value: number;
}) {
  return (
    <div className="flex items-center justify-between">
      <span className="flex items-center gap-2 text-[#aaaaba]">
        <StatusDot status={status} />
        {label}
      </span>
      <b>{value}</b>
    </div>
  );
}
