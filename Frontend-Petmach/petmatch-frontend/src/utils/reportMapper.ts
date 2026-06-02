import type { ReportApiResponse, ReporteResumen } from "../types";

export function mapReport(report: ReportApiResponse): ReporteResumen {
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

export function formatRelativeTime(value: string) {
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
