import { isAxiosError } from "axios";

interface ApiErrorResponse {
  message?: string;
  errors?: Record<string, string>;
}

export function getApiErrorMessage(error: unknown, fallback: string) {
  if (!isAxiosError<ApiErrorResponse>(error)) {
    return fallback;
  }

  const data = error.response?.data;

  if (data?.errors) {
    return Object.values(data.errors).join(" ");
  }

  if (data?.message) {
    return data.message;
  }

  if (error.code === "ECONNABORTED") {
    return "El servidor tardo demasiado en responder. Revisa que Docker y los microservicios esten levantados.";
  }

  if (!error.response) {
    return "No fue posible conectar con el servidor. Revisa que el API Gateway este disponible en http://localhost:8080.";
  }

  return fallback;
}
