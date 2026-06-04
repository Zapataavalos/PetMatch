import { describe, expect, it } from "vitest";
import { buildCoordinateLocationLabel, parseClickedCoordinates } from "./reportLocation";

describe("reportLocation", () => {
  it("formats a clicked map location without reference", () => {
    expect(
      buildCoordinateLocationLabel({
        latitud: -33.4489,
        longitud: -70.6693,
      })
    ).toBe("Ubicacion seleccionada (-33.448900, -70.669300)");
  });

  it("keeps the reference when coordinates are available", () => {
    expect(
      buildCoordinateLocationLabel(
        {
          latitud: -33.45,
          longitud: -70.66,
        },
        "Parque Bustamante"
      )
    ).toBe("Parque Bustamante (-33.450000, -70.660000)");
  });

  it("rounds clicked coordinates to six decimals", () => {
    expect(parseClickedCoordinates(-33.44891234, -70.66935678)).toEqual({
      latitud: -33.448912,
      longitud: -70.669357,
    });
  });
});
