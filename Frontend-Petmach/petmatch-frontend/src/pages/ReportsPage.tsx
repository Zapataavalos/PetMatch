import { AlertTriangle, FileText, Plus, Search } from "lucide-react";
import { useState } from "react";
import { Button } from "../components/ui/Button";
import { ReportCard } from "../components/reports/ReportCard";
import { StatusDot } from "../components/reports/StatusBadge";
import { NewReportModal } from "../components/reports/NewReportModal";
import { mockReportes } from "../data/mockData";
import type { ReportStatus } from "../types";

export function ReportsPage() {
  const [filter, setFilter] = useState<ReportStatus | "TODOS">("TODOS");
  const [modalOpen, setModalOpen] = useState(false);

  const reportesFiltrados =
    filter === "TODOS"
      ? mockReportes
      : mockReportes.filter((reporte) => reporte.estado === filter);

  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="flex flex-col justify-between gap-6 border-b border-[#24242a] pb-9 lg:flex-row lg:items-center">
        <div>
          <div className="flex items-center gap-4">
            <FileText size={38} className="text-[#f5c400]" />
            <h1 className="text-4xl font-black">Reportes</h1>
          </div>

          <p className="mt-4 text-lg text-[#aaaaba]">
            Consulta, filtra y administra reportes de mascotas perdidas,
            encontradas o en peligro.
          </p>
        </div>

        <Button onClick={() => setModalOpen(true)}>
          <Plus className="mr-2 inline" size={19} />
          Nuevo Reporte
        </Button>
      </div>

      <div className="mt-8 flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">
        <div className="flex h-12 w-full items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#17171b] px-4 lg:w-[420px]">
          <Search size={20} className="text-[#85858f]" />
          <input
            placeholder="Buscar por código, mascota o ubicación..."
            className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
          />
        </div>

        <div className="flex flex-wrap gap-2">
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
            className="rounded-full border border-[#2a2a30] px-4 py-2 text-sm font-bold text-[#9c9ca8]"
          >
            <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#f5c400]" />
            Perdidos
          </button>

          <button
            onClick={() => setFilter("EN_REFUGIO")}
            className="rounded-full border border-[#2a2a30] px-4 py-2 text-sm font-bold text-[#9c9ca8]"
          >
            <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#10b981]" />
            En refugio
          </button>

          <button
            onClick={() => setFilter("EN_PELIGRO")}
            className="rounded-full border border-[#2a2a30] px-4 py-2 text-sm font-bold text-[#9c9ca8]"
          >
            <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#ef4444]" />
            En peligro
          </button>
        </div>
      </div>

      <div className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-[1fr_320px]">
        <div className="space-y-5">
          {reportesFiltrados.map((reporte) => (
            <ReportCard key={reporte.id} reporte={reporte} />
          ))}
        </div>

        <aside className="h-fit rounded-2xl border border-[#24242a] bg-[#17171b] p-6">
          <div className="flex items-center gap-3">
            <AlertTriangle className="text-[#f5c400]" />
            <h2 className="text-xl font-black">Resumen</h2>
          </div>

          <div className="mt-6 space-y-4 text-sm">
            <div className="flex items-center justify-between">
              <span className="flex items-center gap-2 text-[#aaaaba]">
                <StatusDot status="PERDIDO" />
                Perdidos
              </span>
              <b>1</b>
            </div>

            <div className="flex items-center justify-between">
              <span className="flex items-center gap-2 text-[#aaaaba]">
                <StatusDot status="EN_REFUGIO" />
                En refugio
              </span>
              <b>1</b>
            </div>

            <div className="flex items-center justify-between">
              <span className="flex items-center gap-2 text-[#aaaaba]">
                <StatusDot status="EN_PELIGRO" />
                En peligro
              </span>
              <b>1</b>
            </div>
          </div>
        </aside>
      </div>

      <NewReportModal open={modalOpen} onClose={() => setModalOpen(false)} />
    </section>
  );
}