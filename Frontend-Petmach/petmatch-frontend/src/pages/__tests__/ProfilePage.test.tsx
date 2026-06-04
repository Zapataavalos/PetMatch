import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ProfilePage } from "../ProfilePage";
import { renderWithAppProviders } from "../../test/render";
import { mockAuthUser, mockUsers } from "../../test/testData";

const authMocks = vi.hoisted(() => ({
  useAuth: vi.fn(),
}));

const userApiMocks = vi.hoisted(() => ({
  getMe: vi.fn(),
  updateMe: vi.fn(),
}));

vi.mock("../../auth/useAuth", () => ({
  useAuth: authMocks.useAuth,
}));

vi.mock("../../api/userApi", () => ({
  userApi: {
    getMe: userApiMocks.getMe,
    updateMe: userApiMocks.updateMe,
  },
}));

describe("ProfilePage", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
    authMocks.useAuth.mockReturnValue({
      user: mockAuthUser,
      role: "ADMIN",
      logout: vi.fn(),
      updateSession: vi.fn(),
    });
    userApiMocks.getMe.mockResolvedValue(mockUsers[0]);
    userApiMocks.updateMe.mockResolvedValue({
      ...mockAuthUser,
      nombre: "Maria Actualizada",
      email: "maria.actualizada@example.com",
      fechaRegistro: mockUsers[0].fechaRegistro,
    });
  });

  it("carga perfil y guarda cambios de nombre y correo", async () => {
    const user = userEvent.setup();

    renderWithAppProviders(<ProfilePage />, "/perfil");

    const nameInput = await screen.findByDisplayValue("Maria Soto");
    const emailInput = screen.getByDisplayValue("maria@example.com");

    await user.clear(nameInput);
    await user.type(nameInput, "Maria Actualizada");
    await user.clear(emailInput);
    await user.type(emailInput, "maria.actualizada@example.com");
    await user.click(screen.getByRole("button", { name: "Guardar cambios" }));

    await waitFor(() =>
      expect(userApiMocks.updateMe).toHaveBeenCalledWith({
        nombre: "Maria Actualizada",
        email: "maria.actualizada@example.com",
      })
    );
    expect(await screen.findByText("Perfil actualizado.")).toBeInTheDocument();
  });

  it("muestra preferencias con idioma y pais Chile", async () => {
    const user = userEvent.setup();

    renderWithAppProviders(<ProfilePage />, "/perfil");

    expect(await screen.findByDisplayValue("Maria Soto")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Preferencias" }));

    expect(screen.getByText("Espanol")).toBeInTheDocument();
    expect(screen.getByText("Chile")).toBeInTheDocument();
  });
});
