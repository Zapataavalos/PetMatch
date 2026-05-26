package com.petmatch.configuracion_usuario_service.Service;

import com.petmatch.configuracion_usuario_service.DTO.ConfiguracionUsuarioRequestDTO;
import com.petmatch.configuracion_usuario_service.DTO.ConfiguracionUsuarioResponseDTO;
import com.petmatch.configuracion_usuario_service.Exception.BadRequestException;
import com.petmatch.configuracion_usuario_service.Exception.ResourceNotFoundException;
import com.petmatch.configuracion_usuario_service.Model.ConfiguracionUsuario;
import com.petmatch.configuracion_usuario_service.Repository.ColorReferenciaRepository;
import com.petmatch.configuracion_usuario_service.Repository.ConfiguracionUsuarioRepository;
import com.petmatch.configuracion_usuario_service.Repository.UsuarioReferenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfiguracionUsuarioService {

    private final ConfiguracionUsuarioRepository configuracionUsuarioRepository;
    private final UsuarioReferenciaRepository usuarioReferenciaRepository;
    private final ColorReferenciaRepository colorReferenciaRepository;

    public ConfiguracionUsuarioService(
            ConfiguracionUsuarioRepository configuracionUsuarioRepository,
            UsuarioReferenciaRepository usuarioReferenciaRepository,
            ColorReferenciaRepository colorReferenciaRepository
    ) {
        this.configuracionUsuarioRepository = configuracionUsuarioRepository;
        this.usuarioReferenciaRepository = usuarioReferenciaRepository;
        this.colorReferenciaRepository = colorReferenciaRepository;
    }

    public List<ConfiguracionUsuarioResponseDTO> listarConfiguraciones() {
        return configuracionUsuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public ConfiguracionUsuarioResponseDTO buscarConfiguracionPorId(Integer idConfiguracionUsuario) {
        ConfiguracionUsuario configuracion = obtenerConfiguracionPorId(idConfiguracionUsuario);
        return convertirAResponseDTO(configuracion);
    }

    public ConfiguracionUsuarioResponseDTO buscarConfiguracionPorUsuario(Integer idUsuario) {
        ConfiguracionUsuario configuracion = configuracionUsuarioRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró configuración para el usuario con ID: " + idUsuario
                ));

        return convertirAResponseDTO(configuracion);
    }

    public ConfiguracionUsuarioResponseDTO crearConfiguracion(ConfiguracionUsuarioRequestDTO requestDTO) {
        validarUsuarioActivo(requestDTO.idUsuario());
        validarColorActivo(requestDTO.idColor());

        if (configuracionUsuarioRepository.existsByIdUsuario(requestDTO.idUsuario())) {
            throw new BadRequestException(
                    "Ya existe una configuración registrada para el usuario con ID: " + requestDTO.idUsuario()
            );
        }

        ConfiguracionUsuario configuracion = new ConfiguracionUsuario();
        configuracion.setIdUsuario(requestDTO.idUsuario());
        configuracion.setIdColor(requestDTO.idColor());
        configuracion.setNotificacionesActivas(requestDTO.notificacionesActivas());
        configuracion.setModoOscuro(requestDTO.modoOscuro());
        configuracion.setIdioma(normalizarIdioma(requestDTO.idioma()));

        ConfiguracionUsuario configuracionGuardada = configuracionUsuarioRepository.save(configuracion);

        return convertirAResponseDTO(configuracionGuardada);
    }

    public ConfiguracionUsuarioResponseDTO actualizarConfiguracion(
            Integer idConfiguracionUsuario,
            ConfiguracionUsuarioRequestDTO requestDTO
    ) {
        ConfiguracionUsuario configuracion = obtenerConfiguracionPorId(idConfiguracionUsuario);

        validarUsuarioActivo(requestDTO.idUsuario());
        validarColorActivo(requestDTO.idColor());

        if (configuracionUsuarioRepository.existsByIdUsuarioAndIdConfiguracionUsuarioNot(
                requestDTO.idUsuario(),
                idConfiguracionUsuario
        )) {
            throw new BadRequestException(
                    "Ya existe otra configuración registrada para el usuario con ID: " + requestDTO.idUsuario()
            );
        }

        configuracion.setIdUsuario(requestDTO.idUsuario());
        configuracion.setIdColor(requestDTO.idColor());
        configuracion.setNotificacionesActivas(requestDTO.notificacionesActivas());
        configuracion.setModoOscuro(requestDTO.modoOscuro());
        configuracion.setIdioma(normalizarIdioma(requestDTO.idioma()));

        ConfiguracionUsuario configuracionActualizada = configuracionUsuarioRepository.save(configuracion);

        return convertirAResponseDTO(configuracionActualizada);
    }

    public void eliminarConfiguracion(Integer idConfiguracionUsuario) {
        ConfiguracionUsuario configuracion = obtenerConfiguracionPorId(idConfiguracionUsuario);
        configuracionUsuarioRepository.delete(configuracion);
    }

    private ConfiguracionUsuario obtenerConfiguracionPorId(Integer idConfiguracionUsuario) {
        return configuracionUsuarioRepository.findById(idConfiguracionUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la configuración con ID: " + idConfiguracionUsuario
                ));
    }

    private void validarUsuarioActivo(Integer idUsuario) {
        boolean existeUsuario = usuarioReferenciaRepository.existsByIdUsuarioAndActivoTrue(idUsuario);

        if (!existeUsuario) {
            throw new ResourceNotFoundException(
                    "No existe un usuario activo registrado con ID: " + idUsuario
            );
        }
    }

    private void validarColorActivo(Integer idColor) {
        boolean existeColor = colorReferenciaRepository.existsByIdColorAndActivoTrue(idColor);

        if (!existeColor) {
            throw new ResourceNotFoundException(
                    "No existe un color activo registrado con ID: " + idColor
            );
        }
    }

    private ConfiguracionUsuarioResponseDTO convertirAResponseDTO(ConfiguracionUsuario configuracion) {
        return new ConfiguracionUsuarioResponseDTO(
                configuracion.getIdConfiguracionUsuario(),
                configuracion.getIdUsuario(),
                configuracion.getIdColor(),
                configuracion.getNotificacionesActivas(),
                configuracion.getModoOscuro(),
                configuracion.getIdioma()
        );
    }

    private String normalizarIdioma(String idioma) {
        return idioma.trim().toUpperCase();
    }
}