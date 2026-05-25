import { api } from "./axiosConfig";
import type { AuthResponse, LoginRequest, RegisterRequest, Usuario } from "../types";

export const authApi = {
  login: async (payload: LoginRequest): Promise<AuthResponse> => {
    const { data } = await api.post<AuthResponse>("/api/v1/auth/login", payload);
    return data;
  },

  register: async (payload: RegisterRequest): Promise<Usuario> => {
    const { data } = await api.post<Usuario>("/api/v1/auth/register", payload);
    return data;
  },
};