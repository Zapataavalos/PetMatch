import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { formatRelativeTime, mapReport } from "./reportMapper";

describe("reportMapper", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2026-06-04T12:00:00.000Z"));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("formats relative time in minutes, hours and days", () => {
    expect(formatRelativeTime("2026-06-04T11:58:00.000Z")).toBe("Hace 2 min");
    expect(formatRelativeTime("2026-06-04T09:00:00.000Z")).toBe("Hace 3 h");
    expect(formatRelativeTime("2026-06-02T12:00:00.000Z")).toBe("Hace 2 d");
  });

  it("maps API reports to UI summaries", () => {
    expect(
      mapReport({
        id: 10,
        codigo: "REP-010",
        nombre: "Luna",
        descripcion: "Gata blanca",
        ubicacion: "Providencia",
        estado: "PERDIDO",
        imagenUrl: "https://example.com/luna.jpg",
        latitud: -33.43,
        longitud: -70.61,
        createdAt: "2026-06-04T11:59:30.000Z",
      })
    ).toMatchObject({
      id: 10,
      codigo: "REP-010",
      nombre: "Luna",
      tiempo: "Hace instantes",
      estado: "PERDIDO",
    });
  });
});
