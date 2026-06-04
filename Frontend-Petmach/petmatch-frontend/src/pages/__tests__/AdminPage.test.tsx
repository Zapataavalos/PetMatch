import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { AdminPage } from "../AdminPage";
import { renderWithRouter } from "../../test/render";
import { mockAuthUser, mockReports, mockUsers } from "../../test/testData";

const authMocks = vi.hoisted(() => ({
  useAuth: vi.fn(),
}));

const reportApiMocks = vi.hoisted(() => ({
  getAll: vi.fn(),
  delete: vi.fn(),
}));

const userApiMocks = vi.hoisted(() => ({
  getAll: vi.fn(),
  delete: vi.fn(),
}));

vi.mock("../../auth/useAuth", () => ({
  useAuth: authMocks.useAuth,
}));

vi.mock("../../api/reportApi", () => ({
  reportApi: {
    getAll: reportApiMocks.getAll,
    delete: reportApiMocks.delete,
  },
}));

vi.mock("../../api/userApi", () => ({
  userApi: {
    getAll: userApiMocks.getAll,
    delete: userApiMocks.delete,
  },
}));

describe("AdminPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    authMocks.useAuth.mockReturnValue({ user: mockAuthUser });
    reportApiMocks.getAll.mockResolvedValue(mockReports);
    reportApiMocks.delete.mockResolvedValue(undefined);
    userApiMocks.getAll.mockResolvedValue(mockUsers);
    userApiMocks.delete.mockResolvedValue(undefined);
    vi.spyOn(window, "confirm").mockReturnValue(true);
  });

  it("carga metricas y datos reales del panel admin", async () => {
    renderWithRouter(<AdminPage />, "/admin");

    expect(await screen.findByText("SISTEMA OPERATIVO")).toBeInTheDocument();
    expect(screen.getByText("Panel de Administracion")).toBeInTheDocument();
    expect(screen.getByText("Reportes Recientes")).toBeInTheDocument();
    expect(screen.getByText("REP-001")).toBeInTheDocument();
  });

  it("permite buscar y eliminar usuarios desde la pestaña admin", async () => {
    const user = userEvent.setup();

    renderWithRouter(<AdminPage />, "/admin");

    expect(await screen.findByText("SISTEMA OPERATIVO")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Usuarios" }));
    await user.type(screen.getByPlaceholderText("Buscar usuario..."), "Pedro");

    expect(screen.getByText("Pedro Rojas")).toBeInTheDocument();
    expect(screen.queryByText("Maria Soto")).not.toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Eliminar" }));

    await waitFor(() => expect(userApiMocks.delete).toHaveBeenCalledWith(8));
    expect(screen.queryByText("Pedro Rojas")).not.toBeInTheDocument();
  });
});
