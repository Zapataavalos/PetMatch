import { describe, expect, it } from "vitest";
import type { ReportApiResponse } from "../types";
import { buildReportMatches, formatDistance } from "./matchEngine";

describe("matchEngine", () => {
  it("formats distances for meters and kilometers", () => {
    expect(formatDistance(0.42)).toBe("420 m");
    expect(formatDistance(3.25)).toBe("3.3 km");
    expect(formatDistance(Number.NaN)).toBe("Distancia no disponible");
  });

  it("builds likely matches between lost and found reports", () => {
    const matches = buildReportMatches([
      createReport(1, "PERDIDO", "Max", "Perro cafe con collar rojo", -33.4489, -70.6693),
      createReport(2, "EN_REFUGIO", "Desconocido", "Perro cafe con collar rojo", -33.449, -70.669),
      createReport(3, "EN_PELIGRO", "Gata", "Gata negra", -33.1, -70.1),
    ]);

    expect(matches[0]).toMatchObject({
      id: "1-2",
      nivel: "ALTA",
    });
    expect(matches[0].porcentaje).toBeGreaterThanOrEqual(75);
  });
});

function createReport(
  id: number,
  estado: ReportApiResponse["estado"],
  nombre: string,
  descripcion: string,
  latitud: number,
  longitud: number
): ReportApiResponse {
  return {
    id,
    codigo: `REP-${String(id).padStart(3, "0")}`,
    nombre,
    descripcion,
    ubicacion: "Santiago Centro",
    estado,
    imagenUrl: "https://example.com/pet.jpg",
    latitud,
    longitud,
    createdAt: "2026-06-04T12:00:00.000Z",
  };
}
