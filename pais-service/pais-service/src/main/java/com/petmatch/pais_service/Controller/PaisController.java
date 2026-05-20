package com.petmatch.pais_service.Controller;

import com.petmatch.pais_service.Dto.PaisRequestDTO;
import com.petmatch.pais_service.Dto.PaisResponseDTO;
import com.petmatch.pais_service.Service.PaisService;
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
        name = "Países",
        description = "Operaciones para administrar los países del sistema"
)
@RestController
@RequestMapping("/api/v1/paises")
public class PaisController {

    private final PaisService paisService;

    public PaisController(PaisService paisService) {
        this.paisService = paisService;
    }

    @Operation(
            summary = "Listar países",
            description = "Obtiene todos los países registrados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Listado de países obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<PaisResponseDTO>> listarPaises() {
        return ResponseEntity.ok(paisService.listarPaises());
    }

    @Operation(
            summary = "Buscar país por ID",
            description = "Obtiene la información de un país específico según su identificador."
    )
    @ApiResponse(responseCode = "200", description = "País encontrado correctamente")
    @ApiResponse(responseCode = "404", description = "País no encontrado")
    @GetMapping("/{idPais}")
    public ResponseEntity<PaisResponseDTO> buscarPaisPorId(
            @Parameter(description = "ID del país", example = "1")
            @PathVariable Integer idPais
    ) {
        return ResponseEntity.ok(paisService.buscarPaisPorId(idPais));
    }

    @Operation(
            summary = "Crear país",
            description = "Registra un nuevo país en el sistema."
    )
    @ApiResponse(responseCode = "201", description = "País creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o país duplicado")
    @PostMapping
    public ResponseEntity<PaisResponseDTO> crearPais(
            @Valid @RequestBody PaisRequestDTO requestDTO
    ) {
        PaisResponseDTO paisCreado = paisService.crearPais(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(paisCreado);
    }

    @Operation(
            summary = "Actualizar país",
            description = "Actualiza la información de un país existente."
    )
    @ApiResponse(responseCode = "200", description = "País actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o país duplicado")
    @ApiResponse(responseCode = "404", description = "País no encontrado")
    @PutMapping("/{idPais}")
    public ResponseEntity<PaisResponseDTO> actualizarPais(
            @Parameter(description = "ID del país", example = "1")
            @PathVariable Integer idPais,

            @Valid @RequestBody PaisRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(paisService.actualizarPais(idPais, requestDTO));
    }

    @Operation(
            summary = "Eliminar país",
            description = "Elimina un país existente según su identificador."
    )
    @ApiResponse(responseCode = "204", description = "País eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "País no encontrado")
    @DeleteMapping("/{idPais}")
    public ResponseEntity<Void> eliminarPais(
            @Parameter(description = "ID del país", example = "1")
            @PathVariable Integer idPais
    ) {
        paisService.eliminarPais(idPais);
        return ResponseEntity.noContent().build();
    }
}