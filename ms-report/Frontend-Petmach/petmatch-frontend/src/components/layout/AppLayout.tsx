import { Outlet } from "react-router-dom";
import { Navbar } from "./Navbar";

export function AppLayout() {
  return (
    <div className="min-h-screen bg-[#050506] text-white">
      <Navbar />
      <main className="pt-[74px]">
        <Outlet />
      </main>
    </div>
  );
}