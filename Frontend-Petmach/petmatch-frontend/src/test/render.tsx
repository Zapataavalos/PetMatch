import { render } from "@testing-library/react";
import type { ReactNode } from "react";
import { MemoryRouter } from "react-router-dom";
import { I18nProvider } from "../i18n/I18nContext";

export function renderWithRouter(ui: ReactNode, route = "/") {
  return render(<MemoryRouter initialEntries={[route]}>{ui}</MemoryRouter>);
}

export function renderWithAppProviders(ui: ReactNode, route = "/") {
  return render(
    <MemoryRouter initialEntries={[route]}>
      <I18nProvider>{ui}</I18nProvider>
    </MemoryRouter>
  );
}
