package com.petmatch.mspet.service;

import com.petmatch.mspet.dto.PetRequest;
import com.petmatch.mspet.dto.PetResponse;
import com.petmatch.mspet.messaging.PetEventPublisher;
import com.petmatch.mspet.model.Pet;
import com.petmatch.mspet.model.PetStatus;
import com.petmatch.mspet.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetEventPublisher eventPublisher;

    @InjectMocks
    private PetService petService;

    @Test
    void listarMascotasRetornaMascotasOrdenadas() {
        Pet pet = pet(
                1L,
                "Milo",
                "Perro",
                "Mestizo",
                "Mediano",
                PetStatus.ACTIVO,
                "Amigable",
                "https://example.com/milo.jpg"
        );
        when(petRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(pet));

        List<PetResponse> result = petService.listarMascotas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).nombre()).isEqualTo("Milo");
    }

    @Test
    void buscarMascotaRetornaMascotaExistente() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet(
                1L,
                "Luna",
                "Gato",
                "Domestico",
                "Pequeno",
                PetStatus.EN_REFUGIO,
                "Tranquila",
                "https://example.com/luna.jpg"
        )));

        PetResponse result = petService.buscarMascota(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nombre()).isEqualTo("Luna");
        assertThat(result.estado()).isEqualTo(PetStatus.EN_REFUGIO);
    }

    @Test
    void buscarMascotaInexistenteLanzaNotFound() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.buscarMascota(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void crearMascotaValidaGuardaYPublicaEvento() {
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            pet.setId(10L);
            pet.setCreatedAt(LocalDateTime.of(2026, 6, 4, 10, 0));
            return pet;
        });

        PetResponse result = petService.crearMascota(new PetRequest(
                "  Nala  ",
                "Perro",
                "Labrador",
                "Grande",
                PetStatus.ACTIVO,
                "Juguetona",
                "https://example.com/nala.jpg"
        ));

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nombre()).isEqualTo("Nala");
        assertThat(result.raza()).isEqualTo("Labrador");
        verify(eventPublisher).publish(eq("CREATED"), eq("PET"), eq(result));
    }

    @Test
    void crearMascotaAplicaValoresPorDefecto() {
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            pet.setId(11L);
            return pet;
        });

        PetResponse result = petService.crearMascota(new PetRequest(
                " ",
                " ",
                null,
                "",
                null,
                " ",
                null
        ));

        assertThat(result.nombre()).isEqualTo("Mascota sin nombre");
        assertThat(result.tipo()).isEqualTo("No informado");
        assertThat(result.raza()).isEqualTo("Mestizo");
        assertThat(result.tamano()).isEqualTo("Mediano");
        assertThat(result.estado()).isEqualTo(PetStatus.ACTIVO);
        assertThat(result.descripcion()).isEqualTo("Sin descripcion");
        assertThat(result.imagenUrl()).contains("images.unsplash.com");
    }

    @Test
    void actualizarMascotaExistenteGuardaYPublicaEvento() {
        Pet existing = pet(
                1L,
                "Milo",
                "Perro",
                "Mestizo",
                "Mediano",
                PetStatus.ACTIVO,
                "Amigable",
                "https://example.com/milo.jpg"
        );
        when(petRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(petRepository.save(existing)).thenReturn(existing);

        PetResponse result = petService.actualizarMascota(1L, new PetRequest(
                "Max",
                "Perro",
                "Golden",
                "Grande",
                PetStatus.REPORTADO_PERDIDO,
                "Necesita ayuda",
                "https://example.com/max.jpg"
        ));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nombre()).isEqualTo("Max");
        assertThat(result.estado()).isEqualTo(PetStatus.REPORTADO_PERDIDO);
        verify(eventPublisher).publish(eq("UPDATED"), eq("PET"), eq(result));
    }

    @Test
    void eliminarMascotaExistenteEliminaYPublicaEvento() {
        when(petRepository.existsById(1L)).thenReturn(true);

        petService.eliminarMascota(1L);

        verify(petRepository).deleteById(1L);
        verify(eventPublisher).publish("DELETED", "PET", Map.of("id", 1L));
    }

    @Test
    void eliminarMascotaInexistenteLanzaNotFound() {
        when(petRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> petService.eliminarMascota(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Pet pet(
            Long id,
            String nombre,
            String tipo,
            String raza,
            String tamano,
            PetStatus estado,
            String descripcion,
            String imagenUrl
    ) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setNombre(nombre);
        pet.setTipo(tipo);
        pet.setRaza(raza);
        pet.setTamano(tamano);
        pet.setEstado(estado);
        pet.setDescripcion(descripcion);
        pet.setImagenUrl(imagenUrl);
        pet.setCreatedAt(LocalDateTime.of(2026, 6, 4, 9, 0));
        return pet;
    }
}
