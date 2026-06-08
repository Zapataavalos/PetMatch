import { isAxiosError } from "axios";
import { ImagePlus, LocateFixed, MapPin, Trash2, X } from "lucide-react";
import { useEffect, useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import { reportApi } from "../../api/reportApi";
import type { Coordinates, ReportApiResponse, ReportStatus } from "../../types";
import { buildCoordinateLocationLabel, parseClickedCoordinates } from "../../utils/reportLocation";
import { Button } from "../ui/Button";
import { Input } from "../ui/Input";

const MAX_IMAGE_SIZE_BYTES = 4 * 1024 * 1024;

interface NewReportModalProps {
  open: boolean;
  selectedLocation?: Coordinates | null;
  onClose: () => void;
  onCreated?: (report: ReportApiResponse) => void;
}

export function NewReportModal({
  open,
  selectedLocation,
  onClose,
  onCreated,
}: NewReportModalProps) {
  const [estado, setEstado] = useState<ReportStatus>("PERDIDO");
  const [nombre, setNombre] = useState("");
  const [ubicacion, setUbicacion] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [imagenData, setImagenData] = useState("");
  const [imagenNombre, setImagenNombre] = useState("");
  const [coordinates, setCoordinates] = useState<Coordinates | null>(null);
  const [locating, setLocating] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!open || !selectedLocation) {
      return;
    }

    setCoordinates(selectedLocation);
    setUbicacion(buildCoordinateLocationLabel(selectedLocation));
    setError("");
  }, [open, selectedLocation]);

  if (!open) {
    return null;
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError("");

    try {
      if (!coordinates) {
        throw new Error("Haz clic en el mapa o usa tu ubicacion actual para publicar el reporte.");
      }

      const report = await reportApi.create({
        nombre: nombre.trim() || "Mascota sin nombre",
        ubicacion: buildCoordinateLocationLabel(coordinates, ubicacion),
        descripcion: descripcion.trim(),
        estado,
        imagenUrl: imagenData || undefined,
        latitud: coordinates.latitud,
        longitud: coordinates.longitud,
      });

      onCreated?.(report);
      resetForm();
      onClose();
    } catch (error) {
      setError(getReportErrorMessage(error));
    } finally {
      setSaving(false);
    }
  };

  const handleUseCurrentLocation = () => {
    setError("");

    if (!navigator.geolocation) {
      setError("Tu navegador no permite obtener la ubicacion.");
      return;
    }

    setLocating(true);

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const nextCoordinates = parseClickedCoordinates(
          position.coords.latitude,
          position.coords.longitude
        );

        setCoordinates(nextCoordinates);
        setUbicacion(buildCoordinateLocationLabel(nextCoordinates, "Mi ubicacion actual"));
        setLocating(false);
      },
      (error) => {
        setError(getGeolocationErrorMessage(error));
        setLocating(false);
      },
      {
        enableHighAccuracy: true,
        maximumAge: 30000,
        timeout: 10000,
      }
    );
  };

  const handleImageChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];

    if (!file) {
      return;
    }

    setError("");

    if (!file.type.startsWith("image/")) {
      setError("Selecciona un archivo de imagen valido.");
      event.target.value = "";
      return;
    }

    if (file.size > MAX_IMAGE_SIZE_BYTES) {
      setError("La foto no puede superar 4 MB.");
      event.target.value = "";
      return;
    }

    const reader = new FileReader();

    reader.onload = () => {
      if (typeof reader.result === "string") {
        setImagenData(reader.result);
        setImagenNombre(file.name);
      }
    };

    reader.onerror = () => {
      setError("No fue posible cargar la foto.");
    };

    reader.readAsDataURL(file);
  };

  const handleRemoveImage = () => {
    setImagenData("");
    setImagenNombre("");
  };

  const resetForm = () => {
    setNombre("");
    setUbicacion("");
    setDescripcion("");
    setImagenData("");
    setImagenNombre("");
    setCoordinates(null);
    setEstado("PERDIDO");
    setError("");
  };

  const handleClose = () => {
    resetForm();
    onClose();
  };

  const types: { value: ReportStatus; label: string; color: string }[] = [
    {
      value: "PERDIDO",
      label: "Perdida",
      color: "bg-[#f5c400]",
    },
    {
      value: "EN_REFUGIO",
      label: "En refugio",
      color: "bg-[#10b981]",
    },
    {
      value: "EN_PELIGRO",
      label: "En peligro",
      color: "bg-[#ef4444]",
    },
  ];

  return (
    <div className="fixed inset-0 z-[1000] flex items-center justify-center bg-black/70 px-4 backdrop-blur-sm">
      <form
        onSubmit={handleSubmit}
        className="max-h-[90vh] w-full max-w-[640px] overflow-hidden rounded-2xl border border-[#2a2a30] bg-[#17171b] shadow-2xl"
      >
        <div className="flex items-center justify-between border-b border-[#24242a] px-7 py-6">
          <h2 className="text-2xl font-black">Nuevo reporte</h2>

          <button
            type="button"
            onClick={handleClose}
            className="flex h-10 w-10 items-center justify-center rounded-full bg-[#0f0f12] text-[#9d9daa] hover:text-white"
          >
            <X size={22} />
          </button>
        </div>

        <div className="max-h-[65vh] space-y-7 overflow-y-auto px-7 py-6">
          {error && (
            <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
              {error}
            </div>
          )}

          <div className="rounded-xl border border-[#f5c400]/25 bg-[#f5c400]/10 p-4 text-sm font-bold text-[#f5c400]">
            La ubicacion del reporte se define con un clic en el mapa o con tu ubicacion actual.
          </div>

          <div>
            <span className="mb-3 block font-bold text-[#a8a8b3]">
              Tipo de reporte
            </span>

            <div className="grid grid-cols-3 gap-3">
              {types.map((item) => (
                <button
                  key={item.value}
                  type="button"
                  onClick={() => setEstado(item.value)}
                  className={`h-20 rounded-xl border font-black transition ${
                    estado === item.value
                      ? "border-[#f5c400] bg-[#f5c400]/15 text-[#f5c400]"
                      : "border-[#292930] bg-[#09090b] text-[#9d9daa] hover:border-[#3a3a42]"
                  }`}
                >
                  <span
                    className={`mx-auto mb-2 block h-4 w-4 rounded-full ${item.color}`}
                  />
                  {item.label}
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
            <Input
              label="Nombre de la mascota"
              placeholder="Ej: Max"
              value={nombre}
              onChange={(event) => setNombre(event.target.value)}
              maxLength={100}
            />

            <div className="block">
              <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                Foto de la mascota
              </span>

              <div className="flex h-14 items-center justify-between gap-3 rounded-xl border border-[#2b2b31] bg-[#151519] px-4">
                <span className="min-w-0 truncate text-sm text-[#aaaaba]">
                  {imagenNombre || "Sin foto seleccionada"}
                </span>

                <label className="inline-flex h-9 shrink-0 cursor-pointer items-center gap-2 rounded-lg bg-[#242429] px-3 text-xs font-black text-white transition hover:bg-[#303036]">
                  <ImagePlus size={15} />
                  Seleccionar
                  <input
                    key={imagenData ? "selected-image" : "empty-image"}
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={handleImageChange}
                    disabled={saving}
                  />
                </label>
              </div>
            </div>
          </div>

          {imagenData && (
            <div className="grid gap-3 rounded-xl border border-[#2b2b31] bg-[#09090b] p-3 sm:grid-cols-[140px_1fr]">
              <img
                src={imagenData}
                alt="Vista previa"
                className="h-28 w-full rounded-lg object-cover sm:w-36"
              />

              <div className="flex min-w-0 flex-col justify-between gap-3">
                <span className="truncate text-sm font-bold text-white">
                  {imagenNombre}
                </span>

                <button
                  type="button"
                  onClick={handleRemoveImage}
                  disabled={saving}
                  className="inline-flex h-10 w-fit items-center gap-2 rounded-lg bg-[#242429] px-3 text-sm font-black text-white transition hover:bg-[#303036] disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <Trash2 size={15} />
                  Quitar
                </button>
              </div>
            </div>
          )}

          <div>
            <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
              Ubicacion
            </span>

            <div className="grid gap-3 sm:grid-cols-[1fr_auto]">
              <div className="flex h-14 items-center gap-3 rounded-xl border border-[#2b2b31] bg-[#09090b] px-4">
                <MapPin size={20} className="shrink-0 text-[#81818b]" />
                <input
                  value={ubicacion}
                  onChange={(event) => setUbicacion(event.target.value)}
                  maxLength={180}
                  placeholder="Haz clic en el mapa para seleccionar la ubicacion"
                  className="min-w-0 flex-1 bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
                />
              </div>
              <button
                type="button"
                onClick={handleUseCurrentLocation}
                disabled={locating || saving}
                className="inline-flex h-14 w-full items-center justify-center gap-2 rounded-xl bg-[#242429] px-4 text-sm font-black text-white transition hover:bg-[#303036] disabled:cursor-not-allowed disabled:opacity-60 sm:w-auto"
              >
                <LocateFixed size={16} />
                {locating ? "Ubicando..." : "Usar mi ubicacion"}
              </button>
            </div>

            {coordinates ? (
              <p className="mt-3 text-sm font-bold text-[#10b981]">
                Coordenadas listas: {coordinates.latitud.toFixed(6)}, {coordinates.longitud.toFixed(6)}
              </p>
            ) : (
              <p className="mt-3 text-sm font-semibold text-[#85858f]">
                Falta seleccionar un punto del mapa.
              </p>
            )}
          </div>

          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
              Descripcion adicional
            </span>

            <textarea
              rows={4}
              value={descripcion}
              onChange={(event) => setDescripcion(event.target.value)}
              required
              maxLength={500}
              placeholder="Describe color, tamano, collar, comportamiento u otra informacion importante..."
              className="w-full resize-none rounded-xl border border-[#2b2b31] bg-[#09090b] p-4 text-white outline-none placeholder:text-[#6f6f79] focus:border-[#f5c400]"
            />
          </label>
        </div>

        <div className="flex items-center justify-end gap-4 border-t border-[#24242a] px-7 py-5">
          <Button type="button" variant="ghost" onClick={handleClose} disabled={saving}>
            Cancelar
          </Button>
          <Button type="submit" disabled={saving}>
            {saving ? "Publicando..." : "Publicar reporte"}
          </Button>
        </div>
      </form>
    </div>
  );
}

interface ApiErrorResponse {
  message?: string;
  errors?: Record<string, string>;
}

function getReportErrorMessage(error: unknown) {
  if (isAxiosError<ApiErrorResponse>(error)) {
    const data = error.response?.data;

    if (data?.errors) {
      return Object.values(data.errors).join(" ");
    }

    if (data?.message) {
      return data.message;
    }
  }

  if (error instanceof Error) {
    return error.message;
  }

  return "No fue posible publicar el reporte.";
}

function getGeolocationErrorMessage(error: GeolocationPositionError) {
  if (error.code === error.PERMISSION_DENIED) {
    return "Permiso de ubicacion denegado. Haz clic en el mapa para seleccionar el punto.";
  }

  if (error.code === error.POSITION_UNAVAILABLE) {
    return "No fue posible detectar tu ubicacion actual.";
  }

  if (error.code === error.TIMEOUT) {
    return "La solicitud de ubicacion tardo demasiado. Intenta nuevamente.";
  }

  return "No fue posible obtener tu ubicacion.";
}
