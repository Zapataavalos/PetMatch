import { ArrowRight, Lock, Mail } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import type { FormEvent } from "react";
import { Input } from "../components/ui/Input";
import { Button } from "../components/ui/Button";
import { useAuth } from "../auth/useAuth";

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      await login({ email, contrasena });
      navigate("/mapa");
    } catch {
      setError("Credenciales inválidas o servidor no disponible.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="grid min-h-screen grid-cols-1 bg-[#050506] lg:grid-cols-2">
      <div className="relative hidden overflow-hidden border-r border-[#1d1d22] lg:block">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,rgba(255,255,255,0.08),transparent_35%)]" />
        <div className="absolute inset-0 bg-black/70" />

        <div className="relative z-10 flex h-full flex-col justify-between p-14">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[#f5c400] text-xl">
              🐾
            </div>
            <span className="text-2xl font-black">PetTracker</span>
          </div>

          <div className="max-w-[540px] pb-10">
            <h1 className="text-5xl font-black leading-tight">
              Cada segundo cuenta cuando alguien te busca.
            </h1>
            <p className="mt-6 text-xl leading-relaxed text-[#b7b7c5]">
              Únete a la red comunitaria más grande de búsqueda y rescate de
              mascotas. Juntos logramos más reencuentros.
            </p>
          </div>
        </div>
      </div>

      <div className="flex items-center justify-center px-6 py-12">
        <form onSubmit={handleSubmit} className="w-full max-w-[560px]">
          <h2 className="text-4xl font-black">Bienvenido de vuelta</h2>
          <p className="mt-3 text-lg text-[#aaaaba]">
            Ingresa tus credenciales para acceder a tu cuenta.
          </p>

          <div className="mt-10 space-y-6">
            <Input
              label="Correo electrónico"
              icon={<Mail size={21} />}
              placeholder="tucorreo@ejemplo.com"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />

            <div>
              <div className="mb-2 flex items-center justify-between">
                <span className="text-sm font-semibold text-[#a8a8b3]">
                  Contraseña
                </span>
                <button
                  type="button"
                  className="text-sm font-bold text-[#f5c400]"
                >
                  ¿Olvidaste tu contraseña?
                </button>
              </div>

              <Input
                icon={<Lock size={21} />}
                placeholder="••••••••"
                type="password"
                value={contrasena}
                onChange={(e) => setContrasena(e.target.value)}
                required
              />
            </div>

            {error && (
              <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
                {error}
              </div>
            )}

            <Button type="submit" disabled={loading} className="w-full">
              {loading ? "Iniciando..." : "Iniciar Sesión"}
              <ArrowRight className="ml-2 inline" size={18} />
            </Button>
          </div>

          <p className="mt-10 text-center text-[#a8a8b3]">
            ¿No tienes una cuenta?{" "}
            <Link to="/registro" className="font-bold text-[#f5c400]">
              Regístrate
            </Link>
          </p>
        </form>
      </div>
    </section>
  );
}
