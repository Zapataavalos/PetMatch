import {
  LayoutDashboard,
  Map,
  PawPrint,
  Plus,
  Search,
  Settings,
  Shield,
  User,
} from "lucide-react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import { useI18n } from "../../i18n/useI18n";
import { NotificationBell } from "../notifications/NotificationBell";

const baseNavItems = [
  {
    labelKey: "nav.dashboard",
    path: "/dashboard",
    icon: LayoutDashboard,
  },
  {
    labelKey: "nav.map",
    path: "/mapa",
    icon: Map,
  },
  {
    labelKey: "nav.matches",
    path: "/coincidencias",
    icon: Search,
  },
  {
    labelKey: "nav.pets",
    path: "/mascotas",
    icon: PawPrint,
  },
  {
    labelKey: "nav.reports",
    path: "/reportes",
    icon: Shield,
  },
  {
    labelKey: "nav.profile",
    path: "/perfil",
    icon: User,
  },
  {
    labelKey: "nav.settings",
    path: "/configuracion",
    icon: Settings,
  },
] as const;

const adminNavItem = {
  labelKey: "nav.admin",
  path: "/admin",
  icon: Shield,
} as const;

export function Navbar() {
  const { isAuthenticated, logout, isAdmin } = useAuth();
  const { t } = useI18n();
  const navigate = useNavigate();

  const visibleItems = isAdmin
    ? [...baseNavItems, adminNavItem]
    : baseNavItems;

  return (
    <header className="fixed left-0 right-0 top-0 z-40 h-[74px] border-b border-[#202025] bg-[#070708]/95 backdrop-blur">
      <div className="flex h-full items-center justify-between px-7">
        <button
          onClick={() => navigate("/mapa")}
          className="flex items-center gap-3"
        >
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-[#f5c400] text-black">
            <span className="text-lg font-black">🐾</span>
          </div>
          <span className="text-xl font-black tracking-tight text-white">
            PetMatch
          </span>
        </button>

        <nav className="hidden items-center gap-5 xl:flex">
          {visibleItems.map((item) => {
            const Icon = item.icon;

            return (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  `flex items-center gap-2 text-sm font-bold transition ${
                    isActive
                      ? "text-[#f5c400]"
                      : "text-[#a7a7b2] hover:text-white"
                  }`
                }
              >
                <Icon size={17} />
                {t(item.labelKey)}
              </NavLink>
            );
          })}
        </nav>

        <div className="flex items-center gap-5">
          {isAuthenticated ? (
            <button
              onClick={logout}
              className="text-sm font-bold text-[#a7a7b2] hover:text-white"
            >
              {t("nav.logout")}
            </button>
          ) : (
            <button
              onClick={() => navigate("/login")}
              className="text-sm font-bold text-[#d8d8e2] hover:text-white"
            >
              {t("nav.login")}
            </button>
          )}

          <NotificationBell />

          <button
            onClick={() => navigate("/nuevo-reporte")}
            className="flex h-11 items-center gap-2 rounded-xl bg-[#f5c400] px-5 font-black text-black transition hover:bg-[#ffd21a]"
          >
            <Plus size={19} />
            {t("nav.newReport")}
          </button>
        </div>
      </div>
    </header>
  );
}
