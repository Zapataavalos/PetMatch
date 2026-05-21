package com.petmatch.usuario_service.Controller;

import com.petmatch.usuario_service.DTO.AuthResponseDTO;
import com.petmatch.usuario_service.DTO.LoginRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @Valid @RequestBody UsuarioRequestDTO requestDTO
    ) {
        UsuarioResponseDTO usuarioRegistrado = authService.registrar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(authService.login(requestDTO));
    }
}