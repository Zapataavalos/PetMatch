package com.petmatch.pais_service.Service;

import com.petmatch.pais_service.Dto.PaisRequestDTO;
import com.petmatch.pais_service.Dto.PaisResponseDTO;
import com.petmatch.pais_service.Exception.BadRequestException;
import com.petmatch.pais_service.Exception.ResourceNotFoundException;
import com.petmatch.pais_service.Model.Pais;
import com.petmatch.pais_service.Repository.PaisRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaisService {

    private final PaisRepository paisRepository;

    public PaisService(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    public List<PaisResponseDTO> listarPaises() {
        return paisRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public PaisResponseDTO buscarPaisPorId(Integer idPais) {
        Pais pais = obtenerPaisPorId(idPais);
        return convertirAResponseDTO(pais);
    }

    public PaisResponseDTO crearPais(PaisRequestDTO requestDTO) {
        String nombreNormalizado = normalizarNombre(requestDTO.nombrePais());

        if (paisRepository.existsByNombrePaisIgnoreCase(nombreNormalizado)) {
            throw new BadRequestException("Ya existe un país registrado con el nombre: " + nombreNormalizado);
        }

        Pais pais = new Pais();
        pais.setNombrePais(nombreNormalizado);

        Pais paisGuardado = paisRepository.save(pais);

        return convertirAResponseDTO(paisGuardado);
    }

    public PaisResponseDTO actualizarPais(Integer idPais, PaisRequestDTO requestDTO) {
        Pais pais = obtenerPaisPorId(idPais);

        String nombreNormalizado = normalizarNombre(requestDTO.nombrePais());

        if (paisRepository.existsByNombrePaisIgnoreCaseAndIdPaisNot(nombreNormalizado, idPais)) {
            throw new BadRequestException("Ya existe otro país registrado con el nombre: " + nombreNormalizado);
        }

        pais.setNombrePais(nombreNormalizado);

        Pais paisActualizado = paisRepository.save(pais);

        return convertirAResponseDTO(paisActualizado);
    }

    public void eliminarPais(Integer idPais) {
        Pais pais = obtenerPaisPorId(idPais);
        paisRepository.delete(pais);
    }

    private Pais obtenerPaisPorId(Integer idPais) {
        return paisRepository.findById(idPais)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el país con ID: " + idPais));
    }

    private PaisResponseDTO convertirAResponseDTO(Pais pais) {
        return new PaisResponseDTO(
                pais.getIdPais(),
                pais.getNombrePais()
        );
    }

    private String normalizarNombre(String nombrePais) {
        return nombrePais.trim().toUpperCase();
    }
}