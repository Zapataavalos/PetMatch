package com.petmatch.ubicacion_service.Service;

import com.petmatch.ubicacion_service.DTO.UbicacionRequestDTO;
import com.petmatch.ubicacion_service.DTO.UbicacionResponseDTO;
import com.petmatch.ubicacion_service.Exception.BadRequestException;
import com.petmatch.ubicacion_service.Exception.ResourceNotFoundException;
import com.petmatch.ubicacion_service.Model.Ubicacion;
import com.petmatch.ubicacion_service.Repository.CiudadReferenciaRepository;
import com.petmatch.ubicacion_service.Repository.UbicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final CiudadReferenciaRepository ciudadReferenciaRepository;

    public UbicacionService(
            UbicacionRepository ubicacionRepository,
            CiudadReferenciaRepository ciudadReferenciaRepository
    ) {
        this.ubicacionRepository = ubicacionRepository;
        this.ciudadReferenciaRepository = ciudadReferenciaRepository;
    }

    public List<UbicacionResponseDTO> listarUbicaciones() {
        return ubicacionRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public List<UbicacionResponseDTO> listarUbicacionesPorCiudad(Integer idCiudad) {
        validarCiudadExisteActiva(idCiudad);

        return ubicacionRepository.findByIdCiudad(idCiudad)
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public UbicacionResponseDTO buscarUbicacionPorId(Integer idUbicacion) {
        Ubicacion ubicacion = obtenerUbicacionPorId(idUbicacion);
        return convertirAResponseDTO(ubicacion);
    }

    public UbicacionResponseDTO crearUbicacion(UbicacionRequestDTO requestDTO) {
        validarCiudadExisteActiva(requestDTO.idCiudad());

        String direccionNormalizada = normalizarTextoObligatorio(requestDTO.direccion());
        String numeroNormalizado = normalizarTextoObligatorio(requestDTO.numero());

        if (ubicacionRepository.existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudad(
                direccionNormalizada,
                numeroNormalizado,
                requestDTO.idCiudad()
        )) {
            throw new BadRequestException(
                    "Ya existe una ubicación registrada con la dirección "
                            + direccionNormalizada
                            + ", número "
                            + numeroNormalizado
                            + " para la ciudad con ID: "
                            + requestDTO.idCiudad()
            );
        }

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setDireccion(direccionNormalizada);
        ubicacion.setNumero(numeroNormalizado);
        ubicacion.setReferencia(normalizarTextoOpcional(requestDTO.referencia()));
        ubicacion.setCodigoPostal(normalizarCodigoPostal(requestDTO.codigoPostal()));
        ubicacion.setLatitud(requestDTO.latitud());
        ubicacion.setLongitud(requestDTO.longitud());
        ubicacion.setIdCiudad(requestDTO.idCiudad());

        Ubicacion ubicacionGuardada = ubicacionRepository.save(ubicacion);

        return convertirAResponseDTO(ubicacionGuardada);
    }

    public UbicacionResponseDTO actualizarUbicacion(Integer idUbicacion, UbicacionRequestDTO requestDTO) {
        Ubicacion ubicacion = obtenerUbicacionPorId(idUbicacion);

        validarCiudadExisteActiva(requestDTO.idCiudad());

        String direccionNormalizada = normalizarTextoObligatorio(requestDTO.direccion());
        String numeroNormalizado = normalizarTextoObligatorio(requestDTO.numero());

        if (ubicacionRepository.existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudadAndIdUbicacionNot(
                direccionNormalizada,
                numeroNormalizado,
                requestDTO.idCiudad(),
                idUbicacion
        )) {
            throw new BadRequestException(
                    "Ya existe otra ubicación registrada con la dirección "
                            + direccionNormalizada
                            + ", número "
                            + numeroNormalizado
                            + " para la ciudad con ID: "
                            + requestDTO.idCiudad()
            );
        }

        ubicacion.setDireccion(direccionNormalizada);
        ubicacion.setNumero(numeroNormalizado);
        ubicacion.setReferencia(normalizarTextoOpcional(requestDTO.referencia()));
        ubicacion.setCodigoPostal(normalizarCodigoPostal(requestDTO.codigoPostal()));
        ubicacion.setLatitud(requestDTO.latitud());
        ubicacion.setLongitud(requestDTO.longitud());
        ubicacion.setIdCiudad(requestDTO.idCiudad());

        Ubicacion ubicacionActualizada = ubicacionRepository.save(ubicacion);

        return convertirAResponseDTO(ubicacionActualizada);
    }

    public void eliminarUbicacion(Integer idUbicacion) {
        Ubicacion ubicacion = obtenerUbicacionPorId(idUbicacion);
        ubicacionRepository.delete(ubicacion);
    }

    private Ubicacion obtenerUbicacionPorId(Integer idUbicacion) {
        return ubicacionRepository.findById(idUbicacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la ubicación con ID: " + idUbicacion
                ));
    }

    private void validarCiudadExisteActiva(Integer idCiudad) {
        boolean existeCiudad = ciudadReferenciaRepository.existsByIdCiudadAndActivoTrue(idCiudad);

        if (!existeCiudad) {
            throw new ResourceNotFoundException(
                    "No existe una ciudad activa registrada con ID: " + idCiudad
            );
        }
    }

    private UbicacionResponseDTO convertirAResponseDTO(Ubicacion ubicacion) {
        return new UbicacionResponseDTO(
                ubicacion.getIdUbicacion(),
                ubicacion.getDireccion(),
                ubicacion.getNumero(),
                ubicacion.getReferencia(),
                ubicacion.getCodigoPostal(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                ubicacion.getIdCiudad()
        );
    }

    private String normalizarTextoObligatorio(String texto) {
        return texto.trim().toUpperCase();
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim().toUpperCase();
    }

    private String normalizarCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.trim().isEmpty()) {
            return null;
        }

        return codigoPostal.trim();
    }
}