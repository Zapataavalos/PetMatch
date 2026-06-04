import { useCallback, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import type { AppLanguage } from "../types";
import {
  CHILE_COUNTRY,
  I18nContext,
  LANGUAGE_KEY,
  translations,
  type I18nContextValue,
  type TranslationKey,
} from "./I18nContextCore";

export function I18nProvider({ children }: { children: ReactNode }) {
  const [language, setLanguageState] = useState<AppLanguage>(readStoredLanguage);

  const setLanguage = useCallback((nextLanguage: AppLanguage) => {
    setLanguageState(nextLanguage);
  }, []);

  useEffect(() => {
    localStorage.setItem(LANGUAGE_KEY, language);
    document.documentElement.lang = language === "ES" ? "es-CL" : "en";
  }, [language]);

  const t = useCallback(
    (key: TranslationKey) => translations[language][key] ?? translations.ES[key],
    [language]
  );

  const value = useMemo<I18nContextValue>(
    () => ({
      language,
      country: CHILE_COUNTRY,
      setLanguage,
      t,
    }),
    [language, setLanguage, t]
  );

  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}

function readStoredLanguage(): AppLanguage {
  const storedLanguage = localStorage.getItem(LANGUAGE_KEY);
  return storedLanguage === "EN" ? "EN" : "ES";
}
