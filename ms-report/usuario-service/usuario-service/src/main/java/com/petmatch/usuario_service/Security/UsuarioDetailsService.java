package com.petmatch.usuario_service.Security;

import com.petmatch.usuario_service.Model.RolReferencia;
import com.petmatch.usuario_service.Model.Usuario;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import com.petmatch.usuario_service.Repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolReferenciaRepository rolReferenciaRepository;

    public UsuarioDetailsService(
            UsuarioRepository usuarioRepository,
            RolReferenciaRepository rolReferenciaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolReferenciaRepository = rolReferenciaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        RolReferencia rol = rolReferenciaRepository.findByIdRolAndActivoTrue(usuario.getIdRol())
                .orElseThrow(() -> new UsernameNotFoundException("Rol no disponible"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getContrasena())
                .roles(rol.getNombreRol())
                .build();
    }
}