import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { SettingsPage } from "../SettingsPage";
import { renderWithAppProviders } from "../../test/render";
import { mockAuthUser, mockColors, mockSettings } from "../../test/testData";

const authMocks = vi.hoisted(() => ({
  useAuth: vi.fn(),
}));

const configuracionApiMocks = vi.hoisted(() => ({
  getColors: vi.fn(),
  getByUser: vi.fn(),
  create: vi.fn(),
  update: vi.fn(),
}));

vi.mock("../../auth/useAuth", () => ({
  useAuth: authMocks.useAuth,
}));

vi.mock("../../api/configuracionApi", () => ({
  configuracionApi: {
    getColors: configuracionApiMocks.getColors,
    getByUser: configuracionApiMocks.getByUser,
    create: configuracionApiMocks.create,
    update: configuracionApiMocks.update,
  },
}));

describe("SettingsPage", () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.lang = "";
    document.documentElement.className = "";
    delete document.documentElement.dataset.theme;
    document.documentElement.style.colorScheme = "";
    vi.clearAllMocks();
    authMocks.useAuth.mockReturnValue({ user: mockAuthUser });
    configuracionApiMocks.getColors.mockResolvedValue(mockColors);
    configuracionApiMocks.getByUser.mockResolvedValue(mockSettings);
    configuracionApiMocks.update.mockResolvedValue({ ...mockSettings, idioma: "EN" });
  });

  it("cambia idioma en vivo y mantiene Chile como unico pais", async () => {
    const user = userEvent.setup();

    renderWithAppProviders(<SettingsPage />, "/configuracion");

    await waitFor(() => expect(configuracionApiMocks.getColors).toHaveBeenCalledTimes(1));

    await user.selectOptions(screen.getByLabelText("Idioma"), "EN");

    expect(await screen.findByRole("heading", { name: "Settings" })).toBeInTheDocument();
    expect(document.documentElement.lang).toBe("en");

    const countrySelect = screen.getByDisplayValue("Chile") as HTMLSelectElement;
    expect(countrySelect).toBeDisabled();
    expect(countrySelect.value).toBe("Chile");
    expect(screen.getByText("Chile is the only enabled country.")).toBeInTheDocument();
  });

  it("guarda configuracion sincronizando idioma seleccionado", async () => {
    const user = userEvent.setup();

    renderWithAppProviders(<SettingsPage />, "/configuracion");

    await waitFor(() => expect(configuracionApiMocks.getByUser).toHaveBeenCalledWith(7));
    await user.selectOptions(screen.getByLabelText("Idioma"), "EN");
    await user.click(await screen.findByRole("button", { name: /Save settings/i }));

    await waitFor(() =>
      expect(configuracionApiMocks.update).toHaveBeenCalledWith(
        4,
        expect.objectContaining({
          idUsuario: 7,
          idioma: "EN",
        })
      )
    );
    expect(await screen.findByText("Settings saved.")).toBeInTheDocument();
  });

  it("oculta color principal y activa modo claro al desactivar modo oscuro", async () => {
    const user = userEvent.setup();

    renderWithAppProviders(<SettingsPage />, "/configuracion");

    await waitFor(() => expect(configuracionApiMocks.getByUser).toHaveBeenCalledWith(7));

    expect(screen.queryByLabelText("Color principal")).not.toBeInTheDocument();

    const darkModeSwitch = screen.getByRole("switch", { name: "Modo oscuro" });
    expect(darkModeSwitch).toHaveAttribute("aria-checked", "true");

    await user.click(darkModeSwitch);

    expect(darkModeSwitch).toHaveAttribute("aria-checked", "false");
    expect(document.documentElement).toHaveClass("petmatch-light");
    expect(document.documentElement.dataset.theme).toBe("light");
  });
});
