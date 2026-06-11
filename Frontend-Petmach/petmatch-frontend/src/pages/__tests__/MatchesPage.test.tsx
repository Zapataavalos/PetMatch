import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MatchesPage } from "../MatchesPage";
import { renderWithRouter } from "../../test/render";
import type { ReportApiResponse } from "../../types";

const reportApiMocks = vi.hoisted(() => ({
  getAll: vi.fn(),
  markFound: vi.fn(),
  delete: vi.fn(),
}));

vi.mock("../../api/reportApi", () => ({
  reportApi: {
    getAll: reportApiMocks.getAll,
    markFound: reportApiMocks.markFound,
    delete: reportApiMocks.delete,
  },
}));

describe("MatchesPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    reportApiMocks.getAll.mockResolvedValue(matchReports);
    reportApiMocks.markFound.mockImplementation((id: number) =>
      Promise.resolve({
        ...matchReports.find((report) => report.id === id),
        estado: "ENCONTRADO",
      })
    );
    reportApiMocks.delete.mockResolvedValue(undefined);
  });

  it("muestra coincidencias y confirma la mascota como encontrada", async () => {
    const user = userEvent.setup();

    renderWithRouter(<MatchesPage />, "/coincidencias");

    expect(await screen.findByText("REP-001 con REP-002")).toBeInTheDocument();
    expect(screen.getByText("ENCONTRADO")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Confirmar" }));

    await waitFor(() => {
      expect(reportApiMocks.markFound).toHaveBeenCalledWith(1);
      expect(reportApiMocks.markFound).toHaveBeenCalledWith(2);
    });
    expect(reportApiMocks.delete).not.toHaveBeenCalled();

    await waitFor(() =>
      expect(screen.queryByText("REP-001 con REP-002")).not.toBeInTheDocument()
    );
  });
});

const matchReports: ReportApiResponse[] = [
  {
    id: 1,
    codigo: "REP-001",
    nombre: "Max",
    descripcion: "Perro cafe con collar rojo perdido cerca del parque.",
    ubicacion: "Santiago Centro",
    estado: "PERDIDO",
    imagenUrl: "https://example.com/max-lost.jpg",
    latitud: -33.4489,
    longitud: -70.6693,
    createdAt: "2026-06-04T12:00:00.000Z",
  },
  {
    id: 2,
    codigo: "REP-002",
    nombre: "Desconocido",
    descripcion: "Perro cafe con collar rojo resguardado por una vecina.",
    ubicacion: "Santiago Centro",
    estado: "EN_REFUGIO",
    imagenUrl: "https://example.com/max-found.jpg",
    latitud: -33.449,
    longitud: -70.669,
    createdAt: "2026-06-04T12:30:00.000Z",
  },
];
