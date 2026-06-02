import type { CiudadCatalogo, Coordinates, RegionCatalogo } from "../types";

interface CityDefinition {
  nombreCiudad: string;
  coordinates: Coordinates;
}

interface RegionDefinition {
  idRegion: number;
  nombreRegion: string;
  aliases: string[];
  coordinates: Coordinates;
  cities: CityDefinition[];
}

export const DEFAULT_LOCATION: Coordinates = {
  latitud: -33.4489,
  longitud: -70.6693,
};

const regionDefinitions: RegionDefinition[] = [
  {
    idRegion: 1,
    nombreRegion: "ARICA Y PARINACOTA",
    aliases: ["REGION DE ARICA Y PARINACOTA"],
    coordinates: { latitud: -18.4783, longitud: -70.3126 },
    cities: [
      { nombreCiudad: "ARICA", coordinates: { latitud: -18.4783, longitud: -70.3126 } },
      { nombreCiudad: "PUTRE", coordinates: { latitud: -18.1982, longitud: -69.5607 } },
    ],
  },
  {
    idRegion: 2,
    nombreRegion: "TARAPACA",
    aliases: ["REGION DE TARAPACA"],
    coordinates: { latitud: -20.2133, longitud: -70.1524 },
    cities: [
      { nombreCiudad: "IQUIQUE", coordinates: { latitud: -20.2133, longitud: -70.1524 } },
      { nombreCiudad: "ALTO HOSPICIO", coordinates: { latitud: -20.2682, longitud: -70.1048 } },
    ],
  },
  {
    idRegion: 3,
    nombreRegion: "ANTOFAGASTA",
    aliases: ["REGION DE ANTOFAGASTA"],
    coordinates: { latitud: -23.6509, longitud: -70.3975 },
    cities: [
      { nombreCiudad: "ANTOFAGASTA", coordinates: { latitud: -23.6509, longitud: -70.3975 } },
      { nombreCiudad: "CALAMA", coordinates: { latitud: -22.4544, longitud: -68.9294 } },
    ],
  },
  {
    idRegion: 4,
    nombreRegion: "ATACAMA",
    aliases: ["REGION DE ATACAMA"],
    coordinates: { latitud: -27.3668, longitud: -70.3323 },
    cities: [
      { nombreCiudad: "COPIAPO", coordinates: { latitud: -27.3668, longitud: -70.3323 } },
      { nombreCiudad: "VALLENAR", coordinates: { latitud: -28.575, longitud: -70.756 } },
    ],
  },
  {
    idRegion: 5,
    nombreRegion: "COQUIMBO",
    aliases: ["REGION DE COQUIMBO"],
    coordinates: { latitud: -29.9027, longitud: -71.2519 },
    cities: [
      { nombreCiudad: "LA SERENA", coordinates: { latitud: -29.9027, longitud: -71.2519 } },
      { nombreCiudad: "COQUIMBO", coordinates: { latitud: -29.9533, longitud: -71.3436 } },
      { nombreCiudad: "OVALLE", coordinates: { latitud: -30.6011, longitud: -71.199 } },
    ],
  },
  {
    idRegion: 6,
    nombreRegion: "VALPARAISO",
    aliases: ["REGION DE VALPARAISO"],
    coordinates: { latitud: -33.0472, longitud: -71.6127 },
    cities: [
      { nombreCiudad: "VALPARAISO", coordinates: { latitud: -33.0472, longitud: -71.6127 } },
      { nombreCiudad: "VINA DEL MAR", coordinates: { latitud: -33.0245, longitud: -71.5518 } },
      { nombreCiudad: "QUILPUE", coordinates: { latitud: -33.0498, longitud: -71.4425 } },
      { nombreCiudad: "SAN ANTONIO", coordinates: { latitud: -33.5947, longitud: -71.6075 } },
    ],
  },
  {
    idRegion: 7,
    nombreRegion: "METROPOLITANA",
    aliases: ["REGION METROPOLITANA", "REGION METROPOLITANA DE SANTIAGO"],
    coordinates: DEFAULT_LOCATION,
    cities: [
      { nombreCiudad: "SANTIAGO", coordinates: DEFAULT_LOCATION },
      { nombreCiudad: "PROVIDENCIA", coordinates: { latitud: -33.4263, longitud: -70.617 } },
      { nombreCiudad: "LAS CONDES", coordinates: { latitud: -33.4089, longitud: -70.5675 } },
      { nombreCiudad: "PUENTE ALTO", coordinates: { latitud: -33.6117, longitud: -70.5758 } },
      { nombreCiudad: "MAIPU", coordinates: { latitud: -33.5103, longitud: -70.7572 } },
    ],
  },
  {
    idRegion: 8,
    nombreRegion: "OHIGGINS",
    aliases: ["O HIGGINS", "LIBERTADOR BERNARDO OHIGGINS", "REGION DE OHIGGINS"],
    coordinates: { latitud: -34.1708, longitud: -70.7444 },
    cities: [
      { nombreCiudad: "RANCAGUA", coordinates: { latitud: -34.1708, longitud: -70.7444 } },
      { nombreCiudad: "SAN FERNANDO", coordinates: { latitud: -34.5833, longitud: -70.9833 } },
    ],
  },
  {
    idRegion: 9,
    nombreRegion: "MAULE",
    aliases: ["REGION DEL MAULE"],
    coordinates: { latitud: -35.4264, longitud: -71.6554 },
    cities: [
      { nombreCiudad: "TALCA", coordinates: { latitud: -35.4264, longitud: -71.6554 } },
      { nombreCiudad: "CURICO", coordinates: { latitud: -34.9828, longitud: -71.2394 } },
      { nombreCiudad: "LINARES", coordinates: { latitud: -35.8467, longitud: -71.5931 } },
    ],
  },
  {
    idRegion: 10,
    nombreRegion: "NUBLE",
    aliases: ["REGION DE NUBLE"],
    coordinates: { latitud: -36.6063, longitud: -72.1034 },
    cities: [
      { nombreCiudad: "CHILLAN", coordinates: { latitud: -36.6063, longitud: -72.1034 } },
      { nombreCiudad: "SAN CARLOS", coordinates: { latitud: -36.4248, longitud: -71.958 } },
    ],
  },
  {
    idRegion: 11,
    nombreRegion: "BIOBIO",
    aliases: ["BIO BIO", "REGION DEL BIOBIO"],
    coordinates: { latitud: -36.827, longitud: -73.0498 },
    cities: [
      { nombreCiudad: "CONCEPCION", coordinates: { latitud: -36.827, longitud: -73.0498 } },
      { nombreCiudad: "TALCAHUANO", coordinates: { latitud: -36.7248, longitud: -73.1168 } },
      { nombreCiudad: "LOS ANGELES", coordinates: { latitud: -37.4697, longitud: -72.3537 } },
    ],
  },
  {
    idRegion: 12,
    nombreRegion: "LA ARAUCANIA",
    aliases: ["ARAUCANIA", "REGION DE LA ARAUCANIA"],
    coordinates: { latitud: -38.7359, longitud: -72.5904 },
    cities: [
      { nombreCiudad: "TEMUCO", coordinates: { latitud: -38.7359, longitud: -72.5904 } },
      { nombreCiudad: "VILLARRICA", coordinates: { latitud: -39.2857, longitud: -72.2273 } },
    ],
  },
  {
    idRegion: 13,
    nombreRegion: "LOS RIOS",
    aliases: ["REGION DE LOS RIOS"],
    coordinates: { latitud: -39.8174, longitud: -73.2425 },
    cities: [
      { nombreCiudad: "VALDIVIA", coordinates: { latitud: -39.8174, longitud: -73.2425 } },
      { nombreCiudad: "LA UNION", coordinates: { latitud: -40.2931, longitud: -73.0817 } },
    ],
  },
  {
    idRegion: 14,
    nombreRegion: "LOS LAGOS",
    aliases: ["REGION DE LOS LAGOS"],
    coordinates: { latitud: -41.4689, longitud: -72.9411 },
    cities: [
      { nombreCiudad: "PUERTO MONTT", coordinates: { latitud: -41.4689, longitud: -72.9411 } },
      { nombreCiudad: "OSORNO", coordinates: { latitud: -40.5739, longitud: -73.1335 } },
      { nombreCiudad: "CASTRO", coordinates: { latitud: -42.4721, longitud: -73.7732 } },
    ],
  },
  {
    idRegion: 15,
    nombreRegion: "AYSEN",
    aliases: ["AISEN", "REGION DE AYSEN"],
    coordinates: { latitud: -45.5712, longitud: -72.0685 },
    cities: [
      { nombreCiudad: "COYHAIQUE", coordinates: { latitud: -45.5712, longitud: -72.0685 } },
      { nombreCiudad: "AYSEN", coordinates: { latitud: -45.403, longitud: -72.6918 } },
    ],
  },
  {
    idRegion: 16,
    nombreRegion: "MAGALLANES",
    aliases: ["REGION DE MAGALLANES"],
    coordinates: { latitud: -53.1638, longitud: -70.9171 },
    cities: [
      { nombreCiudad: "PUNTA ARENAS", coordinates: { latitud: -53.1638, longitud: -70.9171 } },
      { nombreCiudad: "PUERTO NATALES", coordinates: { latitud: -51.7299, longitud: -72.5063 } },
    ],
  },
];

export const fallbackRegiones: RegionCatalogo[] = regionDefinitions.map(
  ({ idRegion, nombreRegion }) => ({
    idRegion,
    nombreRegion,
    idPais: 1,
  })
);

export function getFallbackCiudadesPorRegion(
  region?: Pick<RegionCatalogo, "idRegion" | "nombreRegion">
): CiudadCatalogo[] {
  if (!region) {
    return [];
  }

  const definition = findRegionDefinition(region.nombreRegion);

  if (!definition) {
    return [];
  }

  return definition.cities.map((city, index) => ({
    idCiudad: -(region.idRegion * 100 + index + 1),
    nombreCiudad: city.nombreCiudad,
    idRegion: region.idRegion,
  }));
}

export function getCoordinatesForLocation(
  ciudad?: string,
  region?: string
): Coordinates {
  const cityKey = normalizeLocationKey(ciudad);

  if (cityKey) {
    for (const definition of regionDefinitions) {
      const city = definition.cities.find(
        (item) => normalizeLocationKey(item.nombreCiudad) === cityKey
      );

      if (city) {
        return city.coordinates;
      }
    }
  }

  return findRegionDefinition(region)?.coordinates ?? DEFAULT_LOCATION;
}

export function formatLocationName(value: string) {
  return value
    .toLowerCase()
    .split(" ")
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

function findRegionDefinition(region?: string) {
  const regionKey = normalizeLocationKey(region);

  if (!regionKey) {
    return undefined;
  }

  return regionDefinitions.find((definition) => {
    const names = [definition.nombreRegion, ...definition.aliases].map((item) =>
      normalizeLocationKey(item)
    );

    return names.some(
      (name) => name === regionKey || name.includes(regionKey) || regionKey.includes(name)
    );
  });
}

function normalizeLocationKey(value?: string) {
  return (value ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-zA-Z0-9]+/g, " ")
    .trim()
    .toUpperCase();
}
