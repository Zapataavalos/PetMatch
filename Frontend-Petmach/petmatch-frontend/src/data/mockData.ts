import type { Coincidencia, ReporteResumen } from "../types";

export const mockReportes: ReporteResumen[] = [
  {
    id: 1,
    codigo: "REP-089",
    nombre: "Max",
    descripcion: "Golden retriever, con collar azul.",
    ubicacion: "Centro, Ciudad",
    tiempo: "Hace 2 horas",
    estado: "PERDIDO",
    imagenUrl:
      "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=600&auto=format&fit=crop",
    latitud: -33.45,
    longitud: -70.66,
  },
  {
    id: 2,
    codigo: "REP-088",
    nombre: "Luna",
    descripcion: "Gata negra, asustada. Resguardada.",
    ubicacion: "Refugio Patitas",
    tiempo: "Hace 5 horas",
    estado: "EN_REFUGIO",
    imagenUrl:
      "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=600&auto=format&fit=crop",
    latitud: -33.43,
    longitud: -70.62,
  },
  {
    id: 3,
    codigo: "REP-087",
    nombre: "Desconocido",
    descripcion: "Perro callejero corriendo cerca a la vía.",
    ubicacion: "Autopista Norte",
    tiempo: "Hace 10 min",
    estado: "EN_PELIGRO",
    imagenUrl:
      "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop",
    latitud: -33.41,
    longitud: -70.71,
  },
];

export const mockCoincidencias: Coincidencia[] = [
  {
    id: 1,
    porcentaje: 95,
    ubicacion: "Av. Principal y Calle 14.",
    imagenPerdido:
      "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=600&auto=format&fit=crop",
    imagenEncontrado:
      "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop",
  },
  {
    id: 2,
    porcentaje: 92,
    ubicacion: "Av. Principal y Calle 14.",
    imagenPerdido:
      "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=600&auto=format&fit=crop",
    imagenEncontrado:
      "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop",
  },
  {
    id: 3,
    porcentaje: 88,
    ubicacion: "Av. Principal y Calle 14.",
    imagenPerdido:
      "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=600&auto=format&fit=crop",
    imagenEncontrado:
      "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop",
  },
];