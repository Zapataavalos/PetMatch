import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
const API_TIMEOUT_MS = resolveApiTimeout();

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT_MS,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("petmatch_token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

function resolveApiTimeout() {
  const timeout = Number(import.meta.env.VITE_API_TIMEOUT_MS);

  return Number.isFinite(timeout) && timeout > 0 ? timeout : 20000;
}
