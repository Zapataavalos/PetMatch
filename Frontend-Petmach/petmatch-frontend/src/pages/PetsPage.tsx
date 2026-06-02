import { isAxiosError } from "axios";
import {
  AlertTriangle,
  Edit3,
  Eye,
  ImagePlus,
  PawPrint,
  Plus,
  RefreshCw,
  Search,
  Trash2,
  X,
} from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import { petApi } from "../api/petApi";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { Input } from "../components/ui/Input";
import type { PetApiResponse, PetCreateRequest, PetStatus } from "../types";

const MAX_IMAGE_SIZE_BYTES = 4 * 1024 * 1024;

type PetFilter = "TODOS" | PetStatus;

const statusLabels: Record<PetStatus, string> = {
  ACTIVO: "Activo",
  REPORTADO_PERDIDO: "Reportado perdido",
  EN_REFUGIO: "En refugio",
};

const statusClasses: Record<PetStatus, string> = {
  ACTIVO: "bg-[#10b981]/15 text-[#10b981]",
  REPORTADO_PERDIDO: "bg-[#f5c400]/15 text-[#f5c400]",
  EN_REFUGIO: "bg-[#60a5fa]/15 text-[#60a5fa]",
};

export function PetsPage() {
  const [pets, setPets] = useState<PetApiResponse[]>([]);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState<PetFilter>("TODOS");
  const [modalPet, setModalPet] = useState<PetApiResponse | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [detailsPet, setDetailsPet] = useState<PetApiResponse | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadPets = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await petApi.getAll();
      setPets(data);
    } catch {
      setError("No fue posible cargar las mascotas registradas.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadPets();
  }, [loadPets]);

  const filteredPets = useMemo(() => {
    const query = search.trim().toLowerCase();

    return pets.filter((pet) => {
      const matchesFilter = filter === "TODOS" || pet.estado === filter;
      const matchesSearch =
        !query ||
        pet.nombre.toLowerCase().includes(query) ||
        pet.tipo.toLowerCase().includes(query) ||
        pet.raza.toLowerCase().includes(query) ||
        pet.tamano.toLowerCase().includes(query);

      return matchesFilter && matchesSearch;
    });
  }, [filter, pets, search]);

  const counts = useMemo(
    () => ({
      TODOS: pets.length,
      ACTIVO: pets.filter((pet) => pet.estado === "ACTIVO").length,
      REPORTADO_PERDIDO: pets.filter((pet) => pet.estado === "REPORTADO_PERDIDO").length,
      EN_REFUGIO: pets.filter((pet) => pet.estado === "EN_REFUGIO").length,
    }),
    [pets]
  );

  const handleSaved = (pet: PetApiResponse) => {
    setPets((current) => {
      const exists = current.some((item) => item.id === pet.id);

      if (exists) {
        return current.map((item) => (item.id === pet.id ? pet : item));
      }

      return [pet, ...current];
    });
  };

  const handleDelete = async (pet: PetApiResponse) => {
    const confirmed = window.confirm(`Eliminar a ${pet.nombre}?`);

    if (!confirmed) {
      return;
    }

    setError("");
    setDeletingId(pet.id);

    try {
      await petApi.delete(pet.id);
      setPets((current) => current.filter((item) => item.id !== pet.id));
      setDetailsPet((current) => (current?.id === pet.id ? null : current));
    } catch {
      setError("No fue posible eliminar la mascota.");
    } finally {
      setDeletingId(null);
    }
  };

  const openCreateModal = () => {
    setModalPet(null);
    setModalOpen(true);
  };

  const openEditModal = (pet: PetApiResponse) => {
    setModalPet(pet);
    setModalOpen(true);
  };

  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="flex flex-col justify-between gap-6 border-b border-[#24242a] pb-9 lg:flex-row lg:items-center">
        <div>
          <div className="flex items-center gap-4">
            <PawPrint size={38} className="text-[#f5c400]" />
            <h1 className="text-4xl font-black">Mascotas</h1>
          </div>

          <p className="mt-4 text-lg text-[#aaaaba]">
            Administra mascotas registradas desde el servicio real de mascotas.
          </p>
        </div>

        <div className="flex flex-col gap-3 sm:flex-row">
          <Button type="button" variant="secondary" onClick={() => void loadPets()}>
            <RefreshCw className="mr-2 inline" size={18} />
            Actualizar
          </Button>
          <Button type="button" onClick={openCreateModal}>
            <Plus className="mr-2 inline" size={19} />
            Nueva Mascota
          </Button>
        </div>
      </div>

      <div className="mt-8 flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">
        <div className="flex h-12 w-full items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#17171b] px-4 lg:w-[420px]">
          <Search size={20} className="text-[#85858f]" />
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Buscar por nombre, tipo, raza o tamano..."
            className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
          />
        </div>

        <div className="flex flex-wrap gap-2">
          <FilterButton active={filter === "TODOS"} onClick={() => setFilter("TODOS")}>
            Todas ({counts.TODOS})
          </FilterButton>
          <FilterButton active={filter === "ACTIVO"} onClick={() => setFilter("ACTIVO")}>
            Activas ({counts.ACTIVO})
          </FilterButton>
          <FilterButton
            active={filter === "REPORTADO_PERDIDO"}
            onClick={() => setFilter("REPORTADO_PERDIDO")}
          >
            Perdidas ({counts.REPORTADO_PERDIDO})
          </FilterButton>
          <FilterButton
            active={filter === "EN_REFUGIO"}
            onClick={() => setFilter("EN_REFUGIO")}
          >
            En refugio ({counts.EN_REFUGIO})
          </FilterButton>
        </div>
      </div>

      {error && (
        <div className="mt-6 flex items-center gap-3 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">
          <AlertTriangle size={18} />
          {error}
        </div>
      )}

      {loading && (
        <div className="mt-8 rounded-2xl border border-[#24242a] bg-[#17171b] p-6 text-[#aaaaba]">
          Cargando mascotas...
        </div>
      )}

      {!loading && filteredPets.length === 0 && (
        <div className="mt-8 rounded-2xl border border-[#24242a] bg-[#17171b] p-8">
          <h2 className="text-2xl font-black">No hay mascotas para mostrar</h2>
          <p className="mt-3 text-[#aaaaba]">
            Crea una mascota nueva o ajusta los filtros de busqueda.
          </p>
        </div>
      )}

      {!loading && filteredPets.length > 0 && (
        <div className="mt-8 grid grid-cols-1 gap-7 md:grid-cols-2 xl:grid-cols-3">
          {filteredPets.map((pet) => (
            <PetCard
              key={pet.id}
              pet={pet}
              deleting={deletingId === pet.id}
              onDetails={() => setDetailsPet(pet)}
              onEdit={() => openEditModal(pet)}
              onDelete={() => void handleDelete(pet)}
            />
          ))}
        </div>
      )}

      {modalOpen && (
        <PetFormModal
          pet={modalPet}
          onClose={() => setModalOpen(false)}
          onSaved={handleSaved}
        />
      )}

      {detailsPet && (
        <PetDetailsModal
          pet={detailsPet}
          deleting={deletingId === detailsPet.id}
          onClose={() => setDetailsPet(null)}
          onEdit={() => {
            setDetailsPet(null);
            openEditModal(detailsPet);
          }}
          onDelete={() => void handleDelete(detailsPet)}
        />
      )}
    </section>
  );
}

function PetCard({
  pet,
  deleting,
  onDetails,
  onEdit,
  onDelete,
}: {
  pet: PetApiResponse;
  deleting: boolean;
  onDetails: () => void;
  onEdit: () => void;
  onDelete: () => void;
}) {
  return (
    <Card className="overflow-hidden">
      <img src={pet.imagenUrl} alt={pet.nombre} className="h-56 w-full object-cover" />

      <div className="p-6">
        <div className="flex items-start justify-between gap-4">
          <div className="min-w-0">
            <h2 className="truncate text-2xl font-black">{pet.nombre}</h2>
            <p className="mt-1 truncate text-[#aaaaba]">
              {pet.tipo} - {pet.raza}
            </p>
          </div>

          <span className="shrink-0 rounded-full bg-[#f5c400]/10 px-3 py-1 text-xs font-black text-[#f5c400]">
            {pet.tamano}
          </span>
        </div>

        <div className="mt-5 rounded-xl border border-[#2a2a30] bg-[#101013] p-4">
          <p className="text-sm font-bold text-[#aaaaba]">Estado</p>
          <p className={`mt-2 inline-flex rounded-full px-3 py-1 text-xs font-black ${statusClasses[pet.estado]}`}>
            {statusLabels[pet.estado]}
          </p>
        </div>

        <div className="mt-5 grid grid-cols-3 gap-2">
          <button
            type="button"
            onClick={onDetails}
            className="flex h-11 items-center justify-center gap-2 rounded-xl bg-[#29292f] text-sm font-black text-white hover:bg-[#34343b]"
          >
            <Eye size={16} />
            Ver
          </button>
          <button
            type="button"
            onClick={onEdit}
            className="flex h-11 items-center justify-center gap-2 rounded-xl bg-[#29292f] text-sm font-black text-white hover:bg-[#34343b]"
          >
            <Edit3 size={16} />
            Editar
          </button>
          <button
            type="button"
            onClick={onDelete}
            disabled={deleting}
            className="flex h-11 items-center justify-center gap-2 rounded-xl bg-[#ef4444] text-sm font-black text-white hover:bg-[#dc2626] disabled:cursor-not-allowed disabled:opacity-60"
          >
            <Trash2 size={16} />
            {deleting ? "..." : "Borrar"}
          </button>
        </div>
      </div>
    </Card>
  );
}

function PetFormModal({
  pet,
  onClose,
  onSaved,
}: {
  pet: PetApiResponse | null;
  onClose: () => void;
  onSaved: (pet: PetApiResponse) => void;
}) {
  const [nombre, setNombre] = useState(pet?.nombre ?? "");
  const [tipo, setTipo] = useState(pet?.tipo ?? "Perro");
  const [raza, setRaza] = useState(pet?.raza ?? "");
  const [tamano, setTamano] = useState(pet?.tamano ?? "Mediano");
  const [estado, setEstado] = useState<PetStatus>(pet?.estado ?? "ACTIVO");
  const [descripcion, setDescripcion] = useState(pet?.descripcion ?? "");
  const [imagenData, setImagenData] = useState(pet?.imagenUrl ?? "");
  const [imagenNombre, setImagenNombre] = useState(pet ? "Imagen actual" : "");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setError("");

    const payload: PetCreateRequest = {
      nombre: nombre.trim(),
      tipo: tipo.trim(),
      raza: raza.trim(),
      tamano: tamano.trim(),
      estado,
      descripcion: descripcion.trim(),
      imagenUrl: imagenData || undefined,
    };

    try {
      const saved = pet
        ? await petApi.update(pet.id, payload)
        : await petApi.create(payload);

      onSaved(saved);
      onClose();
    } catch (error) {
      setError(getPetErrorMessage(error));
    } finally {
      setSaving(false);
    }
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

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 px-4 backdrop-blur-sm">
      <form
        onSubmit={handleSubmit}
        className="max-h-[90vh] w-full max-w-[720px] overflow-hidden rounded-2xl border border-[#2a2a30] bg-[#17171b] shadow-2xl"
      >
        <div className="flex items-center justify-between border-b border-[#24242a] px-7 py-6">
          <h2 className="text-2xl font-black">
            {pet ? "Editar mascota" : "Nueva mascota"}
          </h2>

          <button
            type="button"
            onClick={onClose}
            className="flex h-10 w-10 items-center justify-center rounded-full bg-[#0f0f12] text-[#9d9daa] hover:text-white"
          >
            <X size={22} />
          </button>
        </div>

        <div className="max-h-[65vh] space-y-6 overflow-y-auto px-7 py-6">
          {error && (
            <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
              {error}
            </div>
          )}

          <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
            <Input
              label="Nombre"
              value={nombre}
              onChange={(event) => setNombre(event.target.value)}
              placeholder="Ej: Max"
              maxLength={100}
              required
            />
            <Input
              label="Raza"
              value={raza}
              onChange={(event) => setRaza(event.target.value)}
              placeholder="Ej: Mestizo"
              maxLength={100}
              required
            />
          </div>

          <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
            <SelectField
              label="Tipo"
              value={tipo}
              onChange={setTipo}
              options={["Perro", "Gato", "Otro"]}
            />
            <SelectField
              label="Tamano"
              value={tamano}
              onChange={setTamano}
              options={["Pequeno", "Mediano", "Grande"]}
            />
            <label className="block">
              <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                Estado
              </span>
              <select
                value={estado}
                onChange={(event) => setEstado(event.target.value as PetStatus)}
                className="h-14 w-full rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 text-white outline-none focus:border-[#f5c400]"
              >
                <option value="ACTIVO">Activo</option>
                <option value="REPORTADO_PERDIDO">Reportado perdido</option>
                <option value="EN_REFUGIO">En refugio</option>
              </select>
            </label>
          </div>

          <div>
            <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
              Foto de la mascota
            </span>

            <div className="grid gap-3 rounded-xl border border-[#2b2b31] bg-[#09090b] p-3 sm:grid-cols-[140px_1fr]">
              <img
                src={imagenData || "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop"}
                alt="Vista previa"
                className="h-28 w-full rounded-lg object-cover sm:w-36"
              />

              <div className="flex min-w-0 flex-col justify-between gap-3">
                <span className="truncate text-sm font-bold text-white">
                  {imagenNombre || "Sin foto seleccionada"}
                </span>

                <label className="inline-flex h-10 w-fit cursor-pointer items-center gap-2 rounded-lg bg-[#242429] px-3 text-sm font-black text-white transition hover:bg-[#303036]">
                  <ImagePlus size={15} />
                  Seleccionar foto
                  <input
                    key={imagenData ? "selected-pet-image" : "empty-pet-image"}
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

          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
              Descripcion
            </span>
            <textarea
              rows={4}
              value={descripcion}
              onChange={(event) => setDescripcion(event.target.value)}
              required
              maxLength={500}
              placeholder="Describe color, caracteristicas, collar, comportamiento..."
              className="w-full resize-none rounded-xl border border-[#2b2b31] bg-[#09090b] p-4 text-white outline-none placeholder:text-[#6f6f79] focus:border-[#f5c400]"
            />
          </label>
        </div>

        <div className="flex items-center justify-end gap-4 border-t border-[#24242a] px-7 py-5">
          <Button type="button" variant="ghost" onClick={onClose} disabled={saving}>
            Cancelar
          </Button>
          <Button type="submit" disabled={saving}>
            {saving ? "Guardando..." : "Guardar mascota"}
          </Button>
        </div>
      </form>
    </div>
  );
}

function PetDetailsModal({
  pet,
  deleting,
  onClose,
  onEdit,
  onDelete,
}: {
  pet: PetApiResponse;
  deleting: boolean;
  onClose: () => void;
  onEdit: () => void;
  onDelete: () => void;
}) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 px-4 backdrop-blur-sm">
      <div className="max-h-[90vh] w-full max-w-3xl overflow-hidden rounded-2xl border border-[#2a2a30] bg-[#17171b] shadow-2xl">
        <div className="flex items-center justify-between border-b border-[#24242a] px-7 py-6">
          <h2 className="text-2xl font-black">Detalle de mascota</h2>
          <button
            type="button"
            onClick={onClose}
            className="flex h-10 w-10 items-center justify-center rounded-full bg-[#0f0f12] text-[#9d9daa] hover:text-white"
          >
            <X size={22} />
          </button>
        </div>

        <div className="max-h-[65vh] overflow-y-auto">
          <img src={pet.imagenUrl} alt={pet.nombre} className="h-72 w-full object-cover" />

          <div className="p-7">
            <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-start">
              <div>
                <h3 className="text-3xl font-black">{pet.nombre}</h3>
                <p className="mt-2 text-[#aaaaba]">
                  {pet.tipo} - {pet.raza} - {pet.tamano}
                </p>
              </div>

              <span className={`w-fit rounded-full px-3 py-1 text-xs font-black ${statusClasses[pet.estado]}`}>
                {statusLabels[pet.estado]}
              </span>
            </div>

            <div className="mt-6 rounded-xl border border-[#2a2a30] bg-[#09090b] p-4">
              <p className="text-sm font-bold text-[#aaaaba]">Descripcion</p>
              <p className="mt-2 leading-relaxed text-white">{pet.descripcion}</p>
            </div>
          </div>
        </div>

        <div className="flex flex-col justify-end gap-3 border-t border-[#24242a] px-7 py-5 sm:flex-row">
          <Button type="button" variant="secondary" onClick={onEdit}>
            <Edit3 className="mr-2 inline" size={18} />
            Editar
          </Button>
          <Button type="button" variant="danger" onClick={onDelete} disabled={deleting}>
            <Trash2 className="mr-2 inline" size={18} />
            {deleting ? "Eliminando..." : "Eliminar"}
          </Button>
        </div>
      </div>
    </div>
  );
}

function SelectField({
  label,
  value,
  onChange,
  options,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: string[];
}) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
        {label}
      </span>
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="h-14 w-full rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 text-white outline-none focus:border-[#f5c400]"
      >
        {options.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>
    </label>
  );
}

function FilterButton({
  active,
  onClick,
  children,
}: {
  active: boolean;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`rounded-full border px-4 py-2 text-sm font-bold ${
        active
          ? "border-[#383840] bg-[#242429] text-white"
          : "border-[#2a2a30] text-[#9c9ca8]"
      }`}
    >
      {children}
    </button>
  );
}

interface ApiErrorResponse {
  message?: string;
  errors?: Record<string, string>;
}

function getPetErrorMessage(error: unknown) {
  if (isAxiosError<ApiErrorResponse>(error)) {
    const data = error.response?.data;

    if (data?.errors) {
      return Object.values(data.errors).join(" ");
    }

    if (data?.message) {
      return data.message;
    }
  }

  return "No fue posible guardar la mascota.";
}
