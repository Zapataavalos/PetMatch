package com.petmatch.rol_service.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.petmatch.rol_service.DTO.RolRequestDTO;
import com.petmatch.rol_service.DTO.RolResponseDTO;
import com.petmatch.rol_service.Service.RolService;

import java.util.List;

@Tag(
        name = "Roles",
        description = "Operaciones para administrar los roles de usuario del sistema"
)
@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @Operation(
            summary = "Listar roles",
            description = "Obtiene todos los roles registrados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Listado de roles obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> listarRoles() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

    @Operation(
            summary = "Buscar rol por ID",
            description = "Obtiene la información de un rol específico según su identificador."
    )
    @ApiResponse(responseCode = "200", description = "Rol encontrado correctamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @GetMapping("/{idRol}")
    public ResponseEntity<RolResponseDTO> buscarRolPorId(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Integer idRol
    ) {
        return ResponseEntity.ok(rolService.buscarRolPorId(idRol));
    }

    @Operation(
            summary = "Crear rol",
            description = "Registra un nuevo rol en el sistema."
    )
    @ApiResponse(responseCode = "201", description = "Rol creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o rol duplicado")
    @PostMapping
    public ResponseEntity<RolResponseDTO> crearRol(
            @Valid @RequestBody RolRequestDTO requestDTO
    ) {
        RolResponseDTO rolCreado = rolService.crearRol(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(rolCreado);
    }

    @Operation(
            summary = "Actualizar rol",
            description = "Actualiza la información de un rol existente."
    )
    @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o rol duplicado")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @PutMapping("/{idRol}")
    public ResponseEntity<RolResponseDTO> actualizarRol(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Integer idRol,

            @Valid @RequestBody RolRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(rolService.actualizarRol(idRol, requestDTO));
    }

    @Operation(
            summary = "Eliminar rol",
            description = "Elimina un rol existente según su identificador."
    )
    @ApiResponse(responseCode = "204", description = "Rol eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @DeleteMapping("/{idRol}")
    public ResponseEntity<Void> eliminarRol(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Integer idRol
    ) {
        rolService.eliminarRol(idRol);
        return ResponseEntity.noContent().build();
    }
}