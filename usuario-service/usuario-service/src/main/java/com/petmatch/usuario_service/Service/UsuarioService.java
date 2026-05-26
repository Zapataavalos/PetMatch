package com.petmatch.usuario_service.Service;

import com.petmatch.usuario_service.DTO.PerfilRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Event.UsuarioEventPublisher;
import com.petmatch.usuario_service.Exception.BadRequestException;
import com.petmatch.usuario_service.Exception.ResourceNotFoundException;
import com.petmatch.usuario_service.Model.Usuario;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import com.petmatch.usuario_service.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolReferenciaRepository rolReferenciaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioEventPublisher usuarioEventPublisher;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolReferenciaRepository rolReferenciaRepository,
            PasswordEncoder passwordEncoder,
            UsuarioEventPublisher usuarioEventPublisher
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolReferenciaRepository = rolReferenciaRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioEventPublisher = usuarioEventPublisher;
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public List<UsuarioResponseDTO> listarUsuariosPorRol(Integer idRol) {
        validarRolExisteActivo(idRol);

        return usuarioRepository.findByIdRol(idRol)
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public UsuarioResponseDTO buscarUsuarioPorId(Integer idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);
        return convertirAResponseDTO(usuario);
    }

    public UsuarioResponseDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = obtenerUsuarioPorEmail(email);
        return convertirAResponseDTO(usuario);
    }

    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO) {
        String nombreNormalizado = normalizarNombre(requestDTO.nombre());
        String emailNormalizado = normalizarEmail(requestDTO.email());

        validarRolExisteActivo(requestDTO.idRol());

        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new BadRequestException("Ya existe un usuario registrado con el email: " + emailNormalizado);
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombreNormalizado);
        usuario.setEmail(emailNormalizado);
        usuario.setContrasena(passwordEncoder.encode(requestDTO.contrasena()));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setIdRol(requestDTO.idRol());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        usuarioEventPublisher.publicarUsuarioCreado(usuarioGuardado);

        return convertirAResponseDTO(usuarioGuardado);
    }

    public UsuarioResponseDTO actualizarUsuario(Integer idUsuario, UsuarioRequestDTO requestDTO) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);

        String nombreNormalizado = normalizarNombre(requestDTO.nombre());
        String emailNormalizado = normalizarEmail(requestDTO.email());

        validarRolExisteActivo(requestDTO.idRol());

        if (usuarioRepository.existsByEmailIgnoreCaseAndIdUsuarioNot(emailNormalizado, idUsuario)) {
            throw new BadRequestException("Ya existe otro usuario registrado con el email: " + emailNormalizado);
        }

        usuario.setNombre(nombreNormalizado);
        usuario.setEmail(emailNormalizado);
        usuario.setContrasena(passwordEncoder.encode(requestDTO.contrasena()));
        usuario.setIdRol(requestDTO.idRol());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        usuarioEventPublisher.publicarUsuarioActualizado(usuarioActualizado);

        return convertirAResponseDTO(usuarioActualizado);
    }

    public UsuarioResponseDTO actualizarPerfilPorEmail(String emailActual, PerfilRequestDTO requestDTO) {
        Usuario usuario = obtenerUsuarioPorEmail(emailActual);

        String nombreNormalizado = normalizarNombre(requestDTO.nombre());
        String emailNormalizado = normalizarEmail(requestDTO.email());

        if (usuarioRepository.existsByEmailIgnoreCaseAndIdUsuarioNot(emailNormalizado, usuario.getIdUsuario())) {
            throw new BadRequestException("Ya existe otro usuario registrado con el email: " + emailNormalizado);
        }

        usuario.setNombre(nombreNormalizado);
        usuario.setEmail(emailNormalizado);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        usuarioEventPublisher.publicarUsuarioActualizado(usuarioActualizado);

        return convertirAResponseDTO(usuarioActualizado);
    }

    public void eliminarUsuario(Integer idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);

        usuarioRepository.delete(usuario);

        usuarioEventPublisher.publicarUsuarioEliminado(usuario);
    }

    private Usuario obtenerUsuarioPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con ID: " + idUsuario));
    }

    private Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario autenticado"));
    }

    private void validarRolExisteActivo(Integer idRol) {
        boolean existeRol = rolReferenciaRepository.existsByIdRolAndActivoTrue(idRol);

        if (!existeRol) {
            throw new ResourceNotFoundException("No existe un rol activo registrado con ID: " + idRol);
        }
    }

    private UsuarioResponseDTO convertirAResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getFechaRegistro(),
                usuario.getIdRol()
        );
    }

    private String normalizarNombre(String nombre) {
        return nombre.trim().toUpperCase();
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
