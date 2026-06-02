import type { CiudadCatalogo, RegionCatalogo } from "../types";
import { api } from "./axiosConfig";

export const catalogosApi = {
  getRegiones: async (): Promise<RegionCatalogo[]> => {
    const { data } = await api.get<RegionCatalogo[]>("/api/v1/regiones");
    return data;
  },

  getCiudadesPorRegion: async (idRegion: number): Promise<CiudadCatalogo[]> => {
    const { data } = await api.get<CiudadCatalogo[]>(
      `/api/v1/ciudades/region/${idRegion}`
    );
    return data;
  },
};
