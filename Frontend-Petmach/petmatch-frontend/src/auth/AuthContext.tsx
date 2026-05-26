import { useCallback, useMemo, useState } from "react";
import { authApi } from "../api/authApi";
import type { AuthResponse, LoginRequest, RegisterRequest, UserRole } from "../types";
import { AuthContext, TOKEN_KEY, USER_KEY, type AuthContextValue } from "./AuthContextCore";
import { getRoleFromToken, isAdminRole } from "./jwtUtils";

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthResponse | null>(() => {
    const stored = localStorage.getItem(USER_KEY);
    return stored ? JSON.parse(stored) : null;
  });

  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem(TOKEN_KEY);
  });

  const persistSession = useCallback((response: AuthResponse) => {
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify(response));

    setToken(response.token);
    setUser(response);
  }, []);

  const login = useCallback(
    async (payload: LoginRequest) => {
      const response = await authApi.login(payload);
      persistSession(response);
    },
    [persistSession]
  );

  const register = useCallback(async (payload: RegisterRequest) => {
    await authApi.register(payload);
  }, []);

  const updateSession = useCallback(
    (response: AuthResponse) => {
      persistSession(response);
    },
    [persistSession]
  );

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);

    setToken(null);
    setUser(null);
  }, []);

  const role = useMemo<UserRole | null>(() => {
    const tokenRole = getRoleFromToken(token);

    if (tokenRole) {
      return tokenRole;
    }

    return user?.role ?? user?.rol ?? null;
  }, [token, user]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      role,
      isAuthenticated: Boolean(token),
      isAdmin: isAdminRole(role),
      login,
      register,
      updateSession,
      logout,
    }),
    [user, token, role, login, register, updateSession, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
