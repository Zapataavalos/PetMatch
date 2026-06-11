import type { ReportStatus } from "../../types";

interface StatusConfig {
  label: string;
  className: string;
  dot: string;
}

const statusConfig = {
  PERDIDO: {
    label: "PERDIDO",
    className: "bg-[#f5c400]/10 text-[#f5c400]",
    dot: "bg-[#f5c400]",
  },
  EN_REFUGIO: {
    label: "EN REFUGIO",
    className: "bg-[#10b981]/10 text-[#10b981]",
    dot: "bg-[#10b981]",
  },
  EN_PELIGRO: {
    label: "EN PELIGRO",
    className: "bg-[#ef4444]/10 text-[#ef4444]",
    dot: "bg-[#ef4444]",
  },
  ENCONTRADO: {
    label: "ENCONTRADO",
    className: "bg-[#60a5fa]/10 text-[#60a5fa]",
    dot: "bg-[#60a5fa]",
  },
} satisfies Record<ReportStatus, StatusConfig>;

const fallbackStatusConfig: StatusConfig = {
  label: "SIN ESTADO",
  className: "bg-[#85858f]/10 text-[#c7c7d1]",
  dot: "bg-[#85858f]",
};

function getStatusConfig(status: ReportStatus): StatusConfig {
  return statusConfig[status] ?? fallbackStatusConfig;
}

export function StatusBadge({ status }: { status: ReportStatus }) {
  const config = getStatusConfig(status);

  return (
    <span
      className={`inline-flex items-center rounded-md px-2 py-1 text-xs font-black ${config.className}`}
    >
      {config.label}
    </span>
  );
}

export function StatusDot({ status }: { status: ReportStatus }) {
  const config = getStatusConfig(status);

  return <span className={`h-3 w-3 rounded-full ${config.dot}`} />;
}
