package com.petmatch.region_service.Controller;

import com.petmatch.region_service.DTO.RegionRequestDto;
import com.petmatch.region_service.DTO.RegionResponseDto;
import com.petmatch.region_service.Service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Regiones",
        description = "Operaciones para administrar regiones asociadas a países"
)
@RestController
@RequestMapping("/api/v1/regiones")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @Operation(summary = "Listar regiones", description = "Obtiene todas las regiones registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<RegionResponseDto>> listarRegiones() {
        return ResponseEntity.ok(regionService.listarRegiones());
    }

    @Operation(summary = "Listar regiones por país", description = "Obtiene las regiones asociadas a un país activo.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "País no encontrado o inactivo")
    @GetMapping("/pais/{idPais}")
    public ResponseEntity<List<RegionResponseDto>> listarRegionesPorPais(
            @Parameter(description = "ID del país", example = "1")
            @PathVariable Integer idPais
    ) {
        return ResponseEntity.ok(regionService.listarRegionesPorPais(idPais));
    }

    @Operation(summary = "Buscar región por ID", description = "Obtiene una región específica según su identificador.")
    @ApiResponse(responseCode = "200", description = "Región encontrada correctamente")
    @ApiResponse(responseCode = "404", description = "Región no encontrada")
    @GetMapping("/{idRegion}")
    public ResponseEntity<RegionResponseDto> buscarRegionPorId(
            @Parameter(description = "ID de la región", example = "1")
            @PathVariable Integer idRegion
    ) {
        return ResponseEntity.ok(regionService.buscarRegionPorId(idRegion));
    }

    @Operation(summary = "Crear región", description = "Registra una nueva región validando el país desde la referencia local.")
    @ApiResponse(responseCode = "201", description = "Región creada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o región duplicada")
    @ApiResponse(responseCode = "404", description = "País no encontrado o inactivo")
    @PostMapping
    public ResponseEntity<RegionResponseDto> crearRegion(
            @Valid @RequestBody RegionRequestDto requestDTO
    ) {
        RegionResponseDto regionCreada = regionService.crearRegion(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(regionCreada);
    }

    @Operation(summary = "Actualizar región", description = "Actualiza una región existente validando el país desde la referencia local.")
    @ApiResponse(responseCode = "200", description = "Región actualizada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o región duplicada")
    @ApiResponse(responseCode = "404", description = "Región o país no encontrado")
    @PutMapping("/{idRegion}")
    public ResponseEntity<RegionResponseDto> actualizarRegion(
            @Parameter(description = "ID de la región", example = "1")
            @PathVariable Integer idRegion,

            @Valid @RequestBody RegionRequestDto requestDTO
    ) {
        return ResponseEntity.ok(regionService.actualizarRegion(idRegion, requestDTO));
    }

    @Operation(summary = "Eliminar región", description = "Elimina una región existente.")
    @ApiResponse(responseCode = "204", description = "Región eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "Región no encontrada")
    @DeleteMapping("/{idRegion}")
    public ResponseEntity<Void> eliminarRegion(
            @Parameter(description = "ID de la región", example = "1")
            @PathVariable Integer idRegion
    ) {
        regionService.eliminarRegion(idRegion);
        return ResponseEntity.noContent().build();
    }
}