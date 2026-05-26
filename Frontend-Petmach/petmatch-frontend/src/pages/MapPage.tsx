import { Search } from "lucide-react";
import { useState } from "react";
import { InteractiveMap } from "../components/map/InteractiveMap";
import { NewReportModal } from "../components/reports/NewReportModal";
import { ReportCard } from "../components/reports/ReportCard";
import { StatusDot } from "../components/reports/StatusBadge";
import { mockReportes } from "../data/mockData";
import type { ReportStatus } from "../types";

export function MapPage() {
  const [filter, setFilter] = useState<ReportStatus | "TODOS">("TODOS");
  const [modalOpen, setModalOpen] = useState(false);

  const reportesFiltrados =
    filter === "TODOS"
      ? mockReportes
      : mockReportes.filter((reporte) => reporte.estado === filter);

  return (
    <section className="relative h-[calc(100vh-74px)] overflow-hidden bg-[#050506]">
      <div className="absolute inset-0 right-[28rem]">
        <InteractiveMap reportes={reportesFiltrados} />

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
          </div>
        </div>
      </div>

      <aside className="absolute bottom-0 right-0 top-0 z-10 w-[28rem] border-l border-[#202025] bg-[#17171b]">
        <div className="border-b border-[#24242a] p-5">
          <h1 className="text-2xl font-black">Reportes Recientes</h1>

          <div className="mt-5 flex h-12 items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#09090b] px-4">
            <Search size={20} className="text-[#85858f]" />
            <input
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
          </div>
        </div>

        <div className="h-[calc(100vh-74px-180px)] space-y-4 overflow-y-auto p-5">
          {reportesFiltrados.map((reporte) => (
            <ReportCard key={reporte.id} reporte={reporte} />
          ))}
        </div>
      </aside>

      <NewReportModal open={modalOpen} onClose={() => setModalOpen(false)} />

      <button
        onClick={() => setModalOpen(true)}
        className="fixed bottom-8 right-[30rem] z-[600] rounded-xl bg-[#f5c400] px-6 py-4 font-black text-black shadow-[0_0_24px_rgba(245,196,0,0.25)] transition hover:bg-[#ffd21a]"
      >
        Nuevo Reporte
      </button>
    </section>
  );
}