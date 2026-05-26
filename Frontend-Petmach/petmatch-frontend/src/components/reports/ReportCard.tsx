import { Clock, MapPin } from "lucide-react";
import type { ReporteResumen } from "../../types";
import { StatusBadge } from "./StatusBadge";

interface ReportCardProps {
  reporte: ReporteResumen;
}

const borderByStatus = {
  PERDIDO: "border-[#f5c400]/30 bg-[#f5c400]/10",
  EN_REFUGIO: "border-[#10b981]/30 bg-[#10b981]/10",
  EN_PELIGRO: "border-[#ef4444]/30 bg-[#ef4444]/10",
};

export function ReportCard({ reporte }: ReportCardProps) {
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
            <StatusBadge status={reporte.estado} />
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