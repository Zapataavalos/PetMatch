package com.petmatch.ciudad_service.Service;

import com.petmatch.ciudad_service.DTO.CiudadRequestDTO;
import com.petmatch.ciudad_service.DTO.CiudadResponseDTO;
import com.petmatch.ciudad_service.Exception.BadRequestException;
import com.petmatch.ciudad_service.Exception.ResourceNotFoundException;
import com.petmatch.ciudad_service.Model.Ciudad;
import com.petmatch.ciudad_service.Repository.CiudadRepository;
import com.petmatch.ciudad_service.Repository.RegionReferenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiudadService {

    private final CiudadRepository ciudadRepository;
    private final RegionReferenciaRepository regionReferenciaRepository;

    public CiudadService(
            CiudadRepository ciudadRepository,
            RegionReferenciaRepository regionReferenciaRepository
    ) {
        this.ciudadRepository = ciudadRepository;
        this.regionReferenciaRepository = regionReferenciaRepository;
    }

    public List<CiudadResponseDTO> listarCiudades() {
        return ciudadRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public List<CiudadResponseDTO> listarCiudadesPorRegion(Integer idRegion) {
        validarRegionExisteActiva(idRegion);

        return ciudadRepository.findByIdRegion(idRegion)
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public CiudadResponseDTO buscarCiudadPorId(Integer idCiudad) {
        Ciudad ciudad = obtenerCiudadPorId(idCiudad);
        return convertirAResponseDTO(ciudad);
    }

    public CiudadResponseDTO crearCiudad(CiudadRequestDTO requestDTO) {
        String nombreNormalizado = normalizarNombre(requestDTO.nombreCiudad());

        validarRegionExisteActiva(requestDTO.idRegion());

        if (ciudadRepository.existsByNombreCiudadIgnoreCaseAndIdRegion(
                nombreNormalizado,
                requestDTO.idRegion()
        )) {
            throw new BadRequestException(
                    "Ya existe una ciudad registrada con el nombre "
                            + nombreNormalizado
                            + " para la región con ID: "
                            + requestDTO.idRegion()
            );
        }

        Ciudad ciudad = new Ciudad();
        ciudad.setNombreCiudad(nombreNormalizado);
        ciudad.setIdRegion(requestDTO.idRegion());

        Ciudad ciudadGuardada = ciudadRepository.save(ciudad);

        return convertirAResponseDTO(ciudadGuardada);
    }

    public CiudadResponseDTO actualizarCiudad(Integer idCiudad, CiudadRequestDTO requestDTO) {
        Ciudad ciudad = obtenerCiudadPorId(idCiudad);

        String nombreNormalizado = normalizarNombre(requestDTO.nombreCiudad());

        validarRegionExisteActiva(requestDTO.idRegion());

        if (ciudadRepository.existsByNombreCiudadIgnoreCaseAndIdRegionAndIdCiudadNot(
                nombreNormalizado,
                requestDTO.idRegion(),
                idCiudad
        )) {
            throw new BadRequestException(
                    "Ya existe otra ciudad registrada con el nombre "
                            + nombreNormalizado
                            + " para la región con ID: "
                            + requestDTO.idRegion()
            );
        }

        ciudad.setNombreCiudad(nombreNormalizado);
        ciudad.setIdRegion(requestDTO.idRegion());

        Ciudad ciudadActualizada = ciudadRepository.save(ciudad);

        return convertirAResponseDTO(ciudadActualizada);
    }

    public void eliminarCiudad(Integer idCiudad) {
        Ciudad ciudad = obtenerCiudadPorId(idCiudad);
        ciudadRepository.delete(ciudad);
    }

    private Ciudad obtenerCiudadPorId(Integer idCiudad) {
        return ciudadRepository.findById(idCiudad)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la ciudad con ID: " + idCiudad
                ));
    }

    private void validarRegionExisteActiva(Integer idRegion) {
        boolean existeRegion = regionReferenciaRepository.existsByIdRegionAndActivoTrue(idRegion);

        if (!existeRegion) {
            throw new ResourceNotFoundException(
                    "No existe una región activa registrada con ID: " + idRegion
            );
        }
    }

    private CiudadResponseDTO convertirAResponseDTO(Ciudad ciudad) {
        return new CiudadResponseDTO(
                ciudad.getIdCiudad(),
                ciudad.getNombreCiudad(),
                ciudad.getIdRegion()
        );
    }

    private String normalizarNombre(String nombreCiudad) {
        return nombreCiudad.trim().toUpperCase();
    }
}