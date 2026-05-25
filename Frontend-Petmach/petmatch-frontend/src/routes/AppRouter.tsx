import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "../auth/ProtectedRoute";
import { AppLayout } from "../components/layout/AppLayout";
import { AdminPage } from "../pages/AdminPage";
import { DashboardPage } from "../pages/DashboardPage";
import { LoginPage } from "../pages/LoginPage";
import { MapPage } from "../pages/MapPage";
import { MatchesPage } from "../pages/MatchesPage";
import { PetsPage } from "../pages/PetsPage";
import { ProfilePage } from "../pages/ProfilePage";
import { RegisterPage } from "../pages/RegisterPage";
import { ReportsPage } from "../pages/ReportsPage";
import { SettingsPage } from "../pages/SettingsPage";

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/mapa" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registro" element={<RegisterPage />} />

        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/mapa" element={<MapPage />} />
            <Route path="/coincidencias" element={<MatchesPage />} />
            <Route path="/mascotas" element={<PetsPage />} />
            <Route path="/reportes" element={<ReportsPage />} />
            <Route path="/perfil" element={<ProfilePage />} />
            <Route path="/configuracion" element={<SettingsPage />} />
            <Route path="/admin" element={<AdminPage />} />
            <Route path="/nuevo-reporte" element={<ReportsPage />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/mapa" replace />} />
      </Routes>
    </BrowserRouter>
  );
}