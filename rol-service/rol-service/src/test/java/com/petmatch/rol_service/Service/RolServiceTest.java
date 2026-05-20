package com.petmatch.rol_service.Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.petmatch.rol_service.DTO.RolRequestDTO;
import com.petmatch.rol_service.DTO.RolResponseDTO;
import com.petmatch.rol_service.Exception.BadRequestException;
import com.petmatch.rol_service.Exception.ResourceNotFoundException;
import com.petmatch.rol_service.Model.Rol;
import com.petmatch.rol_service.Repository.RolRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    @Test
    @DisplayName("Debe listar todos los roles correctamente")
    void listarRoles_debeRetornarListaDeRoles() {
        Rol admin = new Rol(1, "ADMINISTRADOR");
        Rol usuario = new Rol(2, "USUARIO");

        when(rolRepository.findAll()).thenReturn(List.of(admin, usuario));

        List<RolResponseDTO> resultado = rolService.listarRoles();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nombreRol()).isEqualTo("ADMINISTRADOR");
        assertThat(resultado.get(1).nombreRol()).isEqualTo("USUARIO");

        verify(rolRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un rol por ID correctamente")
    void buscarRolPorId_cuandoExiste_debeRetornarRol() {
        Rol rol = new Rol(1, "ADMINISTRADOR");

        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));

        RolResponseDTO resultado = rolService.buscarRolPorId(1);

        assertThat(resultado.idRol()).isEqualTo(1);
        assertThat(resultado.nombreRol()).isEqualTo("ADMINISTRADOR");

        verify(rolRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el rol no existe")
    void buscarRolPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.buscarRolPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el rol con ID: 99");

        verify(rolRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe crear un rol correctamente")
    void crearRol_cuandoNombreNoExiste_debeCrearRol() {
        RolRequestDTO requestDTO = new RolRequestDTO("administrador");

        when(rolRepository.existsByNombreRolIgnoreCase("ADMINISTRADOR")).thenReturn(false);

        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> {
            Rol rol = invocation.getArgument(0);
            rol.setIdRol(1);
            return rol;
        });

        RolResponseDTO resultado = rolService.crearRol(requestDTO);

        assertThat(resultado.idRol()).isEqualTo(1);
        assertThat(resultado.nombreRol()).isEqualTo("ADMINISTRADOR");

        verify(rolRepository, times(1)).existsByNombreRolIgnoreCase("ADMINISTRADOR");
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    @DisplayName("No debe crear un rol duplicado")
    void crearRol_cuandoNombreExiste_debeLanzarBadRequest() {
        RolRequestDTO requestDTO = new RolRequestDTO("Administrador");

        when(rolRepository.existsByNombreRolIgnoreCase("ADMINISTRADOR")).thenReturn(true);

        assertThatThrownBy(() -> rolService.crearRol(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe un rol registrado");

        verify(rolRepository, times(1)).existsByNombreRolIgnoreCase("ADMINISTRADOR");
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("Debe actualizar un rol correctamente")
    void actualizarRol_cuandoExisteYNoEstaDuplicado_debeActualizarRol() {
        Rol rolExistente = new Rol(1, "ADMINISTRADOR");
        RolRequestDTO requestDTO = new RolRequestDTO("super admin");

        when(rolRepository.findById(1)).thenReturn(Optional.of(rolExistente));
        when(rolRepository.existsByNombreRolIgnoreCaseAndIdRolNot("SUPER ADMIN", 1)).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RolResponseDTO resultado = rolService.actualizarRol(1, requestDTO);

        assertThat(resultado.idRol()).isEqualTo(1);
        assertThat(resultado.nombreRol()).isEqualTo("SUPER ADMIN");

        verify(rolRepository, times(1)).findById(1);
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    @DisplayName("No debe actualizar un rol si el nuevo nombre ya existe")
    void actualizarRol_cuandoNombreDuplicado_debeLanzarBadRequest() {
        Rol rolExistente = new Rol(1, "ADMINISTRADOR");
        RolRequestDTO requestDTO = new RolRequestDTO("usuario");

        when(rolRepository.findById(1)).thenReturn(Optional.of(rolExistente));
        when(rolRepository.existsByNombreRolIgnoreCaseAndIdRolNot("USUARIO", 1)).thenReturn(true);

        assertThatThrownBy(() -> rolService.actualizarRol(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otro rol registrado");

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("Debe eliminar un rol correctamente")
    void eliminarRol_cuandoExiste_debeEliminarRol() {
        Rol rol = new Rol(1, "ADMINISTRADOR");

        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));

        rolService.eliminarRol(1);

        verify(rolRepository, times(1)).findById(1);
        verify(rolRepository, times(1)).delete(rol);
    }
}