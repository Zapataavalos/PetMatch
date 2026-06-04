import { AlertTriangle, CheckCircle, MapPin, Users } from "lucide-react";
import type { ReportApiResponse, ReportStatus, Usuario } from "../types";

export function buildAdminStats(users: Usuario[], reports: ReportApiResponse[]) {
  const activeReports = reports.length;
  const refugeReports = countReportsByStatus(reports, "EN_REFUGIO");
  const criticalReports = countReportsByStatus(reports, "EN_PELIGRO");

  return [
    {
      label: "Usuarios Activos",
      value: String(users.length),
      detail: "Total",
      icon: Users,
      color: "text-[#60a5fa]",
    },
    {
      label: "Reportes Activos",
      value: String(activeReports),
      detail: "Mapa",
      icon: MapPin,
      color: "text-[#a78bfa]",
    },
    {
      label: "En Refugio",
      value: String(refugeReports),
      detail: "Seguimiento",
      icon: CheckCircle,
      color: "text-[#10b981]",
    },
    {
      label: "En Peligro",
      value: String(criticalReports),
      detail: "Prioridad",
      icon: AlertTriangle,
      color: "text-[#ef4444]",
    },
  ];
}

export function buildLastSevenDaysData(reports: ReportApiResponse[], now = new Date()) {
  const days = Array.from({ length: 7 }, (_, index) => {
    const date = new Date(now);
    date.setHours(0, 0, 0, 0);
    date.setDate(date.getDate() - (6 - index));

    return {
      key: date.toISOString().slice(0, 10),
      label: new Intl.DateTimeFormat("es-CL", { weekday: "short" }).format(date),
      count: 0,
      percent: 0,
    };
  });

  reports.forEach((report) => {
    const reportDate = new Date(report.createdAt);

    if (Number.isNaN(reportDate.getTime())) {
      return;
    }

    const key = reportDate.toISOString().slice(0, 10);
    const day = days.find((item) => item.key === key);

    if (day) {
      day.count += 1;
    }
  });

  const maxCount = Math.max(1, ...days.map((day) => day.count));

  return days.map((day) => ({
    ...day,
    percent: Math.round((day.count / maxCount) * 100),
  }));
}

function countReportsByStatus(reports: ReportApiResponse[], status: ReportStatus) {
  return reports.filter((report) => report.estado === status).length;
}
