package com.petmatch.usuario_service.Controller;

import com.petmatch.usuario_service.DTO.UsuarioRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(idUsuario));
    }

    @GetMapping("/rol/{idRol}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuariosPorRol(@PathVariable Integer idRol) {
        return ResponseEntity.ok(usuarioService.listarUsuariosPorRol(idRol));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(
            @Valid @RequestBody UsuarioRequestDTO requestDTO
    ) {
        UsuarioResponseDTO usuarioCreado = usuarioService.crearUsuario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Integer idUsuario,
            @Valid @RequestBody UsuarioRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(idUsuario, requestDTO));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}