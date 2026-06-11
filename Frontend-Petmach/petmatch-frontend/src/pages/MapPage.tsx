import { MapPin, Search } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { reportApi } from "../api/reportApi";
import { InteractiveMap } from "../components/map/InteractiveMap";
import { NewReportModal } from "../components/reports/NewReportModal";
import { ReportCard } from "../components/reports/ReportCard";
import { StatusDot } from "../components/reports/StatusBadge";
import type { Coordinates, ReportApiResponse, ReportStatus, ReporteResumen } from "../types";
import { mapReport } from "../utils/reportMapper";

export function MapPage() {
  const [filter, setFilter] = useState<ReportStatus | "TODOS">("TODOS");
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedLocation, setSelectedLocation] = useState<Coordinates | null>(null);
  const [search, setSearch] = useState("");
  const [reportes, setReportes] = useState<ReporteResumen[]>([]);
  const [rescuingIds, setRescuingIds] = useState<Set<number>>(new Set());
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
          setError("No fue posible cargar los reportes en el mapa.");
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

  const handleCreated = (report: ReportApiResponse) => {
    setReportes((current) => [mapReport(report), ...current]);
    setSelectedLocation(null);
  };

  const handleLocationClick = (coordinates: Coordinates) => {
    setSelectedLocation(coordinates);
    setModalOpen(true);
  };

  const handleRescued = async (reporte: ReporteResumen) => {
    setError("");
    setRescuingIds((current) => new Set(current).add(reporte.id));

    try {
      const updatedReport = await reportApi.markFound(reporte.id);
      setReportes((current) =>
        current.map((item) => (item.id === reporte.id ? mapReport(updatedReport) : item))
      );
    } catch {
      setError("No fue posible marcar el reporte como rescatado.");
    } finally {
      setRescuingIds((current) => {
        const next = new Set(current);
        next.delete(reporte.id);
        return next;
      });
    }
  };

  return (
    <section className="relative h-[calc(100vh-74px)] overflow-hidden bg-[#050506]">
      <div className="absolute inset-0 right-[28rem]">
        <InteractiveMap
          reportes={reportesFiltrados}
          onRescued={handleRescued}
          rescuingIds={rescuingIds}
          selectedLocation={selectedLocation}
          onLocationClick={handleLocationClick}
        />

        <div className="absolute left-5 top-5 z-[500] flex max-w-sm items-center gap-3 rounded-2xl border border-[#f5c400]/30 bg-[#17171b]/95 p-4 text-sm font-bold text-white backdrop-blur">
          <MapPin size={20} className="shrink-0 text-[#f5c400]" />
          Haz clic en una ubicacion del mapa para crear un reporte ahi.
        </div>

        <div className="absolute bottom-5 left-5 z-[500] rounded-2xl border border-[#2c2c32] bg-[#1b1b20]/95 p-4 backdrop-blur">
          <h3 className="mb-3 text-sm font-black text-[#a8a8b3]">ESTADO</h3>

          <div className="space-y-3 text-sm text-white">
            <div className="flex items-center gap-3">
              <StatusDot status="PERDIDO" />
              Mascota Perdida
            </div>

            <div className="flex items-center gap-3">
              <StatusDot status="EN_REFUGIO" />
              En clínica/refugio
            </div>

            <div className="flex items-center gap-3">
              <StatusDot status="EN_PELIGRO" />
              En peligro
            </div>

            <div className="flex items-center gap-3">
              <StatusDot status="ENCONTRADO" />
              Encontrada
            </div>
          </div>
        </div>
      </div>

      <aside className="absolute bottom-0 right-0 top-0 z-10 w-[28rem] border-l border-[#202025] bg-[#17171b]">
        <div className="border-b border-[#24242a] p-5">
          <h1 className="text-2xl font-black">Reportes Recientes</h1>

          <div className="mt-5 flex h-12 items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#09090b] px-4">
            <Search size={20} className="text-[#85858f]" />
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Buscar por zona o raza..."
              className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
            />
          </div>

          <div className="mt-5 flex flex-wrap gap-2">
            <button
              onClick={() => setFilter("TODOS")}
              className={`rounded-full border px-4 py-2 text-sm font-bold ${
                filter === "TODOS"
                  ? "border-[#383840] bg-[#242429] text-white"
                  : "border-[#2a2a30] text-[#9c9ca8]"
              }`}
            >
              Todos
            </button>

            <button
              onClick={() => setFilter("PERDIDO")}
              className={`rounded-full border px-4 py-2 text-sm font-bold ${
                filter === "PERDIDO"
                  ? "border-[#f5c400]/40 bg-[#f5c400]/10 text-[#f5c400]"
                  : "border-[#2a2a30] text-[#9c9ca8]"
              }`}
            >
              <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#f5c400]" />
              Perdidos
            </button>

            <button
              onClick={() => setFilter("EN_REFUGIO")}
              className={`rounded-full border px-4 py-2 text-sm font-bold ${
                filter === "EN_REFUGIO"
                  ? "border-[#10b981]/40 bg-[#10b981]/10 text-[#10b981]"
                  : "border-[#2a2a30] text-[#9c9ca8]"
              }`}
            >
              <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#10b981]" />
              En refugio
            </button>

            <button
              onClick={() => setFilter("EN_PELIGRO")}
              className={`rounded-full border px-4 py-2 text-sm font-bold ${
                filter === "EN_PELIGRO"
                  ? "border-[#ef4444]/40 bg-[#ef4444]/10 text-[#ef4444]"
                  : "border-[#2a2a30] text-[#9c9ca8]"
              }`}
            >
              <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#ef4444]" />
              En peligro
            </button>

            <button
              onClick={() => setFilter("ENCONTRADO")}
              className={`rounded-full border px-4 py-2 text-sm font-bold ${
                filter === "ENCONTRADO"
                  ? "border-[#60a5fa]/40 bg-[#60a5fa]/10 text-[#60a5fa]"
                  : "border-[#2a2a30] text-[#9c9ca8]"
              }`}
            >
              <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#60a5fa]" />
              Encontrados
            </button>
          </div>
        </div>

        {error && (
          <div className="mx-5 mt-5 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
            {error}
          </div>
        )}

        <div className="h-[calc(100vh-74px-180px)] space-y-4 overflow-y-auto p-5">
          {loading && (
            <div className="rounded-xl border border-[#24242a] bg-[#09090b] p-4 text-sm text-[#aaaaba]">
              Cargando reportes...
            </div>
          )}

          {!loading && reportesFiltrados.length === 0 && (
            <div className="rounded-xl border border-[#24242a] bg-[#09090b] p-4 text-sm text-[#aaaaba]">
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
      </aside>

      <NewReportModal
        open={modalOpen}
        selectedLocation={selectedLocation}
        onClose={() => setModalOpen(false)}
        onCreated={handleCreated}
      />

      <button
        onClick={() => {
          setSelectedLocation(null);
          setModalOpen(true);
        }}
        className="fixed bottom-8 right-[30rem] z-[600] rounded-xl bg-[#f5c400] px-6 py-4 font-black text-black shadow-[0_0_24px_rgba(245,196,0,0.25)] transition hover:bg-[#ffd21a]"
      >
        Nuevo Reporte
      </button>
    </section>
  );
}
