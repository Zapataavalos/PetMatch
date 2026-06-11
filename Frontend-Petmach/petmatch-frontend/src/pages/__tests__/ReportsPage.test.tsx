import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ReportsPage } from "../ReportsPage";
import { renderWithRouter } from "../../test/render";
import { mockReports } from "../../test/testData";

const reportApiMocks = vi.hoisted(() => ({
  getAll: vi.fn(),
  delete: vi.fn(),
  markFound: vi.fn(),
}));

vi.mock("../../api/reportApi", () => ({
  reportApi: {
    getAll: reportApiMocks.getAll,
    delete: reportApiMocks.delete,
    markFound: reportApiMocks.markFound,
  },
}));

describe("ReportsPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    reportApiMocks.getAll.mockResolvedValue(mockReports);
    reportApiMocks.delete.mockResolvedValue(undefined);
    reportApiMocks.markFound.mockResolvedValue({ ...mockReports[0], estado: "ENCONTRADO" });
  });

  it("lista reportes reales y filtra por estado", async () => {
    const user = userEvent.setup();

    renderWithRouter(<ReportsPage />, "/reportes");

    expect(await screen.findByText("Luna")).toBeInTheDocument();
    expect(screen.getByText("Max")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /En peligro/i }));

    expect(screen.getByText("Nala")).toBeInTheDocument();
    expect(screen.queryByText("Luna")).not.toBeInTheDocument();
  });

  it("marca un reporte como rescatado en el servicio", async () => {
    const user = userEvent.setup();

    renderWithRouter(<ReportsPage />, "/reportes");

    expect(await screen.findByText("Luna")).toBeInTheDocument();

    await user.click(screen.getAllByRole("button", { name: "Rescatado" })[0]);

    await waitFor(() => expect(reportApiMocks.markFound).toHaveBeenCalledWith(1));
    expect(screen.queryByText("Luna")).not.toBeInTheDocument();
  });
});
