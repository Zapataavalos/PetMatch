import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { render } from "@testing-library/react";
import { LoginPage } from "../LoginPage";
import { RegisterPage } from "../RegisterPage";

const authMocks = vi.hoisted(() => ({
  useAuth: vi.fn(),
}));

vi.mock("../../auth/useAuth", () => ({
  useAuth: authMocks.useAuth,
}));

describe("Auth pages", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("envia credenciales y navega al mapa al iniciar sesion", async () => {
    const user = userEvent.setup();
    const login = vi.fn().mockResolvedValue(undefined);
    authMocks.useAuth.mockReturnValue({ login });

    const { container } = render(
      <MemoryRouter initialEntries={["/login"]}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/mapa" element={<div>Mapa privado</div>} />
        </Routes>
      </MemoryRouter>
    );

    await user.type(screen.getByPlaceholderText("tucorreo@ejemplo.com"), "maria@example.com");
    await user.type(container.querySelector('input[type="password"]') as HTMLInputElement, "secreto123");
    await user.click(screen.getByRole("button", { name: /Iniciar/i }));

    await waitFor(() =>
      expect(login).toHaveBeenCalledWith({
        email: "maria@example.com",
        contrasena: "secreto123",
      })
    );
    expect(await screen.findByText("Mapa privado")).toBeInTheDocument();
  });

  it("registra ciudadanos con rol por defecto y vuelve a login", async () => {
    const user = userEvent.setup();
    const register = vi.fn().mockResolvedValue(undefined);
    authMocks.useAuth.mockReturnValue({ register });

    const { container } = render(
      <MemoryRouter initialEntries={["/registro"]}>
        <Routes>
          <Route path="/registro" element={<RegisterPage />} />
          <Route path="/login" element={<div>Login listo</div>} />
        </Routes>
      </MemoryRouter>
    );

    await user.type(screen.getByLabelText(/Nombre completo/i), "Pedro Rojas");
    await user.type(screen.getByPlaceholderText("tucorreo@ejemplo.com"), "pedro@example.com");
    await user.type(container.querySelector('input[type="password"]') as HTMLInputElement, "secreto123");
    await user.click(screen.getByRole("button", { name: /Crear Cuenta/i }));

    await waitFor(() =>
      expect(register).toHaveBeenCalledWith({
        nombre: "Pedro Rojas",
        email: "pedro@example.com",
        contrasena: "secreto123",
        idRol: 3,
      })
    );
    expect(await screen.findByText("Login listo")).toBeInTheDocument();
  });
});
