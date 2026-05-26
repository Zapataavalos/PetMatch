package com.petmatch.usuario_service.Controller;

import com.petmatch.usuario_service.DTO.PerfilRequestDTO;
import com.petmatch.usuario_service.DTO.PerfilResponseDTO;
import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Service.AuthService;
import com.petmatch.usuario_service.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    public PerfilController(UsuarioService usuarioService, AuthService authService) {
        this.usuarioService = usuarioService;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(authentication.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<PerfilResponseDTO> actualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody PerfilRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(authService.actualizarPerfil(authentication.getName(), requestDTO));
    }
}
