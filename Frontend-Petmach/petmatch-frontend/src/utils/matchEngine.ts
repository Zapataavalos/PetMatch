import type { ReportApiResponse, ReporteResumen } from "../types";
import { mapReport } from "./reportMapper";

export type MatchLevel = "ALTA" | "MEDIA" | "BAJA";

export interface MatchCandidate {
  id: string;
  perdido: ReporteResumen;
  encontrado: ReporteResumen;
  porcentaje: number;
  nivel: MatchLevel;
  distanciaKm: number;
  razones: string[];
}

const genericPetNames = new Set([
  "DESCONOCIDO",
  "DESCONOCIDA",
  "SIN NOMBRE",
  "MASCOTA SIN NOMBRE",
]);

const stopWords = new Set([
  "UNA",
  "UNO",
  "CON",
  "SIN",
  "DEL",
  "LAS",
  "LOS",
  "POR",
  "PARA",
  "CERCA",
  "MASCOTA",
  "PERRO",
  "PERRA",
  "GATO",
  "GATA",
  "COLOR",
  "COLLAR",
  "ZONA",
  "ACTUAL",
  "UBICACION",
]);

export function buildReportMatches(reports: ReportApiResponse[]) {
  const summaries = reports.map(mapReport);
  const perdidos = summaries.filter((report) => report.estado === "PERDIDO");
  const encontrados = summaries.filter((report) => report.estado !== "PERDIDO");

  return perdidos
    .flatMap((perdido) =>
      encontrados.map((encontrado) => buildMatch(perdido, encontrado))
    )
    .filter((match) => match.porcentaje >= 25)
    .sort((a, b) => {
      if (b.porcentaje !== a.porcentaje) {
        return b.porcentaje - a.porcentaje;
      }

      return a.distanciaKm - b.distanciaKm;
    });
}

export function formatDistance(distanceKm: number) {
  if (!Number.isFinite(distanceKm)) {
    return "Distancia no disponible";
  }

  if (distanceKm < 1) {
    return `${Math.round(distanceKm * 1000)} m`;
  }

  return `${distanceKm.toFixed(distanceKm < 10 ? 1 : 0)} km`;
}

function buildMatch(perdido: ReporteResumen, encontrado: ReporteResumen): MatchCandidate {
  const distanciaKm = getDistanceKm(perdido, encontrado);
  const razones: string[] = [];
  let score = 0;

  const distanceScore = getDistanceScore(distanciaKm);
  score += distanceScore;

  if (Number.isFinite(distanciaKm)) {
    razones.push(`Reportes a ${formatDistance(distanciaKm)} de distancia`);
  }

  const sharedLocation = getSharedTokens(perdido.ubicacion, encontrado.ubicacion);

  if (sharedLocation.length > 0) {
    score += Math.min(15, sharedLocation.length * 5);
    razones.push(`Misma zona: ${sharedLocation.slice(0, 3).join(", ")}`);
  }

  const textOverlap = getSharedTokens(
    `${perdido.nombre} ${perdido.descripcion}`,
    `${encontrado.nombre} ${encontrado.descripcion}`
  );

  if (textOverlap.length > 0) {
    score += Math.min(25, textOverlap.length * 6);
    razones.push(`Detalles similares: ${textOverlap.slice(0, 4).join(", ")}`);
  }

  if (hasUsefulName(perdido.nombre) && namesAreRelated(perdido, encontrado)) {
    score += 14;
    razones.push(`Nombre compatible: ${perdido.nombre}`);
  }

  if (encontrado.estado === "EN_REFUGIO") {
    score += 10;
    razones.push("El reporte encontrado esta en refugio o clinica");
  } else if (encontrado.estado === "EN_PELIGRO") {
    score += 8;
    razones.push("El reporte encontrado requiere atencion");
  }

  const porcentaje = Math.max(25, Math.min(99, Math.round(score)));

  return {
    id: `${perdido.id}-${encontrado.id}`,
    perdido,
    encontrado,
    porcentaje,
    nivel: getMatchLevel(porcentaje),
    distanciaKm,
    razones: razones.length > 0 ? razones : ["Coincidencia por datos disponibles"],
  };
}

function getDistanceKm(a: ReporteResumen, b: ReporteResumen) {
  const earthRadiusKm = 6371;
  const lat1 = toRadians(a.latitud);
  const lat2 = toRadians(b.latitud);
  const deltaLat = toRadians(b.latitud - a.latitud);
  const deltaLng = toRadians(b.longitud - a.longitud);
  const haversine =
    Math.sin(deltaLat / 2) ** 2 +
    Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLng / 2) ** 2;

  return earthRadiusKm * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
}

function getDistanceScore(distanceKm: number) {
  if (!Number.isFinite(distanceKm)) {
    return 0;
  }

  if (distanceKm <= 0.5) {
    return 45;
  }

  if (distanceKm <= 2) {
    return 39;
  }

  if (distanceKm <= 5) {
    return 32;
  }

  if (distanceKm <= 10) {
    return 24;
  }

  if (distanceKm <= 25) {
    return 14;
  }

  if (distanceKm <= 60) {
    return 8;
  }

  return 2;
}

function getMatchLevel(score: number): MatchLevel {
  if (score >= 75) {
    return "ALTA";
  }

  if (score >= 50) {
    return "MEDIA";
  }

  return "BAJA";
}

function namesAreRelated(perdido: ReporteResumen, encontrado: ReporteResumen) {
  const lostName = normalizeToken(perdido.nombre);
  const foundText = normalizeText(`${encontrado.nombre} ${encontrado.descripcion}`);

  return foundText.split(" ").includes(lostName);
}

function hasUsefulName(name: string) {
  const normalized = normalizeText(name);
  return normalized.length > 2 && !genericPetNames.has(normalized);
}

function getSharedTokens(left: string, right: string) {
  const leftTokens = new Set(toTokens(left));
  const rightTokens = new Set(toTokens(right));

  return [...leftTokens].filter((token) => rightTokens.has(token));
}

function toTokens(value: string) {
  return normalizeText(value)
    .split(" ")
    .filter((token) => token.length > 2 && !stopWords.has(token));
}

function normalizeText(value: string) {
  return value
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-zA-Z0-9]+/g, " ")
    .trim()
    .toUpperCase();
}

function normalizeToken(value: string) {
  return normalizeText(value).split(" ")[0] ?? "";
}

function toRadians(value: number) {
  return (value * Math.PI) / 180;
}
