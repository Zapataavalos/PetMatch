import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { Navbar } from "../Navbar";
import { renderWithAppProviders } from "../../../test/render";

const authMocks = vi.hoisted(() => ({
  useAuth: vi.fn(),
}));

vi.mock("../../../auth/useAuth", () => ({
  useAuth: authMocks.useAuth,
}));

describe("Navbar", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it("muestra opciones admin y permite cerrar sesion", async () => {
    const user = userEvent.setup();
    const logout = vi.fn();
    authMocks.useAuth.mockReturnValue({
      isAuthenticated: true,
      isAdmin: true,
      logout,
    });

    renderWithAppProviders(<Navbar />, "/dashboard");

    expect(screen.getByText("Admin")).toBeInTheDocument();
    expect(screen.getByText("Mascotas")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Salir" }));

    expect(logout).toHaveBeenCalledTimes(1);
  });

  it("muestra accion de login si no hay sesion", () => {
    authMocks.useAuth.mockReturnValue({
      isAuthenticated: false,
      isAdmin: false,
      logout: vi.fn(),
    });

    renderWithAppProviders(<Navbar />, "/dashboard");

    expect(screen.getByRole("button", { name: "Entrar" })).toBeInTheDocument();
    expect(screen.queryByText("Admin")).not.toBeInTheDocument();
  });
});
