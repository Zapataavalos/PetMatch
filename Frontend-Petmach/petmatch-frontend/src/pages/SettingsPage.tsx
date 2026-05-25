import { Bell, Globe, Lock, Moon, Palette, Save, Shield } from "lucide-react";
import { Card } from "../components/ui/Card";
import { Button } from "../components/ui/Button";

export function SettingsPage() {
  return (
    <section className="mx-auto max-w-6xl px-8 py-12">
      <div className="border-b border-[#24242a] pb-9">
        <div className="flex items-center gap-4">
          <Shield size={38} className="text-[#f5c400]" />
          <h1 className="text-4xl font-black">Configuración</h1>
        </div>

        <p className="mt-4 text-lg text-[#aaaaba]">
          Personaliza la experiencia de usuario, privacidad y preferencias del
          sistema.
        </p>
      </div>

      <div className="mt-10 grid grid-cols-1 gap-8 lg:grid-cols-[280px_1fr]">
        <aside className="space-y-3">
          <button className="flex h-14 w-full items-center gap-4 rounded-xl border border-[#f5c400]/30 bg-[#f5c400]/10 px-5 font-bold text-[#f5c400]">
            <Palette size={22} />
            Preferencias
          </button>

          <button className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#aaaaba] hover:bg-[#17171b]">
            <Bell size={22} />
            Notificaciones
          </button>

          <button className="flex h-14 w-full items-center gap-4 rounded-xl px-5 font-bold text-[#aaaaba] hover:bg-[#17171b]">
            <Lock size={22} />
            Seguridad
          </button>
        </aside>

        <div className="space-y-8">
          <Card className="p-8">
            <div className="flex items-center gap-3">
              <Palette className="text-[#f5c400]" />
              <h2 className="text-2xl font-black">Apariencia</h2>
            </div>

            <div className="mt-8 grid grid-cols-1 gap-6 md:grid-cols-2">
              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                  Color principal
                </span>

                <select className="h-14 w-full rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 text-white outline-none focus:border-[#f5c400]">
                  <option>Amarillo PetTracker</option>
                  <option>Verde rescate</option>
                  <option>Azul comunidad</option>
                </select>
              </label>

              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                  Tema
                </span>

                <div className="flex h-14 items-center gap-3 rounded-xl border border-[#2b2b31] bg-[#09090b] px-4">
                  <Moon size={20} className="text-[#f5c400]" />
                  <span className="font-bold text-white">Modo oscuro</span>
                </div>
              </label>
            </div>
          </Card>

          <Card className="p-8">
            <div className="flex items-center gap-3">
              <Bell className="text-[#f5c400]" />
              <h2 className="text-2xl font-black">Notificaciones</h2>
            </div>

            <div className="mt-8 space-y-5">
              <SettingToggle
                title="Alertas de coincidencias"
                description="Recibir avisos cuando se detecte una posible coincidencia."
                checked
              />

              <SettingToggle
                title="Reportes cercanos"
                description="Recibir notificaciones de mascotas perdidas cerca de tu zona."
                checked
              />

              <SettingToggle
                title="Correos informativos"
                description="Recibir correos sobre actividad importante de la cuenta."
              />
            </div>
          </Card>

          <Card className="p-8">
            <div className="flex items-center gap-3">
              <Globe className="text-[#f5c400]" />
              <h2 className="text-2xl font-black">Idioma y región</h2>
            </div>

            <div className="mt-8 grid grid-cols-1 gap-6 md:grid-cols-2">
              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                  Idioma
                </span>

                <select className="h-14 w-full rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 text-white outline-none focus:border-[#f5c400]">
                  <option>Español</option>
                  <option>English</option>
                </select>
              </label>

              <label className="block">
                <span className="mb-2 block text-sm font-semibold text-[#a8a8b3]">
                  País
                </span>

                <select className="h-14 w-full rounded-xl border border-[#2b2b31] bg-[#09090b] px-4 text-white outline-none focus:border-[#f5c400]">
                  <option>Chile</option>
                  <option>Colombia</option>
                  <option>Argentina</option>
                </select>
              </label>
            </div>
          </Card>

          <div className="flex justify-end">
            <Button>
              <Save className="mr-2 inline" size={18} />
              Guardar Configuración
            </Button>
          </div>
        </div>
      </div>
    </section>
  );
}

function SettingToggle({
  title,
  description,
  checked = false,
}: {
  title: string;
  description: string;
  checked?: boolean;
}) {
  return (
    <div className="flex items-center justify-between gap-6 rounded-2xl border border-[#24242a] bg-[#101013] p-5">
      <div>
        <p className="font-black text-white">{title}</p>
        <p className="mt-1 text-sm text-[#aaaaba]">{description}</p>
      </div>

      <button
        className={`relative h-7 w-12 rounded-full transition ${
          checked ? "bg-[#f5c400]" : "bg-[#33333a]"
        }`}
      >
        <span
          className={`absolute top-1 h-5 w-5 rounded-full bg-white transition ${
            checked ? "left-6" : "left-1"
          }`}
        />
      </button>
    </div>
  );
}