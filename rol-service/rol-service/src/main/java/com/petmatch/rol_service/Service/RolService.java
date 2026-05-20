package com.petmatch.rol_service.Service;

import org.springframework.stereotype.Service;

import com.petmatch.rol_service.DTO.RolRequestDTO;
import com.petmatch.rol_service.DTO.RolResponseDTO;
import com.petmatch.rol_service.Exception.BadRequestException;
import com.petmatch.rol_service.Exception.ResourceNotFoundException;
import com.petmatch.rol_service.Model.Rol;
import com.petmatch.rol_service.Repository.RolRepository;

import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<RolResponseDTO> listarRoles() {
        return rolRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public RolResponseDTO buscarRolPorId(Integer idRol) {
        Rol rol = obtenerRolPorId(idRol);
        return convertirAResponseDTO(rol);
    }

    public RolResponseDTO crearRol(RolRequestDTO requestDTO) {
        String nombreNormalizado = normalizarNombre(requestDTO.nombreRol());

        if (rolRepository.existsByNombreRolIgnoreCase(nombreNormalizado)) {
            throw new BadRequestException("Ya existe un rol registrado con el nombre: " + nombreNormalizado);
        }

        Rol rol = new Rol();
        rol.setNombreRol(nombreNormalizado);

        Rol rolGuardado = rolRepository.save(rol);

        return convertirAResponseDTO(rolGuardado);
    }

    public RolResponseDTO actualizarRol(Integer idRol, RolRequestDTO requestDTO) {
        Rol rol = obtenerRolPorId(idRol);

        String nombreNormalizado = normalizarNombre(requestDTO.nombreRol());

        if (rolRepository.existsByNombreRolIgnoreCaseAndIdRolNot(nombreNormalizado, idRol)) {
            throw new BadRequestException("Ya existe otro rol registrado con el nombre: " + nombreNormalizado);
        }

        rol.setNombreRol(nombreNormalizado);

        Rol rolActualizado = rolRepository.save(rol);

        return convertirAResponseDTO(rolActualizado);
    }

    public void eliminarRol(Integer idRol) {
        Rol rol = obtenerRolPorId(idRol);
        rolRepository.delete(rol);
    }

    private Rol obtenerRolPorId(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el rol con ID: " + idRol));
    }

    private RolResponseDTO convertirAResponseDTO(Rol rol) {
        return new RolResponseDTO(
                rol.getIdRol(),
                rol.getNombreRol()
        );
    }

    private String normalizarNombre(String nombreRol) {
        return nombreRol.trim().toUpperCase();
    }
}