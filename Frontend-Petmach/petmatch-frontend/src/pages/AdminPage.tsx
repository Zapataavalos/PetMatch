import { AlertTriangle, CheckCircle, MapPin, Shield, Users } from "lucide-react";
import { Card } from "../components/ui/Card";
import { StatusBadge } from "../components/reports/StatusBadge";

const stats = [
  {
    label: "Usuarios Activos",
    value: "2,451",
    change: "+12%",
    icon: Users,
    color: "text-blue-400",
  },
  {
    label: "Reportes Totales",
    value: "843",
    change: "+5%",
    icon: MapPin,
    color: "text-purple-400",
  },
  {
    label: "Casos Resueltos",
    value: "324",
    change: "+18%",
    icon: CheckCircle,
    color: "text-emerald-400",
  },
  {
    label: "En Peligro Crítico",
    value: "12",
    change: "-2%",
    icon: AlertTriangle,
    color: "text-red-400",
  },
];

export function AdminPage() {
  return (
    <section className="mx-auto max-w-7xl px-8 py-12">
      <div className="border-b border-[#24242a] pb-9">
        <div className="flex items-center justify-between gap-6">
          <div>
            <div className="flex items-center gap-4">
              <Shield size={38} className="text-[#f5c400]" />
              <h1 className="text-4xl font-black">Panel de Administración</h1>
            </div>

            <p className="mt-4 text-lg text-[#b8b8c3]">
              Monitorea la actividad del sistema, gestiona reportes y modera
              usuarios.
            </p>
          </div>

          <span className="rounded-full border border-[#f5c400]/30 bg-[#f5c400]/10 px-8 py-2 text-sm font-black text-[#f5c400]">
            SISTEMA OPERATIVO
          </span>
        </div>
      </div>

      <div className="mt-8 flex gap-8 border-b border-[#24242a]">
        {["Vista General", "Gestión de Reportes", "Usuarios", "Configuración del Sistema"].map(
          (tab, index) => (
            <button
              key={tab}
              className={`border-b-2 pb-5 font-bold ${
                index === 0
                  ? "border-[#f5c400] text-[#f5c400]"
                  : "border-transparent text-[#aaaaba]"
              }`}
            >
              {tab}
            </button>
          )
        )}
      </div>

      <div className="mt-9 grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;

          return (
            <Card key={stat.label} className="p-7">
              <div className="flex items-start justify-between">
                <div
                  className={`flex h-14 w-14 items-center justify-center rounded-2xl bg-[#0f0f12] ${stat.color}`}
                >
                  <Icon size={28} />
                </div>

                <span
                  className={`text-sm font-black ${
                    stat.change.startsWith("+")
                      ? "text-[#10b981]"
                      : "text-[#ef4444]"
                  }`}
                >
                  {stat.change} ↗
                </span>
              </div>

              <p className="mt-7 font-bold text-[#aaaaba]">{stat.label}</p>
              <h3 className="mt-3 text-4xl font-black">{stat.value}</h3>
            </Card>
          );
        })}
      </div>

      <div className="mt-8 grid grid-cols-1 gap-8 xl:grid-cols-[1.4fr_0.8fr]">
        <Card className="min-h-[360px] p-7">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-black">
              Actividad de Reportes Últimos 7 días
            </h2>

            <select className="rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 py-3 text-white">
              <option>Esta semana</option>
              <option>Este mes</option>
            </select>
          </div>

          <div className="mt-10 flex h-56 items-end gap-5">
            {[45, 70, 55, 90, 64, 78, 50].map((height, index) => (
              <div key={index} className="flex flex-1 flex-col items-center gap-3">
                <div
                  className="w-full rounded-t-xl bg-[#f5c400]/80"
                  style={{ height: `${height}%` }}
                />
                <span className="text-sm text-[#777783]">D{index + 1}</span>
              </div>
            ))}
          </div>
        </Card>

        <Card className="p-7">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-black">Reportes Recientes</h2>
            <button className="font-bold text-[#f5c400]">Ver todos</button>
          </div>

          <div className="mt-8 space-y-4">
            <div className="rounded-2xl bg-[#111114] p-4">
              <div className="flex items-center gap-3">
                <b>REP-089</b>
                <StatusBadge status="PERDIDO" />
              </div>
              <p className="mt-2 text-sm text-[#8f8f9a]">María G. • Hace 10 min</p>
            </div>

            <div className="rounded-2xl bg-[#111114] p-4">
              <div className="flex items-center gap-3">
                <b>REP-088</b>
                <StatusBadge status="EN_PELIGRO" />
              </div>
              <p className="mt-2 text-sm text-[#8f8f9a]">Carlos R. • Hace 45 min</p>
            </div>
          </div>
        </Card>
      </div>
    </section>
  );
}