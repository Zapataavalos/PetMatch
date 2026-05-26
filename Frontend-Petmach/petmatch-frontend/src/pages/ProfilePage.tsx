import { LogOut, Settings, Shield, User } from "lucide-react";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { Input } from "../components/ui/Input";
import { useAuth } from "../auth/AuthContext";

export function ProfilePage() {
  const { user, logout } = useAuth();

  const nombreCompleto = user?.nombre ?? "Juan Pérez";
  const partes = nombreCompleto.split(" ");
  const nombre = partes[0] ?? "Juan";
  const apellido = partes.slice(1).join(" ") || "Pérez";

  return (
    <section className="mx-auto max-w-6xl px-8 py-14">
      <div className="flex items-center gap-8 border-b border-[#24242a] pb-10">
        <div className="flex h-32 w-32 items-center justify-center rounded-full border border-[#f5c400] text-[#8d8d98]">
          <User size={56} />
        </div>

        <div>
          <h1 className="text-4xl font-black">{nombreCompleto}</h1>
          <p className="mt-2 text-lg text-[#aaaaba]">
            Voluntario Activo • Miembro desde 2023
          </p>

          <div className="mt-5 flex gap-4">
            <span className="rounded-full border border-[#2a2a30] bg-[#17171b] px-4 py-2">
              <b className="text-[#f5c400]">12</b> reportes
            </span>
            <span className="rounded-full border border-[#2a2a30] bg-[#17171b] px-4 py-2">
              <b className="text-[#10b981]">5</b> reunidos
            </span>
          </div>
        </div>
      </div>

      <div className="mt-10 grid grid-cols-1 gap-8 lg:grid-cols-[280px_1fr]">
        <aside className="space-y-3">
          <button className="flex h-14 w-full items-center gap-4 rounded-xl border border-[#f5c400]/30 bg-[#f5c400]/10 px-5 font-bold text-[#f5c400]">
            <User size={22} />
            Información Personal
          </button>

          <button className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#aaaaba] hover:bg-[#17171b]">
            <Shield size={22} />
            Privacidad y Seguridad
          </button>

          <button className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#aaaaba] hover:bg-[#17171b]">
            <Settings size={22} />
            Preferencias
          </button>

          <button
            onClick={logout}
            className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#ef4444] hover:bg-[#ef4444]/10"
          >
            <LogOut size={22} />
            Cerrar Sesión
          </button>
        </aside>

        <Card className="p-8">
          <h2 className="text-3xl font-black">Información Personal</h2>
          <div className="mt-6 h-px bg-[#292930]" />

          <div className="mt-8 grid grid-cols-1 gap-6 md:grid-cols-2">
            <Input label="Nombre" value={nombre} readOnly />
            <Input label="Apellido" value={apellido} readOnly />
          </div>

          <div className="mt-6">
            <Input
              label="Correo Electrónico"
              value={user?.email ?? "juan.perez@ejemplo.com"}
              readOnly
            />
          </div>

          <div className="mt-6">
            <Input label="Teléfono opcional" placeholder="+57 300 123 45 67" />
          </div>

          <div className="mt-10 flex justify-end">
            <Button>Guardar Cambios</Button>
          </div>
        </Card>
      </div>
    </section>
  );
}