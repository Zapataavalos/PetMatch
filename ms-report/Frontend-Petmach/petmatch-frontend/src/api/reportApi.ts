import { api } from "./axiosConfig";
import type { ReportApiResponse, ReportCreateRequest } from "../types";

export const reportApi = {
  getAll: async (): Promise<ReportApiResponse[]> => {
    const { data } = await api.get<ReportApiResponse[]>("/api/report");
    return data;
  },

  create: async (payload: ReportCreateRequest): Promise<ReportApiResponse> => {
    const { data } = await api.post<ReportApiResponse>("/api/report", payload);
    return data;
  },
};
