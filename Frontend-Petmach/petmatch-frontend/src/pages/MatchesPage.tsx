import { Heart, Search } from "lucide-react";
import { mockCoincidencias } from "../data/mockData";

export function MatchesPage() {
  return (
    <section className="mx-auto max-w-7xl px-8 py-14">
      <div className="flex flex-col justify-between gap-6 border-b border-[#24242a] pb-8 md:flex-row md:items-center">
        <div>
          <div className="flex items-center gap-4">
            <Heart size={38} className="text-[#f5c400]" />
            <h1 className="text-4xl font-black">Posibles Coincidencias</h1>
          </div>

          <p className="mt-5 max-w-3xl text-lg leading-relaxed text-[#b5b5c2]">
            Nuestro sistema analiza reportes de mascotas perdidas y mascotas
            encontradas para sugerir posibles matches basados en ubicación,
            fecha y características.
          </p>
        </div>

        <div className="flex h-12 w-full items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#17171b] px-4 md:w-[320px]">
          <Search size={20} className="text-[#85858f]" />
          <input
            placeholder="Buscar por ID de reporte..."
            className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
          />
        </div>
      </div>

      <div className="mt-10 grid grid-cols-1 gap-8 md:grid-cols-2 xl:grid-cols-3">
        {mockCoincidencias.map((coincidencia) => (
          <article
            key={coincidencia.id}
            className="overflow-hidden rounded-2xl border border-[#25252b] bg-[#17171b]"
          >
            <div className="relative grid h-44 grid-cols-2">
              <div className="relative">
                <img
                  src={coincidencia.imagenPerdido}
                  className="h-full w-full object-cover"
                />
                <span className="absolute left-3 top-3 rounded bg-[#f5c400] px-2 py-1 text-xs font-black text-black">
                  PERDIDO
                </span>
              </div>

              <div className="relative grayscale">
                <img
                  src={coincidencia.imagenEncontrado}
                  className="h-full w-full object-cover"
                />
                <span className="absolute right-3 top-3 rounded bg-[#10b981] px-2 py-1 text-xs font-black text-black">
                  ENCONTRADO
                </span>
              </div>

              <div className="absolute left-1/2 top-1/2 flex h-12 w-12 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full bg-[#1a1a1f] text-lg font-black text-[#f5c400]">
                {coincidencia.porcentaje}%
              </div>
            </div>

            <div className="p-6">
              <p className="text-lg leading-relaxed text-white">
                <span className="font-black text-[#f5c400]">
                  Coincidencia alta
                </span>{" "}
                detectada cerca de{" "}
                <span className="font-black">{coincidencia.ubicacion}</span>
              </p>

              <button className="mt-6 h-12 w-full rounded-xl bg-[#29292f] font-black text-white hover:bg-[#34343b]">
                Ver Detalles
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}