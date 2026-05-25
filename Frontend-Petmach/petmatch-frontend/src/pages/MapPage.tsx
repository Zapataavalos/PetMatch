import { Minus, Plus, Search } from "lucide-react";
import { useState } from "react";
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

  const pins = [
    {
      top: "32%",
      left: "35%",
      status: "PERDIDO" as ReportStatus,
    },
    {
      top: "25%",
      left: "50%",
      status: "EN_PELIGRO" as ReportStatus,
    },
    {
      top: "64%",
      left: "20%",
      status: "EN_REFUGIO" as ReportStatus,
    },
    {
      top: "55%",
      left: "65%",
      status: "EN_REFUGIO" as ReportStatus,
    },
    {
      top: "38%",
      left: "80%",
      status: "PERDIDO" as ReportStatus,
    },
  ];

  return (
    <section className="relative h-[calc(100vh-74px)] overflow-hidden bg-[#050506]">
      <div className="absolute inset-0 right-[28rem]">
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.04)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.04)_1px,transparent_1px)] bg-[size:48px_48px]" />
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.02)_1px,transparent_1px)] bg-[size:16px_16px]" />

        <div className="absolute left-[10%] top-[20%] h-[2px] w-[80%] rotate-12 bg-[#1c1c21]" />
        <div className="absolute left-0 top-[60%] h-[8px] w-full -rotate-2 bg-[#151519]" />
        <div className="absolute left-[48%] top-[5%] h-[8px] w-[45%] rotate-[-45deg] bg-[#131318]" />

        {pins.map((pin, index) => (
          <button
            key={index}
            className="absolute flex h-9 w-9 items-center justify-center rounded-full bg-black/40"
            style={{ top: pin.top, left: pin.left }}
          >
            <span
              className={`h-5 w-5 rounded-full shadow-[0_0_22px_currentColor] ${
                pin.status === "PERDIDO"
                  ? "bg-[#f5c400] text-[#f5c400]"
                  : pin.status === "EN_REFUGIO"
                  ? "bg-[#10b981] text-[#10b981]"
                  : "bg-[#ef4444] text-[#ef4444]"
              }`}
            />
          </button>
        ))}

        <div className="absolute bottom-5 left-5 rounded-2xl border border-[#2c2c32] bg-[#1b1b20]/90 p-4 backdrop-blur">
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

        <div className="absolute bottom-5 right-5 space-y-3">
          <button className="flex h-12 w-12 items-center justify-center rounded-xl border border-[#2c2c32] bg-[#1b1b20] text-white">
            <Plus />
          </button>
          <button className="flex h-12 w-12 items-center justify-center rounded-xl border border-[#2c2c32] bg-[#1b1b20] text-white">
            <Minus />
          </button>
        </div>
      </div>

      <aside className="absolute bottom-0 right-0 top-0 w-[28rem] border-l border-[#202025] bg-[#17171b]">
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

        <div className="space-y-4 overflow-y-auto p-5">
          {reportesFiltrados.map((reporte) => (
            <ReportCard key={reporte.id} reporte={reporte} />
          ))}
        </div>
      </aside>

      <button
        onClick={() => setModalOpen(true)}
        className="fixed right-8 top-4 z-50 hidden h-11 items-center gap-2 rounded-xl bg-[#f5c400] px-5 font-black text-black shadow-[0_0_24px_rgba(245,196,0,0.20)] hover:bg-[#ffd21a] lg:flex"
      >
        <Plus size={19} />
        Nuevo Reporte
      </button>

      <NewReportModal open={modalOpen} onClose={() => setModalOpen(false)} />
    </section>
  );
}