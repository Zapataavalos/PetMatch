import type { Coordinates } from "../types";

export function buildCoordinateLocationLabel(coordinates: Coordinates, reference?: string) {
  const cleanReference = reference?.trim();
  const coordinateLabel = `${coordinates.latitud.toFixed(6)}, ${coordinates.longitud.toFixed(6)}`;

  return cleanReference
    ? `${cleanReference} (${coordinateLabel})`
    : `Ubicacion seleccionada (${coordinateLabel})`;
}

export function parseClickedCoordinates(latitud: number, longitud: number): Coordinates {
  return {
    latitud: Number(latitud.toFixed(6)),
    longitud: Number(longitud.toFixed(6)),
  };
}
