import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MapPage } from "../MapPage";
import { renderWithRouter } from "../../test/render";
import { mockReports } from "../../test/testData";

const reportApiMocks = vi.hoisted(() => ({
  getAll: vi.fn(),
  delete: vi.fn(),
  create: vi.fn(),
  markFound: vi.fn(),
}));

vi.mock("../../api/reportApi", () => ({
  reportApi: {
    getAll: reportApiMocks.getAll,
    delete: reportApiMocks.delete,
    create: reportApiMocks.create,
    markFound: reportApiMocks.markFound,
  },
}));

vi.mock("../../components/map/InteractiveMap", () => ({
  InteractiveMap: ({ onLocationClick }: { onLocationClick?: (coordinates: { latitud: number; longitud: number }) => void }) => (
    <button
      type="button"
      onClick={() => onLocationClick?.({ latitud: -33.4489, longitud: -70.6693 })}
    >
      Mapa interactivo mock
    </button>
  ),
}));

describe("MapPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    reportApiMocks.getAll.mockResolvedValue(mockReports);
    reportApiMocks.delete.mockResolvedValue(undefined);
    reportApiMocks.markFound.mockResolvedValue({ ...mockReports[0], estado: "ENCONTRADO" });
  });

  it("abre el modal de reporte al hacer clic en una ubicacion", async () => {
    const user = userEvent.setup();

    renderWithRouter(<MapPage />, "/mapa");

    expect(await screen.findByText("Luna")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Mapa interactivo mock" }));

    expect(screen.getByRole("heading", { name: "Nuevo reporte" })).toBeInTheDocument();
    expect(screen.getByText(/Coordenadas listas: -33\.448900, -70\.669300/)).toBeInTheDocument();
  });
});
