import { CircleCheck, Clock, MapPin } from "lucide-react";
import type { ReporteResumen } from "../../types";
import { StatusBadge } from "./StatusBadge";

interface ReportCardProps {
  reporte: ReporteResumen;
  onRescued?: (reporte: ReporteResumen) => void;
  rescuing?: boolean;
}

const borderByStatus = {
  PERDIDO: "border-[#f5c400]/30 bg-[#f5c400]/10",
  EN_REFUGIO: "border-[#10b981]/30 bg-[#10b981]/10",
  EN_PELIGRO: "border-[#ef4444]/30 bg-[#ef4444]/10",
  ENCONTRADO: "border-[#60a5fa]/30 bg-[#60a5fa]/10",
};

export function ReportCard({ reporte, onRescued, rescuing = false }: ReportCardProps) {
  return (
    <article
      className={`rounded-2xl border p-3 transition hover:scale-[1.01] ${borderByStatus[reporte.estado]}`}
    >
      <div className="flex gap-4">
        <img
          src={reporte.imagenUrl}
          alt={reporte.nombre}
          className="h-24 w-24 rounded-xl object-cover"
        />

        <div className="min-w-0 flex-1">
          <div className="flex items-start justify-between gap-3">
            <h3 className="truncate text-xl font-black">{reporte.nombre}</h3>
            <div className="flex shrink-0 flex-col items-end gap-2">
              <StatusBadge status={reporte.estado} />

              {onRescued && (
                <button
                  type="button"
                  onClick={() => onRescued(reporte)}
                  disabled={rescuing}
                  className="inline-flex h-9 items-center gap-2 rounded-lg bg-[#10b981] px-3 text-xs font-black text-black transition hover:bg-[#34d399] disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <CircleCheck size={15} />
                  {rescuing ? "Marcando..." : "Rescatado"}
                </button>
              )}
            </div>
          </div>

          <p className="mt-2 line-clamp-1 text-sm text-[#b8b8c3]">
            {reporte.descripcion}
          </p>

          <div className="mt-5 flex flex-wrap items-center justify-between gap-3 text-sm text-[#85858f]">
            <span className="flex items-center gap-1">
              <MapPin size={15} />
              {reporte.ubicacion}
            </span>

            <span className="flex items-center gap-1">
              <Clock size={15} />
              {reporte.tiempo}
            </span>
          </div>
        </div>
      </div>
    </article>
  );
}
