import { Eye, PawPrint, Plus, Search } from "lucide-react";
import { Card } from "../components/ui/Card";
import { Button } from "../components/ui/Button";

const mockPets = [
  {
    id: 1,
    nombre: "Max",
    tipo: "Perro",
    raza: "Golden Retriever",
    tamano: "Grande",
    estado: "Reportado perdido",
    imagen:
      "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=600&auto=format&fit=crop",
  },
  {
    id: 2,
    nombre: "Luna",
    tipo: "Gato",
    raza: "Mestizo",
    tamano: "Mediano",
    estado: "En refugio",
    imagen:
      "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=600&auto=format&fit=crop",
  },
  {
    id: 3,
    nombre: "Rocky",
    tipo: "Perro",
    raza: "Mestizo",
    tamano: "Mediano",
    estado: "Activo",
    imagen:
      "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop",
  },
];

export function PetsPage() {
  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="flex flex-col justify-between gap-6 border-b border-[#24242a] pb-9 lg:flex-row lg:items-center">
        <div>
          <div className="flex items-center gap-4">
            <PawPrint size={38} className="text-[#f5c400]" />
            <h1 className="text-4xl font-black">Mascotas</h1>
          </div>

          <p className="mt-4 text-lg text-[#aaaaba]">
            Administra mascotas registradas, reportadas o asociadas a usuarios.
          </p>
        </div>

        <Button>
          <Plus className="mr-2 inline" size={19} />
          Nueva Mascota
        </Button>
      </div>

      <div className="mt-8 flex h-12 max-w-md items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#17171b] px-4">
        <Search size={20} className="text-[#85858f]" />
        <input
          placeholder="Buscar por nombre, tipo o raza..."
          className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
        />
      </div>

      <div className="mt-8 grid grid-cols-1 gap-7 md:grid-cols-2 xl:grid-cols-3">
        {mockPets.map((pet) => (
          <Card key={pet.id} className="overflow-hidden">
            <img
              src={pet.imagen}
              alt={pet.nombre}
              className="h-56 w-full object-cover"
            />

            <div className="p-6">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <h2 className="text-2xl font-black">{pet.nombre}</h2>
                  <p className="mt-1 text-[#aaaaba]">
                    {pet.tipo} • {pet.raza}
                  </p>
                </div>

                <span className="rounded-full bg-[#f5c400]/10 px-3 py-1 text-xs font-black text-[#f5c400]">
                  {pet.tamano}
                </span>
              </div>

              <div className="mt-5 rounded-xl border border-[#2a2a30] bg-[#101013] p-4">
                <p className="text-sm font-bold text-[#aaaaba]">Estado</p>
                <p className="mt-1 font-black text-white">{pet.estado}</p>
              </div>

              <button className="mt-5 flex h-12 w-full items-center justify-center gap-2 rounded-xl bg-[#29292f] font-black text-white hover:bg-[#34343b]">
                <Eye size={18} />
                Ver Detalles
              </button>
            </div>
          </Card>
        ))}
      </div>
    </section>
  );
}