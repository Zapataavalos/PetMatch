package com.petmatch.ciudad_service.Controller;

import com.petmatch.ciudad_service.DTO.CiudadRequestDTO;
import com.petmatch.ciudad_service.DTO.CiudadResponseDTO;
import com.petmatch.ciudad_service.Service.CiudadService;
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
        name = "Ciudades",
        description = "Operaciones para administrar ciudades asociadas a regiones"
)
@RestController
@RequestMapping("/api/v1/ciudades")
public class CiudadController {

    private final CiudadService ciudadService;

    public CiudadController(CiudadService ciudadService) {
        this.ciudadService = ciudadService;
    }

    @Operation(summary = "Listar ciudades", description = "Obtiene todas las ciudades registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<CiudadResponseDTO>> listarCiudades() {
        return ResponseEntity.ok(ciudadService.listarCiudades());
    }

    @Operation(summary = "Listar ciudades por región", description = "Obtiene las ciudades asociadas a una región activa.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Región no encontrada o inactiva")
    @GetMapping("/region/{idRegion}")
    public ResponseEntity<List<CiudadResponseDTO>> listarCiudadesPorRegion(
            @Parameter(description = "ID de la región", example = "1")
            @PathVariable Integer idRegion
    ) {
        return ResponseEntity.ok(ciudadService.listarCiudadesPorRegion(idRegion));
    }

    @Operation(summary = "Buscar ciudad por ID", description = "Obtiene una ciudad específica según su identificador.")
    @ApiResponse(responseCode = "200", description = "Ciudad encontrada correctamente")
    @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    @GetMapping("/{idCiudad}")
    public ResponseEntity<CiudadResponseDTO> buscarCiudadPorId(
            @Parameter(description = "ID de la ciudad", example = "1")
            @PathVariable Integer idCiudad
    ) {
        return ResponseEntity.ok(ciudadService.buscarCiudadPorId(idCiudad));
    }

    @Operation(summary = "Crear ciudad", description = "Registra una nueva ciudad validando la región desde la referencia local.")
    @ApiResponse(responseCode = "201", description = "Ciudad creada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o ciudad duplicada")
    @ApiResponse(responseCode = "404", description = "Región no encontrada o inactiva")
    @PostMapping
    public ResponseEntity<CiudadResponseDTO> crearCiudad(
            @Valid @RequestBody CiudadRequestDTO requestDTO
    ) {
        CiudadResponseDTO ciudadCreada = ciudadService.crearCiudad(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ciudadCreada);
    }

    @Operation(summary = "Actualizar ciudad", description = "Actualiza una ciudad existente validando la región desde la referencia local.")
    @ApiResponse(responseCode = "200", description = "Ciudad actualizada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o ciudad duplicada")
    @ApiResponse(responseCode = "404", description = "Ciudad o región no encontrada")
    @PutMapping("/{idCiudad}")
    public ResponseEntity<CiudadResponseDTO> actualizarCiudad(
            @Parameter(description = "ID de la ciudad", example = "1")
            @PathVariable Integer idCiudad,

            @Valid @RequestBody CiudadRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(ciudadService.actualizarCiudad(idCiudad, requestDTO));
    }

    @Operation(summary = "Eliminar ciudad", description = "Elimina una ciudad existente.")
    @ApiResponse(responseCode = "204", description = "Ciudad eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    @DeleteMapping("/{idCiudad}")
    public ResponseEntity<Void> eliminarCiudad(
            @Parameter(description = "ID de la ciudad", example = "1")
            @PathVariable Integer idCiudad
    ) {
        ciudadService.eliminarCiudad(idCiudad);
        return ResponseEntity.noContent().build();
    }
}