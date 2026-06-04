import { api } from "./axiosConfig";
import type { PerfilRequest, PerfilResponse, Usuario } from "../types";

export const userApi = {
  getAll: async (): Promise<Usuario[]> => {
    const { data } = await api.get<Usuario[]>("/api/v1/usuarios");
    return data;
  },

  getMe: async (): Promise<Usuario> => {
    const { data } = await api.get<Usuario>("/api/v1/usuarios/me");
    return data;
  },

  updateMe: async (payload: PerfilRequest): Promise<PerfilResponse> => {
    const { data } = await api.patch<PerfilResponse>("/api/v1/usuarios/me", payload);
    return data;
  },

  delete: async (idUsuario: number): Promise<void> => {
    await api.delete(`/api/v1/usuarios/${idUsuario}`);
  },
};
