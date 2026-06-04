import type {
  ColorCatalogo,
  ConfiguracionUsuarioRequest,
  ConfiguracionUsuarioResponse,
} from "../types";
import { api } from "./axiosConfig";

export const configuracionApi = {
  getByUser: async (idUsuario: number): Promise<ConfiguracionUsuarioResponse> => {
    const { data } = await api.get<ConfiguracionUsuarioResponse>(
      `/api/v1/configuraciones-usuario/usuario/${idUsuario}`
    );
    return data;
  },

  create: async (
    payload: ConfiguracionUsuarioRequest
  ): Promise<ConfiguracionUsuarioResponse> => {
    const { data } = await api.post<ConfiguracionUsuarioResponse>(
      "/api/v1/configuraciones-usuario",
      payload
    );
    return data;
  },

  update: async (
    idConfiguracionUsuario: number,
    payload: ConfiguracionUsuarioRequest
  ): Promise<ConfiguracionUsuarioResponse> => {
    const { data } = await api.put<ConfiguracionUsuarioResponse>(
      `/api/v1/configuraciones-usuario/${idConfiguracionUsuario}`,
      payload
    );
    return data;
  },

  getColors: async (): Promise<ColorCatalogo[]> => {
    const { data } = await api.get<ColorCatalogo[]>("/api/v1/colores");
    return data;
  },
};
