import {
  AlertTriangle,
  CheckCircle,
  Clock,
  Heart,
  MapPin,
  PawPrint,
  Plus,
  Search,
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { Card } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { ReportCard } from "../components/reports/ReportCard";
import { mockReportes } from "../data/mockData";

const stats = [
  {
    label: "Reportes activos",
    value: "24",
    icon: MapPin,
    color: "text-[#f5c400]",
  },
  {
    label: "Mascotas registradas",
    value: "12",
    icon: PawPrint,
    color: "text-blue-400",
  },
  {
    label: "Coincidencias",
    value: "8",
    icon: Heart,
    color: "text-pink-400",
  },
  {
    label: "Casos resueltos",
    value: "16",
    icon: CheckCircle,
    color: "text-emerald-400",
  },
];

export function DashboardPage() {
  const navigate = useNavigate();

  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="flex flex-col justify-between gap-6 border-b border-[#24242a] pb-9 lg:flex-row lg:items-center">
        <div>
          <h1 className="text-4xl font-black">Panel Principal</h1>
          <p className="mt-3 text-lg text-[#aaaaba]">
            Revisa la actividad reciente, reportes activos y posibles
            coincidencias.
          </p>
        </div>

        <Button onClick={() => navigate("/nuevo-reporte")}>
          <Plus className="mr-2 inline" size={19} />
          Nuevo Reporte
        </Button>
      </div>

      <div className="mt-9 grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;

          return (
            <Card key={stat.label} className="p-7">
              <div
                className={`flex h-14 w-14 items-center justify-center rounded-2xl bg-[#0f0f12] ${stat.color}`}
              >
                <Icon size={28} />
              </div>

              <p className="mt-7 font-bold text-[#aaaaba]">{stat.label}</p>
              <h3 className="mt-3 text-4xl font-black">{stat.value}</h3>
            </Card>
          );
        })}
      </div>

      <div className="mt-8 grid grid-cols-1 gap-8 xl:grid-cols-[1.2fr_0.8fr]">
        <Card className="p-7">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-black">Reportes recientes</h2>

            <button
              onClick={() => navigate("/reportes")}
              className="font-bold text-[#f5c400]"
            >
              Ver todos
            </button>
          </div>

          <div className="mt-6 space-y-4">
            {mockReportes.map((reporte) => (
              <ReportCard key={reporte.id} reporte={reporte} />
            ))}
          </div>
        </Card>

        <div className="space-y-8">
          <Card className="p-7">
            <div className="flex items-center gap-3">
              <Search className="text-[#f5c400]" />
              <h2 className="text-2xl font-black">Búsqueda rápida</h2>
            </div>

            <p className="mt-3 text-[#aaaaba]">
              Encuentra reportes por nombre, zona o código.
            </p>

            <div className="mt-6 flex h-12 items-center gap-3 rounded-xl border border-[#2a2a30] bg-[#09090b] px-4">
              <Search size={20} className="text-[#85858f]" />
              <input
                placeholder="Buscar reporte..."
                className="w-full bg-transparent text-white outline-none placeholder:text-[#6f6f79]"
              />
            </div>
          </Card>

          <Card className="p-7">
            <div className="flex items-center gap-3">
              <AlertTriangle className="text-red-400" />
              <h2 className="text-2xl font-black">Alertas críticas</h2>
            </div>

            <div className="mt-6 space-y-4">
              <div className="rounded-2xl border border-red-500/20 bg-red-500/10 p-4">
                <p className="font-black text-red-300">Reporte en peligro</p>
                <p className="mt-1 text-sm text-[#aaaaba]">
                  Perro visto cerca de una vía rápida.
                </p>
              </div>

              <div className="rounded-2xl border border-[#f5c400]/20 bg-[#f5c400]/10 p-4">
                <p className="font-black text-[#f5c400]">Coincidencia alta</p>
                <p className="mt-1 text-sm text-[#aaaaba]">
                  Posible match detectado hace pocos minutos.
                </p>
              </div>
            </div>
          </Card>

          <Card className="p-7">
            <div className="flex items-center gap-3">
              <Clock className="text-[#aaaaba]" />
              <h2 className="text-2xl font-black">Actividad reciente</h2>
            </div>

            <div className="mt-6 space-y-4 text-sm text-[#aaaaba]">
              <p>• Nuevo reporte publicado hace 10 minutos.</p>
              <p>• Una mascota fue marcada como resguardada.</p>
              <p>• Se detectó una posible coincidencia.</p>
            </div>
          </Card>
        </div>
      </div>
    </section>
  );
}