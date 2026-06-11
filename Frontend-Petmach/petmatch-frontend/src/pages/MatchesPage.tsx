import {
  AlertTriangle,
  CircleCheck,
  Eye,
  Heart,
  MapPin,
  RefreshCw,
  Search,
  X,
} from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { reportApi } from "../api/reportApi";
import { StatusBadge } from "../components/reports/StatusBadge";
import { Button } from "../components/ui/Button";
import type { ReportApiResponse, ReporteResumen } from "../types";
import {
  buildReportMatches,
  formatDistance,
  type MatchCandidate,
  type MatchLevel,
} from "../utils/matchEngine";

type MatchFilter = "TODAS" | MatchLevel;

export function MatchesPage() {
  const [reports, setReports] = useState<ReportApiResponse[]>([]);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState<MatchFilter>("TODAS");
  const [dismissedIds, setDismissedIds] = useState<Set<string>>(new Set());
  const [selectedMatch, setSelectedMatch] = useState<MatchCandidate | null>(null);
  const [resolvingId, setResolvingId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadReports = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await reportApi.getAll();
      setReports(data);
      setDismissedIds(new Set());
    } catch {
      setError("No fue posible cargar los reportes para calcular coincidencias.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadReports();
  }, [loadReports]);

  const allMatches = useMemo(() => buildReportMatches(reports), [reports]);

  const visibleMatches = useMemo(() => {
    const query = search.trim().toLowerCase();

    return allMatches.filter((match) => {
      if (dismissedIds.has(match.id)) {
        return false;
      }

      if (filter !== "TODAS" && match.nivel !== filter) {
        return false;
      }

      if (!query) {
        return true;
      }

      const searchable = [
        match.perdido.codigo,
        match.perdido.nombre,
        match.perdido.ubicacion,
        match.encontrado.codigo,
        match.encontrado.nombre,
        match.encontrado.ubicacion,
      ]
        .join(" ")
        .toLowerCase();

      return searchable.includes(query);
    });
  }, [allMatches, dismissedIds, filter, search]);

  const counts = useMemo(
    () => ({
      TODAS: allMatches.length,
      ALTA: allMatches.filter((match) => match.nivel === "ALTA").length,
      MEDIA: allMatches.filter((match) => match.nivel === "MEDIA").length,
      BAJA: allMatches.filter((match) => match.nivel === "BAJA").length,
    }),
    [allMatches]
  );

  const handleResolveMatch = async (match: MatchCandidate) => {
    setError("");
    setResolvingId(match.id);

    try {
      const updatedReports = await Promise.all([
        reportApi.markFound(match.perdido.id),
        reportApi.markFound(match.encontrado.id),
      ]);

      setReports((current) => {
        const updatesById = new Map(updatedReports.map((report) => [report.id, report]));

        return current.map((report) => updatesById.get(report.id) ?? report);
      });
      setSelectedMatch(null);
    } catch {
      setError("No fue posible confirmar la coincidencia. Intenta nuevamente.");
      await loadReports();
    } finally {
      setResolvingId(null);
    }
  };

  const handleDismissMatch = (match: MatchCandidate) => {
    setDismissedIds((current) => new Set(current).add(match.id));

    if (selectedMatch?.id === match.id) {
      setSelectedMatch(null);
    }
  };

  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="flex flex-col justify-between gap-6 border-b border-[#24242a] pb-8 lg:flex-row lg:items-center">
        <div>
          <div className="flex items-center gap-4">
            <Heart size={38} className="text-[#f5c400]" />
            <h1 className="text-4xl font-black">Posibles Coincidencias</h1>
          </div>

          <p className="mt-4 max-w-3xl text-lg leading-relaxed text-[#b5b5c2]">
            Coincidencias calculadas con reportes reales segun distancia,
            ubicacion, estado y detalles similares.
          </p>
        </div>

        <div className="flex flex-col gap-3 sm:flex-row">
          <div className="flex h-12 w-full items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#17171b] px-4 sm:w-[320px]">
            <Search size={20} className="text-[#85858f]" />
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Buscar por reporte, mascota o zona..."
              className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
            />
          </div>

          <Button type="button" variant="secondary" onClick={() => void loadReports()}>
            <RefreshCw className="mr-2 inline" size={18} />
            Actualizar
          </Button>
        </div>
      </div>

      <div className="mt-6 flex flex-wrap gap-2">
        <FilterButton active={filter === "TODAS"} onClick={() => setFilter("TODAS")}>
          Todas ({counts.TODAS})
        </FilterButton>
        <FilterButton active={filter === "ALTA"} onClick={() => setFilter("ALTA")}>
          Alta ({counts.ALTA})
        </FilterButton>
        <FilterButton active={filter === "MEDIA"} onClick={() => setFilter("MEDIA")}>
          Media ({counts.MEDIA})
        </FilterButton>
        <FilterButton active={filter === "BAJA"} onClick={() => setFilter("BAJA")}>
          Baja ({counts.BAJA})
        </FilterButton>
      </div>

      {error && (
        <div className="mt-6 flex items-center gap-3 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">
          <AlertTriangle size={18} />
          {error}
        </div>
      )}

      {loading && (
        <div className="mt-8 rounded-2xl border border-[#24242a] bg-[#17171b] p-6 text-[#aaaaba]">
          Calculando coincidencias...
        </div>
      )}

      {!loading && visibleMatches.length === 0 && (
        <div className="mt-8 rounded-2xl border border-[#24242a] bg-[#17171b] p-8">
          <h2 className="text-2xl font-black">No hay coincidencias para mostrar</h2>
          <p className="mt-3 max-w-2xl text-[#aaaaba]">
            Se necesitan reportes perdidos y reportes encontrados o en peligro
            para calcular posibles matches.
          </p>
        </div>
      )}

      {!loading && visibleMatches.length > 0 && (
        <div className="mt-8 grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
          {visibleMatches.map((match) => (
            <MatchCard
              key={match.id}
              match={match}
              resolving={resolvingId === match.id}
              onDetails={() => setSelectedMatch(match)}
              onDismiss={() => handleDismissMatch(match)}
              onResolve={() => void handleResolveMatch(match)}
            />
          ))}
        </div>
      )}

      {selectedMatch && (
        <MatchDetailsModal
          match={selectedMatch}
          resolving={resolvingId === selectedMatch.id}
          onClose={() => setSelectedMatch(null)}
          onDismiss={() => handleDismissMatch(selectedMatch)}
          onResolve={() => void handleResolveMatch(selectedMatch)}
        />
      )}
    </section>
  );
}

function MatchCard({
  match,
  resolving,
  onDetails,
  onDismiss,
  onResolve,
}: {
  match: MatchCandidate;
  resolving: boolean;
  onDetails: () => void;
  onDismiss: () => void;
  onResolve: () => void;
}) {
  return (
    <article className="overflow-hidden rounded-2xl border border-[#25252b] bg-[#17171b]">
      <div className="relative grid h-44 grid-cols-2">
        <ReportImage report={match.perdido} label="PERDIDO" tone="lost" />
        <ReportImage report={match.encontrado} label="ENCONTRADO" tone="found" />

        <div className="absolute left-1/2 top-1/2 flex h-14 w-14 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full border border-[#f5c400]/30 bg-[#1a1a1f] text-lg font-black text-[#f5c400]">
          {match.porcentaje}%
        </div>
      </div>

      <div className="p-5">
        <div className="flex items-center justify-between gap-3">
          <span className={getLevelClass(match.nivel)}>{match.nivel}</span>
          <span className="text-sm font-bold text-[#85858f]">
            {formatDistance(match.distanciaKm)}
          </span>
        </div>

        <h2 className="mt-4 text-lg font-black">
          {match.perdido.codigo} con {match.encontrado.codigo}
        </h2>

        <p className="mt-2 line-clamp-2 text-sm leading-relaxed text-[#b8b8c3]">
          {match.razones[0]}
        </p>

        <div className="mt-5 flex gap-2">
          <button
            type="button"
            onClick={onDetails}
            className="inline-flex h-11 flex-1 items-center justify-center gap-2 rounded-xl bg-[#29292f] text-sm font-black text-white transition hover:bg-[#34343b]"
          >
            <Eye size={16} />
            Detalles
          </button>
          <button
            type="button"
            onClick={onResolve}
            disabled={resolving}
            className="inline-flex h-11 flex-1 items-center justify-center gap-2 rounded-xl bg-[#10b981] text-sm font-black text-black transition hover:bg-[#34d399] disabled:cursor-not-allowed disabled:opacity-60"
          >
            <CircleCheck size={16} />
            {resolving ? "Confirmando..." : "Confirmar"}
          </button>
        </div>

        <button
          type="button"
          onClick={onDismiss}
          className="mt-3 h-10 w-full rounded-xl border border-[#2a2a30] text-sm font-bold text-[#aaaaba] transition hover:bg-[#242429] hover:text-white"
        >
          Descartar
        </button>
      </div>
    </article>
  );
}

function MatchDetailsModal({
  match,
  resolving,
  onClose,
  onDismiss,
  onResolve,
}: {
  match: MatchCandidate;
  resolving: boolean;
  onClose: () => void;
  onDismiss: () => void;
  onResolve: () => void;
}) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 px-4 backdrop-blur-sm">
      <div className="max-h-[90vh] w-full max-w-5xl overflow-hidden rounded-2xl border border-[#2a2a30] bg-[#17171b] shadow-2xl">
        <div className="flex items-center justify-between border-b border-[#24242a] px-7 py-5">
          <div>
            <h2 className="text-2xl font-black">Detalle de coincidencia</h2>
            <p className="mt-1 text-sm text-[#aaaaba]">
              {match.porcentaje}% de compatibilidad, {formatDistance(match.distanciaKm)}
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="flex h-10 w-10 items-center justify-center rounded-full bg-[#0f0f12] text-[#9d9daa] hover:text-white"
          >
            <X size={22} />
          </button>
        </div>

        <div className="max-h-[65vh] overflow-y-auto p-7">
          <div className="grid grid-cols-1 gap-5 lg:grid-cols-2">
            <ReportDetail report={match.perdido} title="Reporte perdido" />
            <ReportDetail report={match.encontrado} title="Reporte encontrado" />
          </div>

          <div className="mt-6 rounded-2xl border border-[#24242a] bg-[#09090b] p-5">
            <h3 className="font-black">Razones del match</h3>
            <div className="mt-4 grid gap-3 md:grid-cols-2">
              {match.razones.map((reason) => (
                <div
                  key={reason}
                  className="rounded-xl border border-[#26262d] bg-[#17171b] p-3 text-sm text-[#d6d6df]"
                >
                  {reason}
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="flex flex-col justify-end gap-3 border-t border-[#24242a] px-7 py-5 sm:flex-row">
          <Button type="button" variant="ghost" onClick={onDismiss}>
            Descartar
          </Button>
          <Button type="button" onClick={onResolve} disabled={resolving}>
            <CircleCheck className="mr-2 inline" size={18} />
            {resolving ? "Confirmando..." : "Confirmar coincidencia"}
          </Button>
        </div>
      </div>
    </div>
  );
}

function ReportDetail({ report, title }: { report: ReporteResumen; title: string }) {
  return (
    <article className="overflow-hidden rounded-2xl border border-[#24242a] bg-[#09090b]">
      <img src={report.imagenUrl} alt={report.nombre} className="h-56 w-full object-cover" />

      <div className="p-5">
        <div className="flex items-start justify-between gap-3">
          <div>
            <p className="text-sm font-bold text-[#85858f]">{title}</p>
            <h3 className="mt-1 text-2xl font-black">{report.nombre}</h3>
          </div>
          <StatusBadge status={report.estado} />
        </div>

        <p className="mt-4 text-sm leading-relaxed text-[#c8c8d2]">{report.descripcion}</p>

        <div className="mt-5 space-y-2 text-sm text-[#aaaaba]">
          <p className="flex items-center gap-2">
            <MapPin size={16} />
            {report.ubicacion}
          </p>
          <p className="font-bold text-[#f5c400]">{report.codigo}</p>
        </div>
      </div>
    </article>
  );
}

function ReportImage({
  report,
  label,
  tone,
}: {
  report: ReporteResumen;
  label: string;
  tone: "lost" | "found";
}) {
  return (
    <div className={tone === "found" ? "relative grayscale" : "relative"}>
      <img src={report.imagenUrl} alt={report.nombre} className="h-full w-full object-cover" />
      <span
        className={`absolute top-3 rounded px-2 py-1 text-xs font-black text-black ${
          tone === "lost" ? "left-3 bg-[#f5c400]" : "right-3 bg-[#10b981]"
        }`}
      >
        {label}
      </span>
    </div>
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
      type="button"
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

function getLevelClass(level: MatchLevel) {
  const base = "rounded-full px-3 py-1 text-xs font-black";

  if (level === "ALTA") {
    return `${base} bg-[#10b981]/15 text-[#10b981]`;
  }

  if (level === "MEDIA") {
    return `${base} bg-[#f5c400]/15 text-[#f5c400]`;
  }

  return `${base} bg-[#85858f]/15 text-[#c8c8d2]`;
}
