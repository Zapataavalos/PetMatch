import type { PetApiResponse, PetCreateRequest } from "../types";
import { api } from "./axiosConfig";

export const petApi = {
  getAll: async (): Promise<PetApiResponse[]> => {
    const { data } = await api.get<PetApiResponse[]>("/api/pet");
    return data;
  },

  getById: async (id: number): Promise<PetApiResponse> => {
    const { data } = await api.get<PetApiResponse>(`/api/pet/${id}`);
    return data;
  },

  create: async (payload: PetCreateRequest): Promise<PetApiResponse> => {
    const { data } = await api.post<PetApiResponse>("/api/pet", payload);
    return data;
  },

  update: async (id: number, payload: PetCreateRequest): Promise<PetApiResponse> => {
    const { data } = await api.put<PetApiResponse>(`/api/pet/${id}`, payload);
    return data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/pet/${id}`);
  },
};
