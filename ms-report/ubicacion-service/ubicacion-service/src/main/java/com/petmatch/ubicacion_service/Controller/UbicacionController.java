package com.petmatch.ubicacion_service.Controller;

import com.petmatch.ubicacion_service.DTO.UbicacionRequestDTO;
import com.petmatch.ubicacion_service.DTO.UbicacionResponseDTO;
import com.petmatch.ubicacion_service.Service.UbicacionService;
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
        name = "Ubicaciones",
        description = "Operaciones para administrar ubicaciones asociadas a ciudades"
)
@RestController
@RequestMapping("/api/v1/ubicaciones")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @Operation(summary = "Listar ubicaciones", description = "Obtiene todas las ubicaciones registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<UbicacionResponseDTO>> listarUbicaciones() {
        return ResponseEntity.ok(ubicacionService.listarUbicaciones());
    }

    @Operation(summary = "Listar ubicaciones por ciudad", description = "Obtiene ubicaciones asociadas a una ciudad activa.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Ciudad no encontrada o inactiva")
    @GetMapping("/ciudad/{idCiudad}")
    public ResponseEntity<List<UbicacionResponseDTO>> listarUbicacionesPorCiudad(
            @Parameter(description = "ID de la ciudad", example = "1")
            @PathVariable Integer idCiudad
    ) {
        return ResponseEntity.ok(ubicacionService.listarUbicacionesPorCiudad(idCiudad));
    }

    @Operation(summary = "Buscar ubicación por ID", description = "Obtiene una ubicación específica.")
    @ApiResponse(responseCode = "200", description = "Ubicación encontrada correctamente")
    @ApiResponse(responseCode = "404", description = "Ubicación no encontrada")
    @GetMapping("/{idUbicacion}")
    public ResponseEntity<UbicacionResponseDTO> buscarUbicacionPorId(
            @Parameter(description = "ID de la ubicación", example = "1")
            @PathVariable Integer idUbicacion
    ) {
        return ResponseEntity.ok(ubicacionService.buscarUbicacionPorId(idUbicacion));
    }

    @Operation(summary = "Crear ubicación", description = "Registra una nueva ubicación validando la ciudad desde referencia local.")
    @ApiResponse(responseCode = "201", description = "Ubicación creada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o ubicación duplicada")
    @ApiResponse(responseCode = "404", description = "Ciudad no encontrada o inactiva")
    @PostMapping
    public ResponseEntity<UbicacionResponseDTO> crearUbicacion(
            @Valid @RequestBody UbicacionRequestDTO requestDTO
    ) {
        UbicacionResponseDTO ubicacionCreada = ubicacionService.crearUbicacion(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionCreada);
    }

    @Operation(summary = "Actualizar ubicación", description = "Actualiza una ubicación existente.")
    @ApiResponse(responseCode = "200", description = "Ubicación actualizada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o ubicación duplicada")
    @ApiResponse(responseCode = "404", description = "Ubicación o ciudad no encontrada")
    @PutMapping("/{idUbicacion}")
    public ResponseEntity<UbicacionResponseDTO> actualizarUbicacion(
            @Parameter(description = "ID de la ubicación", example = "1")
            @PathVariable Integer idUbicacion,

            @Valid @RequestBody UbicacionRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(ubicacionService.actualizarUbicacion(idUbicacion, requestDTO));
    }

    @Operation(summary = "Eliminar ubicación", description = "Elimina una ubicación existente.")
    @ApiResponse(responseCode = "204", description = "Ubicación eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "Ubicación no encontrada")
    @DeleteMapping("/{idUbicacion}")
    public ResponseEntity<Void> eliminarUbicacion(
            @Parameter(description = "ID de la ubicación", example = "1")
            @PathVariable Integer idUbicacion
    ) {
        ubicacionService.eliminarUbicacion(idUbicacion);
        return ResponseEntity.noContent().build();
    }
}