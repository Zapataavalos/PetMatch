package com.petmatch.configuracion_usuario_service.Controller;

import com.petmatch.configuracion_usuario_service.DTO.ConfiguracionUsuarioRequestDTO;
import com.petmatch.configuracion_usuario_service.DTO.ConfiguracionUsuarioResponseDTO;
import com.petmatch.configuracion_usuario_service.Service.ConfiguracionUsuarioService;
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
        name = "Configuraciones de Usuario",
        description = "Operaciones para administrar configuraciones personalizadas de usuario"
)
@RestController
@RequestMapping("/api/v1/configuraciones-usuario")
public class ConfiguracionUsuarioController {

    private final ConfiguracionUsuarioService configuracionUsuarioService;

    public ConfiguracionUsuarioController(ConfiguracionUsuarioService configuracionUsuarioService) {
        this.configuracionUsuarioService = configuracionUsuarioService;
    }

    @Operation(summary = "Listar configuraciones", description = "Obtiene todas las configuraciones registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<ConfiguracionUsuarioResponseDTO>> listarConfiguraciones() {
        return ResponseEntity.ok(configuracionUsuarioService.listarConfiguraciones());
    }

    @Operation(summary = "Buscar configuración por ID", description = "Obtiene una configuración específica.")
    @ApiResponse(responseCode = "200", description = "Configuración encontrada correctamente")
    @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    @GetMapping("/{idConfiguracionUsuario}")
    public ResponseEntity<ConfiguracionUsuarioResponseDTO> buscarConfiguracionPorId(
            @Parameter(description = "ID de la configuración", example = "1")
            @PathVariable Integer idConfiguracionUsuario
    ) {
        return ResponseEntity.ok(configuracionUsuarioService.buscarConfiguracionPorId(idConfiguracionUsuario));
    }

    @Operation(summary = "Buscar configuración por usuario", description = "Obtiene la configuración asociada a un usuario.")
    @ApiResponse(responseCode = "200", description = "Configuración encontrada correctamente")
    @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<ConfiguracionUsuarioResponseDTO> buscarConfiguracionPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer idUsuario
    ) {
        return ResponseEntity.ok(configuracionUsuarioService.buscarConfiguracionPorUsuario(idUsuario));
    }

    @Operation(summary = "Crear configuración", description = "Registra una nueva configuración para un usuario activo.")
    @ApiResponse(responseCode = "201", description = "Configuración creada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o configuración duplicada")
    @ApiResponse(responseCode = "404", description = "Usuario o color no encontrado")
    @PostMapping
    public ResponseEntity<ConfiguracionUsuarioResponseDTO> crearConfiguracion(
            @Valid @RequestBody ConfiguracionUsuarioRequestDTO requestDTO
    ) {
        ConfiguracionUsuarioResponseDTO configuracionCreada =
                configuracionUsuarioService.crearConfiguracion(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(configuracionCreada);
    }

    @Operation(summary = "Actualizar configuración", description = "Actualiza una configuración existente.")
    @ApiResponse(responseCode = "200", description = "Configuración actualizada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o configuración duplicada")
    @ApiResponse(responseCode = "404", description = "Configuración, usuario o color no encontrado")
    @PutMapping("/{idConfiguracionUsuario}")
    public ResponseEntity<ConfiguracionUsuarioResponseDTO> actualizarConfiguracion(
            @Parameter(description = "ID de la configuración", example = "1")
            @PathVariable Integer idConfiguracionUsuario,

            @Valid @RequestBody ConfiguracionUsuarioRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(
                configuracionUsuarioService.actualizarConfiguracion(idConfiguracionUsuario, requestDTO)
        );
    }

    @Operation(summary = "Eliminar configuración", description = "Elimina una configuración existente.")
    @ApiResponse(responseCode = "204", description = "Configuración eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    @DeleteMapping("/{idConfiguracionUsuario}")
    public ResponseEntity<Void> eliminarConfiguracion(
            @Parameter(description = "ID de la configuración", example = "1")
            @PathVariable Integer idConfiguracionUsuario
    ) {
        configuracionUsuarioService.eliminarConfiguracion(idConfiguracionUsuario);
        return ResponseEntity.noContent().build();
    }
}