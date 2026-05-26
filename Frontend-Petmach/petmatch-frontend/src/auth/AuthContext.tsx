import { createContext, useContext, useMemo, useState } from "react";
import { authApi } from "../api/authApi";
import type { AuthResponse, LoginRequest, RegisterRequest, UserRole } from "../types";
import { getRoleFromToken, isAdminRole } from "./jwtUtils";

interface AuthContextValue {
  user: AuthResponse | null;
  token: string | null;
  role: UserRole | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (payload: LoginRequest) => Promise<void>;
  register: (payload: RegisterRequest) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const USER_KEY = "petmatch_user";
const TOKEN_KEY = "petmatch_token";

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthResponse | null>(() => {
    const stored = localStorage.getItem(USER_KEY);
    return stored ? JSON.parse(stored) : null;
  });

  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem(TOKEN_KEY);
  });

  const login = async (payload: LoginRequest) => {
    const response = await authApi.login(payload);

    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify(response));

    setToken(response.token);
    setUser(response);
  };

  const register = async (payload: RegisterRequest) => {
    await authApi.register(payload);
  };

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);

    setToken(null);
    setUser(null);
  };

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
      logout,
    }),
    [user, token, role]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth debe usarse dentro de AuthProvider");
  }

  return context;
}