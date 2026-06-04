import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { AdminRoute } from "../AdminRoute";
import { ProtectedRoute } from "../ProtectedRoute";

const authMocks = vi.hoisted(() => ({
  useAuth: vi.fn(),
}));

vi.mock("../useAuth", () => ({
  useAuth: authMocks.useAuth,
}));

describe("Route guards", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("redirige usuarios anonimos al login en rutas protegidas", () => {
    authMocks.useAuth.mockReturnValue({ isAuthenticated: false });

    render(
      <MemoryRouter initialEntries={["/privado"]}>
        <Routes>
          <Route path="/login" element={<div>Login requerido</div>} />
          <Route element={<ProtectedRoute />}>
            <Route path="/privado" element={<div>Contenido privado</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Login requerido")).toBeInTheDocument();
  });

  it("permite acceso a rutas protegidas cuando hay sesion", () => {
    authMocks.useAuth.mockReturnValue({ isAuthenticated: true });

    render(
      <MemoryRouter initialEntries={["/privado"]}>
        <Routes>
          <Route element={<ProtectedRoute />}>
            <Route path="/privado" element={<div>Contenido privado</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Contenido privado")).toBeInTheDocument();
  });

  it("redirige no administradores al dashboard", () => {
    authMocks.useAuth.mockReturnValue({ isAuthenticated: true, isAdmin: false });

    render(
      <MemoryRouter initialEntries={["/admin"]}>
        <Routes>
          <Route path="/dashboard" element={<div>Dashboard usuario</div>} />
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<div>Panel admin</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Dashboard usuario")).toBeInTheDocument();
  });

  it("permite acceso al panel admin con rol administrador", () => {
    authMocks.useAuth.mockReturnValue({ isAuthenticated: true, isAdmin: true });

    render(
      <MemoryRouter initialEntries={["/admin"]}>
        <Routes>
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<div>Panel admin</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Panel admin")).toBeInTheDocument();
  });
});
