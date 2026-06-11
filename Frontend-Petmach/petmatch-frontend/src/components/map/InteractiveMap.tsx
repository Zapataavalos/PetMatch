import L from "leaflet";
import { CircleCheck } from "lucide-react";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import { useMapEvents } from "react-leaflet";
import type { Coordinates, ReporteResumen, ReportStatus } from "../../types";
import { StatusBadge } from "../reports/StatusBadge";

interface InteractiveMapProps {
  reportes: ReporteResumen[];
  onRescued?: (reporte: ReporteResumen) => void;
  rescuingIds?: ReadonlySet<number>;
  selectedLocation?: Coordinates | null;
  onLocationClick?: (coordinates: Coordinates) => void;
}

const markerColors: Record<ReportStatus, string> = {
  PERDIDO: "#f5c400",
  EN_REFUGIO: "#10b981",
  EN_PELIGRO: "#ef4444",
  ENCONTRADO: "#60a5fa",
};

function createCustomIcon(status: ReportStatus) {
  const color = markerColors[status];

  return L.divIcon({
    className: "custom-pet-marker",
    html: `
      <div style="
        width: 32px;
        height: 32px;
        border-radius: 999px;
        background: ${color};
        border: 4px solid rgba(0,0,0,0.75);
        box-shadow: 0 0 22px ${color};
        display: flex;
        align-items: center;
        justify-content: center;
        color: #000;
        font-size: 15px;
        font-weight: 900;
      ">
        🐾
      </div>
    `,
    iconSize: [32, 32],
    iconAnchor: [16, 16],
    popupAnchor: [0, -18],
  });
}

export function InteractiveMap({
  reportes,
  onRescued,
  rescuingIds = new Set<number>(),
  selectedLocation,
  onLocationClick,
}: InteractiveMapProps) {
  const initialPosition: [number, number] = [-33.4489, -70.6693];

  return (
    <MapContainer
      center={initialPosition}
      zoom={12}
      scrollWheelZoom
      className="z-0"
    >
      <TileLayer
        attribution='&copy; OpenStreetMap contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      <LocationClickHandler onLocationClick={onLocationClick} />

      {selectedLocation && (
        <Marker
          position={[selectedLocation.latitud, selectedLocation.longitud]}
          icon={createSelectedLocationIcon()}
        >
          <Popup>
            <div className="w-[210px]">
              <p className="font-black text-white">Nueva ubicacion</p>
              <p className="mt-2 text-xs font-bold text-[#f5c400]">
                {selectedLocation.latitud.toFixed(6)}, {selectedLocation.longitud.toFixed(6)}
              </p>
            </div>
          </Popup>
        </Marker>
      )}

      {reportes.map((reporte) => (
        <Marker
          key={reporte.id}
          position={[reporte.latitud, reporte.longitud]}
          icon={createCustomIcon(reporte.estado)}
        >
          <Popup>
            <div className="w-[230px]">
              <img
                src={reporte.imagenUrl}
                alt={reporte.nombre}
                className="mb-3 h-28 w-full rounded-xl object-cover"
              />

              <div className="mb-2 flex items-center justify-between gap-2">
                <h3 className="text-lg font-black text-white">
                  {reporte.nombre}
                </h3>

                <StatusBadge status={reporte.estado} />
              </div>

              <p className="text-sm text-[#b8b8c3]">{reporte.descripcion}</p>

              <p className="mt-3 text-xs font-bold text-[#f5c400]">
                {reporte.ubicacion}
              </p>

              <p className="mt-1 text-xs text-[#8f8f9a]">{reporte.tiempo}</p>

              {onRescued && (
                <button
                  type="button"
                  onClick={() => onRescued(reporte)}
                  disabled={rescuingIds.has(reporte.id)}
                  className="mt-4 inline-flex h-10 w-full items-center justify-center gap-2 rounded-lg bg-[#10b981] px-3 text-sm font-black text-black transition hover:bg-[#34d399] disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <CircleCheck size={16} />
                  {rescuingIds.has(reporte.id) ? "Marcando..." : "Rescatado"}
                </button>
              )}
            </div>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
}

function LocationClickHandler({
  onLocationClick,
}: {
  onLocationClick?: (coordinates: Coordinates) => void;
}) {
  useMapEvents({
    click(event) {
      onLocationClick?.({
        latitud: Number(event.latlng.lat.toFixed(6)),
        longitud: Number(event.latlng.lng.toFixed(6)),
      });
    },
  });

  return null;
}

function createSelectedLocationIcon() {
  return L.divIcon({
    className: "custom-selected-location-marker",
    html: `
      <div style="
        width: 38px;
        height: 38px;
        border-radius: 999px;
        background: #f5c400;
        border: 4px solid rgba(0,0,0,0.8);
        box-shadow: 0 0 24px rgba(245,196,0,0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #000;
        font-size: 18px;
        font-weight: 900;
      ">
        +
      </div>
    `,
    iconSize: [38, 38],
    iconAnchor: [19, 19],
    popupAnchor: [0, -20],
  });
}
