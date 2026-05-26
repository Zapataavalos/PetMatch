import { createContext } from "react";
import type { AuthResponse, LoginRequest, RegisterRequest, UserRole } from "../types";

export interface AuthContextValue {
  user: AuthResponse | null;
  token: string | null;
  role: UserRole | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (payload: LoginRequest) => Promise<void>;
  register: (payload: RegisterRequest) => Promise<void>;
  updateSession: (response: AuthResponse) => void;
  logout: () => void;
}

export const USER_KEY = "petmatch_user";
export const TOKEN_KEY = "petmatch_token";

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);
