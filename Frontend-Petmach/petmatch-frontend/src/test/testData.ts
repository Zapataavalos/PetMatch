import type {
  AuthResponse,
  ColorCatalogo,
  ConfiguracionUsuarioResponse,
  PetApiResponse,
  ReportApiResponse,
  Usuario,
} from "../types";

export const mockAuthUser: AuthResponse = {
  token: "token-test",
  tipo: "Bearer",
  idUsuario: 7,
  nombre: "Maria Soto",
  email: "maria@example.com",
  idRol: 1,
  fechaRegistro: "2026-01-15T10:00:00.000Z",
  rol: "ADMIN",
};

export const mockReports: ReportApiResponse[] = [
  {
    id: 1,
    codigo: "REP-001",
    nombre: "Luna",
    descripcion: "Gata blanca perdida cerca del parque.",
    ubicacion: "Providencia",
    estado: "PERDIDO",
    imagenUrl: "https://example.com/luna.jpg",
    latitud: -33.43,
    longitud: -70.61,
    createdAt: "2026-06-04T12:00:00.000Z",
  },
  {
    id: 2,
    codigo: "REP-002",
    nombre: "Max",
    descripcion: "Perro encontrado con collar rojo.",
    ubicacion: "Santiago Centro",
    estado: "EN_REFUGIO",
    imagenUrl: "https://example.com/max.jpg",
    latitud: -33.44,
    longitud: -70.66,
    createdAt: "2026-06-03T12:00:00.000Z",
  },
  {
    id: 3,
    codigo: "REP-003",
    nombre: "Nala",
    descripcion: "Mascota vista en avenida concurrida.",
    ubicacion: "Las Condes",
    estado: "EN_PELIGRO",
    imagenUrl: "https://example.com/nala.jpg",
    latitud: -33.41,
    longitud: -70.57,
    createdAt: "2026-06-02T12:00:00.000Z",
  },
];

export const mockPets: PetApiResponse[] = [
  {
    id: 10,
    nombre: "Bruno",
    tipo: "Perro",
    raza: "Mestizo",
    tamano: "Mediano",
    estado: "ACTIVO",
    descripcion: "Jugueton y sociable.",
    imagenUrl: "https://example.com/bruno.jpg",
    createdAt: "2026-05-10T10:00:00.000Z",
  },
  {
    id: 11,
    nombre: "Mishi",
    tipo: "Gato",
    raza: "Europeo",
    tamano: "Pequeno",
    estado: "REPORTADO_PERDIDO",
    descripcion: "Gata gris con collar verde.",
    imagenUrl: "https://example.com/mishi.jpg",
    createdAt: "2026-05-12T10:00:00.000Z",
  },
];

export const mockUsers: Usuario[] = [
  {
    idUsuario: 7,
    nombre: "Maria Soto",
    email: "maria@example.com",
    fechaRegistro: "2026-01-15T10:00:00.000Z",
    idRol: 1,
  },
  {
    idUsuario: 8,
    nombre: "Pedro Rojas",
    email: "pedro@example.com",
    fechaRegistro: "2026-02-20T10:00:00.000Z",
    idRol: 3,
  },
];

export const mockColors: ColorCatalogo[] = [
  {
    idColor: 1,
    nombreColor: "AMARILLO PETMATCH",
    codigoHexadecimal: "#F5C400",
  },
  {
    idColor: 2,
    nombreColor: "VERDE RESCATE",
    codigoHexadecimal: "#10B981",
  },
];

export const mockSettings: ConfiguracionUsuarioResponse = {
  idConfiguracionUsuario: 4,
  idUsuario: 7,
  idColor: 1,
  notificacionesActivas: true,
  modoOscuro: true,
  idioma: "ES",
};
