export type UserRole = "ADMIN" | "DUENO" | "CIUDADANO";

export interface AuthResponse {
  token: string;
  tipo: string;
  idUsuario: number;
  nombre: string;
  email: string;
  idRol: number;
}

export interface LoginRequest {
  email: string;
  contrasena: string;
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  contrasena: string;
  idRol: number;
}

export interface Usuario {
  idUsuario: number;
  nombre: string;
  email: string;
  fechaRegistro: string;
  idRol: number;
}

export type ReportStatus = "PERDIDO" | "EN_REFUGIO" | "EN_PELIGRO";

export interface ReporteResumen {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  ubicacion: string;
  tiempo: string;
  estado: ReportStatus;
  imagenUrl: string;
  latitud: number;
  longitud: number;
}

export interface Coincidencia {
  id: number;
  porcentaje: number;
  ubicacion: string;
  imagenPerdido: string;
  imagenEncontrado: string;
}