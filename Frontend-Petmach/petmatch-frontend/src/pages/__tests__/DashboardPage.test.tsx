import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { DashboardPage } from "../DashboardPage";
import { renderWithRouter } from "../../test/render";
import { mockReports } from "../../test/testData";

const reportApiMocks = vi.hoisted(() => ({
  getAll: vi.fn(),
}));

vi.mock("../../api/reportApi", () => ({
  reportApi: {
    getAll: reportApiMocks.getAll,
  },
}));

describe("DashboardPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    reportApiMocks.getAll.mockResolvedValue(mockReports);
  });

  it("carga reportes vivos y permite filtrarlos", async () => {
    const user = userEvent.setup();

    renderWithRouter(<DashboardPage />, "/dashboard");

    expect(await screen.findByText("Luna")).toBeInTheDocument();
    expect(screen.getByText("Reportes vivos")).toBeInTheDocument();
    expect(screen.getByText("Avistamientos")).toBeInTheDocument();

    await user.type(screen.getByPlaceholderText("Buscar reporte..."), "Providencia");

    expect(screen.getByText("Luna")).toBeInTheDocument();
    await waitFor(() => expect(screen.queryByText("Max")).not.toBeInTheDocument());
  });

  it("muestra error cuando el servicio de reportes falla", async () => {
    reportApiMocks.getAll.mockRejectedValue(new Error("offline"));

    renderWithRouter(<DashboardPage />, "/dashboard");

    expect(await screen.findByText("No fue posible cargar los reportes en vivo.")).toBeInTheDocument();
  });
});
