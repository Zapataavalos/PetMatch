export type UserRole = "ADMIN" | "DUENO" | "CIUDADANO";

export interface AuthResponse {
  token: string;
  tipo: string;
  idUsuario: number;
  nombre: string;
  email: string;
  idRol: number;
  fechaRegistro?: string;
  rol?: UserRole;
  role?: UserRole;
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

export interface PerfilRequest {
  nombre: string;
  email: string;
}

export interface PerfilResponse extends AuthResponse {
  fechaRegistro: string;
}

export type ReportStatus = "PERDIDO" | "EN_REFUGIO" | "EN_PELIGRO" | "ENCONTRADO";

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

export interface ReportApiResponse {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  ubicacion: string;
  estado: ReportStatus;
  imagenUrl: string;
  latitud: number;
  longitud: number;
  createdAt: string;
}

export interface ReportCreateRequest {
  nombre: string;
  descripcion: string;
  ubicacion: string;
  estado: ReportStatus;
  imagenUrl?: string;
  latitud?: number;
  longitud?: number;
}

export interface RegionCatalogo {
  idRegion: number;
  nombreRegion: string;
  idPais: number;
}

export interface CiudadCatalogo {
  idCiudad: number;
  nombreCiudad: string;
  idRegion: number;
}

export interface ColorCatalogo {
  idColor: number;
  nombreColor: string;
  codigoHexadecimal: string;
}

export interface Coordinates {
  latitud: number;
  longitud: number;
}

export type PetStatus = "ACTIVO" | "REPORTADO_PERDIDO" | "EN_REFUGIO";

export interface PetApiResponse {
  id: number;
  nombre: string;
  tipo: string;
  raza: string;
  tamano: string;
  estado: PetStatus;
  descripcion: string;
  imagenUrl: string;
  createdAt: string;
}

export interface PetCreateRequest {
  nombre: string;
  tipo: string;
  raza: string;
  tamano: string;
  estado: PetStatus;
  descripcion: string;
  imagenUrl?: string;
}

export type AppLanguage = "ES" | "EN";

export interface ConfiguracionUsuarioResponse {
  idConfiguracionUsuario: number;
  idUsuario: number;
  idColor: number;
  notificacionesActivas: boolean;
  modoOscuro: boolean;
  idioma: AppLanguage;
}

export interface ConfiguracionUsuarioRequest {
  idUsuario: number;
  idColor: number;
  notificacionesActivas: boolean;
  modoOscuro: boolean;
  idioma: AppLanguage;
}

export interface Coincidencia {
  id: number;
  porcentaje: number;
  ubicacion: string;
  imagenPerdido: string;
  imagenEncontrado: string;
}
