package com.petmatch.color_service.Service;

import com.petmatch.color_service.DTO.ColorRequestDTO;
import com.petmatch.color_service.DTO.ColorResponseDTO;
import com.petmatch.color_service.Exception.BadRequestException;
import com.petmatch.color_service.Exception.ResourceNotFoundException;
import com.petmatch.color_service.Model.Color;
import com.petmatch.color_service.Repository.ColorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {

    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    public List<ColorResponseDTO> listarColores() {
        return colorRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public ColorResponseDTO buscarColorPorId(Integer idColor) {
        Color color = obtenerColorPorId(idColor);
        return convertirAResponseDTO(color);
    }

    public ColorResponseDTO crearColor(ColorRequestDTO requestDTO) {
        String nombreNormalizado = normalizarNombre(requestDTO.nombreColor());
        String hexNormalizado = normalizarHexadecimal(requestDTO.codigoHexadecimal());

        if (colorRepository.existsByNombreColorIgnoreCase(nombreNormalizado)) {
            throw new BadRequestException("Ya existe un color registrado con el nombre: " + nombreNormalizado);
        }

        if (colorRepository.existsByCodigoHexadecimalIgnoreCase(hexNormalizado)) {
            throw new BadRequestException("Ya existe un color registrado con el código hexadecimal: " + hexNormalizado);
        }

        Color color = new Color();
        color.setNombreColor(nombreNormalizado);
        color.setCodigoHexadecimal(hexNormalizado);

        Color colorGuardado = colorRepository.save(color);

        return convertirAResponseDTO(colorGuardado);
    }

    public ColorResponseDTO actualizarColor(Integer idColor, ColorRequestDTO requestDTO) {
        Color color = obtenerColorPorId(idColor);

        String nombreNormalizado = normalizarNombre(requestDTO.nombreColor());
        String hexNormalizado = normalizarHexadecimal(requestDTO.codigoHexadecimal());

        if (colorRepository.existsByNombreColorIgnoreCaseAndIdColorNot(nombreNormalizado, idColor)) {
            throw new BadRequestException("Ya existe otro color registrado con el nombre: " + nombreNormalizado);
        }

        if (colorRepository.existsByCodigoHexadecimalIgnoreCaseAndIdColorNot(hexNormalizado, idColor)) {
            throw new BadRequestException("Ya existe otro color registrado con el código hexadecimal: " + hexNormalizado);
        }

        color.setNombreColor(nombreNormalizado);
        color.setCodigoHexadecimal(hexNormalizado);

        Color colorActualizado = colorRepository.save(color);

        return convertirAResponseDTO(colorActualizado);
    }

    public void eliminarColor(Integer idColor) {
        Color color = obtenerColorPorId(idColor);
        colorRepository.delete(color);
    }

    private Color obtenerColorPorId(Integer idColor) {
        return colorRepository.findById(idColor)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el color con ID: " + idColor));
    }

    private ColorResponseDTO convertirAResponseDTO(Color color) {
        return new ColorResponseDTO(
                color.getIdColor(),
                color.getNombreColor(),
                color.getCodigoHexadecimal()
        );
    }

    private String normalizarNombre(String nombreColor) {
        return nombreColor.trim().toUpperCase();
    }

    private String normalizarHexadecimal(String codigoHexadecimal) {
        return codigoHexadecimal.trim().toUpperCase();
    }
}