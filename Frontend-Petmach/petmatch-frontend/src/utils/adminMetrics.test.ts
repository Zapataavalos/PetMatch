import { describe, expect, it } from "vitest";
import type { ReportApiResponse, Usuario } from "../types";
import { buildAdminStats, buildLastSevenDaysData } from "./adminMetrics";

const users: Usuario[] = [
  {
    idUsuario: 1,
    nombre: "Admin",
    email: "admin@test.cl",
    fechaRegistro: "2026-06-01T10:00:00",
    idRol: 1,
  },
  {
    idUsuario: 2,
    nombre: "Usuario",
    email: "usuario@test.cl",
    fechaRegistro: "2026-06-02T10:00:00",
    idRol: 3,
  },
];

const reports: ReportApiResponse[] = [
  createReport(1, "PERDIDO", "2026-06-01T12:00:00.000Z"),
  createReport(2, "EN_REFUGIO", "2026-06-02T12:00:00.000Z"),
  createReport(3, "EN_PELIGRO", "2026-06-02T14:00:00.000Z"),
  createReport(4, "ENCONTRADO", "2026-06-03T14:00:00.000Z"),
];

describe("adminMetrics", () => {
  it("builds dashboard stats from real users and reports", () => {
    const stats = buildAdminStats(users, reports);

    expect(stats.map((stat) => [stat.label, stat.value])).toEqual([
      ["Usuarios Activos", "2"],
      ["Reportes Activos", "4"],
      ["Avistamientos", "2"],
      ["Encontrados", "1"],
    ]);
  });

  it("groups reports for the last seven days", () => {
    const data = buildLastSevenDaysData(
      reports,
      new Date("2026-06-04T18:00:00.000Z")
    );

    expect(data).toHaveLength(7);
    expect(data.find((day) => day.key === "2026-06-01")?.count).toBe(1);
    expect(data.find((day) => day.key === "2026-06-02")?.count).toBe(2);
    expect(data.find((day) => day.key === "2026-06-03")?.count).toBe(1);
    expect(data.find((day) => day.key === "2026-06-02")?.percent).toBe(100);
  });
});

function createReport(
  id: number,
  estado: ReportApiResponse["estado"],
  createdAt: string
): ReportApiResponse {
  return {
    id,
    codigo: `REP-${String(id).padStart(3, "0")}`,
    nombre: "Max",
    descripcion: "Perro con collar rojo",
    ubicacion: "Santiago",
    estado,
    imagenUrl: "https://example.com/pet.jpg",
    latitud: -33.44,
    longitud: -70.66,
    createdAt,
  };
}
