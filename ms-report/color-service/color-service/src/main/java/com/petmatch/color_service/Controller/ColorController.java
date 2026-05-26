package com.petmatch.color_service.Controller;

import com.petmatch.color_service.DTO.ColorRequestDTO;
import com.petmatch.color_service.DTO.ColorResponseDTO;
import com.petmatch.color_service.Service.ColorService;
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
        name = "Colores",
        description = "Operaciones para administrar los colores disponibles en el sistema"
)
@RestController
@RequestMapping("/api/v1/colores")
public class ColorController {

    private final ColorService colorService;

    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    @Operation(summary = "Listar colores", description = "Obtiene todos los colores registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<ColorResponseDTO>> listarColores() {
        return ResponseEntity.ok(colorService.listarColores());
    }

    @Operation(summary = "Buscar color por ID", description = "Obtiene un color específico según su identificador.")
    @ApiResponse(responseCode = "200", description = "Color encontrado correctamente")
    @ApiResponse(responseCode = "404", description = "Color no encontrado")
    @GetMapping("/{idColor}")
    public ResponseEntity<ColorResponseDTO> buscarColorPorId(
            @Parameter(description = "ID del color", example = "1")
            @PathVariable Integer idColor
    ) {
        return ResponseEntity.ok(colorService.buscarColorPorId(idColor));
    }

    @Operation(summary = "Crear color", description = "Registra un nuevo color en el sistema.")
    @ApiResponse(responseCode = "201", description = "Color creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos, nombre duplicado o código hexadecimal duplicado")
    @PostMapping
    public ResponseEntity<ColorResponseDTO> crearColor(
            @Valid @RequestBody ColorRequestDTO requestDTO
    ) {
        ColorResponseDTO colorCreado = colorService.crearColor(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(colorCreado);
    }

    @Operation(summary = "Actualizar color", description = "Actualiza la información de un color existente.")
    @ApiResponse(responseCode = "200", description = "Color actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos, nombre duplicado o código hexadecimal duplicado")
    @ApiResponse(responseCode = "404", description = "Color no encontrado")
    @PutMapping("/{idColor}")
    public ResponseEntity<ColorResponseDTO> actualizarColor(
            @Parameter(description = "ID del color", example = "1")
            @PathVariable Integer idColor,

            @Valid @RequestBody ColorRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(colorService.actualizarColor(idColor, requestDTO));
    }

    @Operation(summary = "Eliminar color", description = "Elimina un color existente según su identificador.")
    @ApiResponse(responseCode = "204", description = "Color eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Color no encontrado")
    @DeleteMapping("/{idColor}")
    public ResponseEntity<Void> eliminarColor(
            @Parameter(description = "ID del color", example = "1")
            @PathVariable Integer idColor
    ) {
        colorService.eliminarColor(idColor);
        return ResponseEntity.noContent().build();
    }
}