package com.petmatch.color_service.Service;

import com.petmatch.color_service.DTO.ColorRequestDTO;
import com.petmatch.color_service.DTO.ColorResponseDTO;
import com.petmatch.color_service.Exception.BadRequestException;
import com.petmatch.color_service.Exception.ResourceNotFoundException;
import com.petmatch.color_service.Model.Color;
import com.petmatch.color_service.Repository.ColorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColorServiceTest {

    @Mock
    private ColorRepository colorRepository;

    @InjectMocks
    private ColorService colorService;

    @Test
    @DisplayName("Debe listar todos los colores correctamente")
    void listarColores_debeRetornarListaDeColores() {
        Color rojo = new Color(1, "ROJO", "#FF0000");
        Color azul = new Color(2, "AZUL", "#0000FF");

        when(colorRepository.findAll()).thenReturn(List.of(rojo, azul));

        List<ColorResponseDTO> resultado = colorService.listarColores();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nombreColor()).isEqualTo("ROJO");
        assertThat(resultado.get(0).codigoHexadecimal()).isEqualTo("#FF0000");
        assertThat(resultado.get(1).nombreColor()).isEqualTo("AZUL");
        assertThat(resultado.get(1).codigoHexadecimal()).isEqualTo("#0000FF");

        verify(colorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un color por ID correctamente")
    void buscarColorPorId_cuandoExiste_debeRetornarColor() {
        Color color = new Color(1, "ROJO", "#FF0000");

        when(colorRepository.findById(1)).thenReturn(Optional.of(color));

        ColorResponseDTO resultado = colorService.buscarColorPorId(1);

        assertThat(resultado.idColor()).isEqualTo(1);
        assertThat(resultado.nombreColor()).isEqualTo("ROJO");
        assertThat(resultado.codigoHexadecimal()).isEqualTo("#FF0000");

        verify(colorRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el color no existe")
    void buscarColorPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(colorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colorService.buscarColorPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el color con ID: 99");

        verify(colorRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe crear un color correctamente")
    void crearColor_cuandoNoExisteNombreNiHex_debeCrearColor() {
        ColorRequestDTO requestDTO = new ColorRequestDTO("Rojo", "#ff0000");

        when(colorRepository.existsByNombreColorIgnoreCase("ROJO")).thenReturn(false);
        when(colorRepository.existsByCodigoHexadecimalIgnoreCase("#FF0000")).thenReturn(false);

        when(colorRepository.save(any(Color.class))).thenAnswer(invocation -> {
            Color color = invocation.getArgument(0);
            color.setIdColor(1);
            return color;
        });

        ColorResponseDTO resultado = colorService.crearColor(requestDTO);

        assertThat(resultado.idColor()).isEqualTo(1);
        assertThat(resultado.nombreColor()).isEqualTo("ROJO");
        assertThat(resultado.codigoHexadecimal()).isEqualTo("#FF0000");

        verify(colorRepository, times(1)).existsByNombreColorIgnoreCase("ROJO");
        verify(colorRepository, times(1)).existsByCodigoHexadecimalIgnoreCase("#FF0000");
        verify(colorRepository, times(1)).save(any(Color.class));
    }

    @Test
    @DisplayName("No debe crear un color si el nombre ya existe")
    void crearColor_cuandoNombreExiste_debeLanzarBadRequest() {
        ColorRequestDTO requestDTO = new ColorRequestDTO("Rojo", "#FF0000");

        when(colorRepository.existsByNombreColorIgnoreCase("ROJO")).thenReturn(true);

        assertThatThrownBy(() -> colorService.crearColor(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe un color registrado con el nombre: ROJO");

        verify(colorRepository, times(1)).existsByNombreColorIgnoreCase("ROJO");
        verify(colorRepository, never()).save(any(Color.class));
    }

    @Test
    @DisplayName("No debe crear un color si el código hexadecimal ya existe")
    void crearColor_cuandoHexExiste_debeLanzarBadRequest() {
        ColorRequestDTO requestDTO = new ColorRequestDTO("Rojo", "#FF0000");

        when(colorRepository.existsByNombreColorIgnoreCase("ROJO")).thenReturn(false);
        when(colorRepository.existsByCodigoHexadecimalIgnoreCase("#FF0000")).thenReturn(true);

        assertThatThrownBy(() -> colorService.crearColor(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe un color registrado con el código hexadecimal: #FF0000");

        verify(colorRepository, times(1)).existsByNombreColorIgnoreCase("ROJO");
        verify(colorRepository, times(1)).existsByCodigoHexadecimalIgnoreCase("#FF0000");
        verify(colorRepository, never()).save(any(Color.class));
    }

    @Test
    @DisplayName("Debe actualizar un color correctamente")
    void actualizarColor_cuandoExisteYNoEstaDuplicado_debeActualizarColor() {
        Color colorExistente = new Color(1, "ROJO", "#FF0000");
        ColorRequestDTO requestDTO = new ColorRequestDTO("Azul", "#0000ff");

        when(colorRepository.findById(1)).thenReturn(Optional.of(colorExistente));
        when(colorRepository.existsByNombreColorIgnoreCaseAndIdColorNot("AZUL", 1)).thenReturn(false);
        when(colorRepository.existsByCodigoHexadecimalIgnoreCaseAndIdColorNot("#0000FF", 1)).thenReturn(false);
        when(colorRepository.save(any(Color.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ColorResponseDTO resultado = colorService.actualizarColor(1, requestDTO);

        assertThat(resultado.idColor()).isEqualTo(1);
        assertThat(resultado.nombreColor()).isEqualTo("AZUL");
        assertThat(resultado.codigoHexadecimal()).isEqualTo("#0000FF");

        verify(colorRepository, times(1)).findById(1);
        verify(colorRepository, times(1)).save(any(Color.class));
    }

    @Test
    @DisplayName("No debe actualizar un color si el nuevo nombre ya existe")
    void actualizarColor_cuandoNombreDuplicado_debeLanzarBadRequest() {
        Color colorExistente = new Color(1, "ROJO", "#FF0000");
        ColorRequestDTO requestDTO = new ColorRequestDTO("Azul", "#0000FF");

        when(colorRepository.findById(1)).thenReturn(Optional.of(colorExistente));
        when(colorRepository.existsByNombreColorIgnoreCaseAndIdColorNot("AZUL", 1)).thenReturn(true);

        assertThatThrownBy(() -> colorService.actualizarColor(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otro color registrado con el nombre: AZUL");

        verify(colorRepository, times(1)).findById(1);
        verify(colorRepository, never()).save(any(Color.class));
    }

    @Test
    @DisplayName("No debe actualizar un color si el nuevo código hexadecimal ya existe")
    void actualizarColor_cuandoHexDuplicado_debeLanzarBadRequest() {
        Color colorExistente = new Color(1, "ROJO", "#FF0000");
        ColorRequestDTO requestDTO = new ColorRequestDTO("Azul", "#0000FF");

        when(colorRepository.findById(1)).thenReturn(Optional.of(colorExistente));
        when(colorRepository.existsByNombreColorIgnoreCaseAndIdColorNot("AZUL", 1)).thenReturn(false);
        when(colorRepository.existsByCodigoHexadecimalIgnoreCaseAndIdColorNot("#0000FF", 1)).thenReturn(true);

        assertThatThrownBy(() -> colorService.actualizarColor(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otro color registrado con el código hexadecimal: #0000FF");

        verify(colorRepository, times(1)).findById(1);
        verify(colorRepository, never()).save(any(Color.class));
    }

    @Test
    @DisplayName("Debe eliminar un color correctamente")
    void eliminarColor_cuandoExiste_debeEliminarColor() {
        Color color = new Color(1, "ROJO", "#FF0000");

        when(colorRepository.findById(1)).thenReturn(Optional.of(color));

        colorService.eliminarColor(1);

        verify(colorRepository, times(1)).findById(1);
        verify(colorRepository, times(1)).delete(color);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un color inexistente")
    void eliminarColor_cuandoNoExiste_debeLanzarExcepcion() {
        when(colorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colorService.eliminarColor(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el color con ID: 99");

        verify(colorRepository, times(1)).findById(99);
        verify(colorRepository, never()).delete(any(Color.class));
    }
}