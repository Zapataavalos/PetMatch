package com.petmatch.region_service.Service;

import com.petmatch.region_service.DTO.RegionRequestDto;
import com.petmatch.region_service.DTO.RegionResponseDto;
import com.petmatch.region_service.Exception.BadRequestException;
import com.petmatch.region_service.Exception.ResourceNotFoundException;
import com.petmatch.region_service.Model.Region;
import com.petmatch.region_service.Repository.PaisReferenciaRepository;
import com.petmatch.region_service.Repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final PaisReferenciaRepository paisReferenciaRepository;

    public RegionService(
            RegionRepository regionRepository,
            PaisReferenciaRepository paisReferenciaRepository
    ) {
        this.regionRepository = regionRepository;
        this.paisReferenciaRepository = paisReferenciaRepository;
    }

    public List<RegionResponseDto> listarRegiones() {
        return regionRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public List<RegionResponseDto> listarRegionesPorPais(Integer idPais) {
        validarPaisExisteActivo(idPais);

        return regionRepository.findByIdPais(idPais)
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public RegionResponseDto buscarRegionPorId(Integer idRegion) {
        Region region = obtenerRegionPorId(idRegion);
        return convertirAResponseDTO(region);
    }

    public RegionResponseDto crearRegion(RegionRequestDto requestDTO) {
        String nombreNormalizado = normalizarNombre(requestDTO.nombreRegion());

        validarPaisExisteActivo(requestDTO.idPais());

        if (regionRepository.existsByNombreRegionIgnoreCaseAndIdPais(
                nombreNormalizado,
                requestDTO.idPais()
        )) {
            throw new BadRequestException(
                    "Ya existe una región registrada con el nombre "
                            + nombreNormalizado
                            + " para el país con ID: "
                            + requestDTO.idPais()
            );
        }

        Region region = new Region();
        region.setNombreRegion(nombreNormalizado);
        region.setIdPais(requestDTO.idPais());

        Region regionGuardada = regionRepository.save(region);

        return convertirAResponseDTO(regionGuardada);
    }

    public RegionResponseDto actualizarRegion(Integer idRegion, RegionRequestDto requestDTO) {
        Region region = obtenerRegionPorId(idRegion);

        String nombreNormalizado = normalizarNombre(requestDTO.nombreRegion());

        validarPaisExisteActivo(requestDTO.idPais());

        if (regionRepository.existsByNombreRegionIgnoreCaseAndIdPaisAndIdRegionNot(
                nombreNormalizado,
                requestDTO.idPais(),
                idRegion
        )) {
            throw new BadRequestException(
                    "Ya existe otra región registrada con el nombre "
                            + nombreNormalizado
                            + " para el país con ID: "
                            + requestDTO.idPais()
            );
        }

        region.setNombreRegion(nombreNormalizado);
        region.setIdPais(requestDTO.idPais());

        Region regionActualizada = regionRepository.save(region);

        return convertirAResponseDTO(regionActualizada);
    }

    public void eliminarRegion(Integer idRegion) {
        Region region = obtenerRegionPorId(idRegion);
        regionRepository.delete(region);
    }

    private Region obtenerRegionPorId(Integer idRegion) {
        return regionRepository.findById(idRegion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la región con ID: " + idRegion
                ));
    }

    private void validarPaisExisteActivo(Integer idPais) {
        boolean existePais = paisReferenciaRepository.existsByIdPaisAndActivoTrue(idPais);

        if (!existePais) {
            throw new ResourceNotFoundException(
                    "No existe un país activo registrado con ID: " + idPais
            );
        }
    }

    private RegionResponseDto convertirAResponseDTO(Region region) {
        return new RegionResponseDto(
                region.getIdRegion(),
                region.getNombreRegion(),
                region.getIdPais()
        );
    }

    private String normalizarNombre(String nombreRegion) {
        return nombreRegion.trim().toUpperCase();
    }
}