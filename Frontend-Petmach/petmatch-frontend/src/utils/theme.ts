export const USER_SETTINGS_KEY = "petmatch_user_settings";
export const USER_SETTINGS_UPDATED_EVENT = "petmatch_user_settings_updated";
export const LIGHT_THEME_CLASS = "petmatch-light";

export function applyThemePreference(modoOscuro: boolean) {
  if (typeof document === "undefined") {
    return;
  }

  const isLightMode = !modoOscuro;

  document.documentElement.classList.toggle(LIGHT_THEME_CLASS, isLightMode);
  document.documentElement.dataset.theme = isLightMode ? "light" : "dark";
  document.documentElement.style.colorScheme = isLightMode ? "light" : "dark";
}

export function applyStoredTheme(fallbackModoOscuro = true) {
  applyThemePreference(readStoredDarkMode(fallbackModoOscuro));
}

function readStoredDarkMode(fallbackModoOscuro: boolean) {
  if (typeof localStorage === "undefined") {
    return fallbackModoOscuro;
  }

  try {
    const stored = localStorage.getItem(USER_SETTINGS_KEY);

    if (!stored) {
      return fallbackModoOscuro;
    }

    const parsed: unknown = JSON.parse(stored);

    if (!isRecord(parsed) || typeof parsed.modoOscuro !== "boolean") {
      return fallbackModoOscuro;
    }

    return parsed.modoOscuro;
  } catch {
    return fallbackModoOscuro;
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}
