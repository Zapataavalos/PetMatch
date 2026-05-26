import type { UserRole } from "../types";

interface JwtPayload {
  idUsuario?: number;
  idRol?: number;
  rol?: UserRole;
  role?: UserRole;
  sub?: string;
  exp?: number;
}

export function decodeJwtPayload(token: string): JwtPayload | null {
  try {
    const payloadBase64 = token.split(".")[1];

    if (!payloadBase64) {
      return null;
    }

    const payloadJson = atob(
      payloadBase64.replace(/-/g, "+").replace(/_/g, "/")
    );

    return JSON.parse(payloadJson) as JwtPayload;
  } catch {
    return null;
  }
}

export function getRoleFromToken(token: string | null): UserRole | null {
  if (!token) {
    return null;
  }

  const payload = decodeJwtPayload(token);

  if (!payload) {
    return null;
  }

  return payload.role ?? payload.rol ?? null;
}

export function isAdminRole(role: UserRole | null): boolean {
  return role === "ADMIN";
}