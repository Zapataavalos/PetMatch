import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { PetsPage } from "../PetsPage";
import { renderWithRouter } from "../../test/render";
import { mockPets } from "../../test/testData";

const petApiMocks = vi.hoisted(() => ({
  getAll: vi.fn(),
  delete: vi.fn(),
  create: vi.fn(),
  update: vi.fn(),
}));

vi.mock("../../api/petApi", () => ({
  petApi: {
    getAll: petApiMocks.getAll,
    delete: petApiMocks.delete,
    create: petApiMocks.create,
    update: petApiMocks.update,
  },
}));

describe("PetsPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    petApiMocks.getAll.mockResolvedValue(mockPets);
    petApiMocks.delete.mockResolvedValue(undefined);
  });

  it("muestra mascotas cargadas desde el servicio y el estado en vivo", async () => {
    renderWithRouter(<PetsPage />, "/mascotas");

    expect(await screen.findByText("Bruno")).toBeInTheDocument();
    expect(screen.getByText("Mishi")).toBeInTheDocument();
    expect(screen.getByText("Datos en vivo")).toBeInTheDocument();
    expect(screen.getByText("Mostrando 2 de 2 mascotas registradas.")).toBeInTheDocument();
  });

  it("filtra mascotas reportadas perdidas", async () => {
    const user = userEvent.setup();

    renderWithRouter(<PetsPage />, "/mascotas");

    expect(await screen.findByText("Bruno")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /Perdidas \(1\)/i }));

    expect(screen.getByText("Mishi")).toBeInTheDocument();
    expect(screen.queryByText("Bruno")).not.toBeInTheDocument();
  });

  it("permite actualizar manualmente la lista de mascotas", async () => {
    const user = userEvent.setup();

    renderWithRouter(<PetsPage />, "/mascotas");

    expect(await screen.findByText("Bruno")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /Actualizar/i }));

    await waitFor(() => expect(petApiMocks.getAll).toHaveBeenCalledTimes(2));
  });
});
