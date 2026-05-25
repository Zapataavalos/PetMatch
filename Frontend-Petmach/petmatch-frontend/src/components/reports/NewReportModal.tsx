import { MapPin, Upload, X } from "lucide-react";
import { useState } from "react";
import type { FormEvent } from "react";
import { Button } from "../ui/Button";
import { Input } from "../ui/Input";

interface NewReportModalProps {
  open: boolean;
  onClose: () => void;
}

type ReportType = "PERDIDA" | "RESGUARDADA" | "EN_PELIGRO";

export function NewReportModal({ open, onClose }: NewReportModalProps) {
  const [tipo, setTipo] = useState<ReportType>("PERDIDA");

  if (!open) {
    return null;
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();
    onClose();
  };

  const types: { value: ReportType; label: string; color: string }[] = [
    {
      value: "PERDIDA",
      label: "Perdida",
      color: "bg-[#f5c400]",
    },
    {
      value: "RESGUARDADA",
      label: "Resguardada",
      color: "bg-[#10b981]",
    },
    {
      value: "EN_PELIGRO",
      label: "En Peligro",
      color: "bg-[#ef4444]",
    },
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 px-4 backdrop-blur-sm">
      <form
        onSubmit={handleSubmit}
        className="max-h-[90vh] w-full max-w-[640px] overflow-hidden rounded-2xl border border-[#2a2a30] bg-[#17171b] shadow-2xl"
      >
        <div className="flex items-center justify-between border-b border-[#24242a] px-7 py-6">
          <h2 className="text-2xl font-black">Nuevo Reporte</h2>

          <button
            type="button"
            onClick={onClose}
            className="flex h-10 w-10 items-center justify-center rounded-full bg-[#0f0f12] text-[#9d9daa] hover:text-white"
          >
            <X size={22} />
          </button>
        </div>

        <div className="max-h-[65vh] space-y-7 overflow-y-auto px-7 py-6">
          <div>
            <span className="mb-3 block font-bold text-[#a8a8b3]">
              Tipo de reporte
            </span>

            <div className="grid grid-cols-3 gap-3">
              {types.map((item) => (
                <button
                  key={item.value}
                  type="button"
                  onClick={() => setTipo(item.value)}
                  className={`h-20 rounded-xl border font-black transition ${
                    tipo === item.value
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

          <div>
            <span className="mb-3 block font-bold text-[#a8a8b3]">
              Foto opcional
            </span>

            <div className="flex h-44 flex-col items-center justify-center rounded-xl border border-dashed border-[#34343a] bg-[#111114] text-center">
              <Upload size={34} className="text-[#777783]" />
              <p className="mt-3 text-[#8f8f9a]">
                Sube o arrastra una imagen aquí
              </p>
              <p className="mt-1 text-sm text-[#666672]">
                JPG, PNG hasta 5MB
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
            <label className="block">
              <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                Tipo de Animal
              </span>
              <select className="h-14 w-full rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 text-white outline-none focus:border-[#f5c400]">
                <option>Perro</option>
                <option>Gato</option>
                <option>Otro</option>
              </select>
            </label>

            <Input label="Nombre (si se sabe)" placeholder="Ej: Max" />
          </div>

          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
              Ubicación
            </span>

            <div className="flex h-14 items-center gap-3 rounded-xl border border-[#2b2b31] bg-[#09090b] px-4">
              <MapPin size={20} className="text-[#81818b]" />
              <input
                placeholder="Dirección o punto de referencia"
                className="flex-1 bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
              />

              <button
                type="button"
                className="rounded-lg bg-[#242429] px-3 py-2 text-sm font-black text-white"
              >
                Usar GPS
              </button>
            </div>
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
              Descripción adicional
            </span>

            <textarea
              rows={4}
              placeholder="Describe color, tamaño, collar, comportamiento u otra información importante..."
              className="w-full resize-none rounded-xl border border-[#2b2b31] bg-[#09090b] p-4 text-white outline-none placeholder:text-[#6f6f79] focus:border-[#f5c400]"
            />
          </label>
        </div>

        <div className="flex items-center justify-end gap-4 border-t border-[#24242a] px-7 py-5">
          <Button type="button" variant="ghost" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit">Publicar Reporte</Button>
        </div>
      </form>
    </div>
  );
}