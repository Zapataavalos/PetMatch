package com.petmatch.usuario_service.Service;

import com.petmatch.usuario_service.DTO.AuthResponseDTO;
import com.petmatch.usuario_service.DTO.LoginRequestDTO;
import com.petmatch.usuario_service.DTO.PerfilRequestDTO;
import com.petmatch.usuario_service.DTO.PerfilResponseDTO;
import com.petmatch.usuario_service.DTO.UsuarioRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Exception.ResourceNotFoundException;
import com.petmatch.usuario_service.Exception.UnauthorizedException;
import com.petmatch.usuario_service.Model.RolReferencia;
import com.petmatch.usuario_service.Model.Usuario;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import com.petmatch.usuario_service.Repository.UsuarioRepository;
import com.petmatch.usuario_service.Security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final RolReferenciaRepository rolReferenciaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            RolReferenciaRepository rolReferenciaRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.rolReferenciaRepository = rolReferenciaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UsuarioResponseDTO registrar(UsuarioRequestDTO requestDTO) {
        return usuarioService.crearUsuario(requestDTO);
    }

    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(requestDTO.email())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!passwordEncoder.matches(requestDTO.contrasena(), usuario.getContrasena())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        RolReferencia rol = rolReferenciaRepository.findByIdRolAndActivoTrue(usuario.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("El rol del usuario no se encuentra activo"));

        String token = jwtService.generarToken(usuario, rol.getNombreRol());

        return new AuthResponseDTO(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getIdRol()
        );
    }

    public PerfilResponseDTO actualizarPerfil(String emailActual, PerfilRequestDTO requestDTO) {
        UsuarioResponseDTO perfilActualizado = usuarioService.actualizarPerfilPorEmail(emailActual, requestDTO);

        Usuario usuario = usuarioRepository.findById(perfilActualizado.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario actualizado"));

        RolReferencia rol = rolReferenciaRepository.findByIdRolAndActivoTrue(usuario.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("El rol del usuario no se encuentra activo"));

        String token = jwtService.generarToken(usuario, rol.getNombreRol());

        return new PerfilResponseDTO(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getIdRol(),
                usuario.getFechaRegistro()
        );
    }
}
