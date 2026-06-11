package com.petmatch.mspet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.mspet.dto.PetRequest;
import com.petmatch.mspet.dto.PetResponse;
import com.petmatch.mspet.model.PetStatus;
import com.petmatch.mspet.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetController.class)
@AutoConfigureMockMvc(addFilters = false)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    @Test
    void listarMascotasRetornaOkYJson() throws Exception {
        when(petService.listarMascotas()).thenReturn(List.of(response(1L, "Milo")));

        mockMvc.perform(get("/api/pet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Milo"));
    }

    @Test
    void buscarMascotaExistenteRetornaOk() throws Exception {
        when(petService.buscarMascota(1L)).thenReturn(response(1L, "Luna"));

        mockMvc.perform(get("/api/pet/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Luna"));
    }

    @Test
    void buscarMascotaInexistenteRetornaNotFound() throws Exception {
        when(petService.buscarMascota(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        mockMvc.perform(get("/api/pet/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearMascotaValidaRetornaCreated() throws Exception {
        PetRequest request = request("Nala");
        when(petService.crearMascota(any(PetRequest.class))).thenReturn(response(10L, "Nala"));

        mockMvc.perform(post("/api/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Nala"));
    }

    @Test
    void actualizarMascotaValidaRetornaOk() throws Exception {
        PetRequest request = request("Max");
        when(petService.actualizarMascota(eq(1L), any(PetRequest.class))).thenReturn(response(1L, "Max"));

        mockMvc.perform(put("/api/pet/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Max"));
    }

    @Test
    void eliminarMascotaExistenteRetornaNoContent() throws Exception {
        doNothing().when(petService).eliminarMascota(1L);

        mockMvc.perform(delete("/api/pet/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarMascotaInexistenteRetornaNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"))
                .when(petService)
                .eliminarMascota(99L);

        mockMvc.perform(delete("/api/pet/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    private PetRequest request(String nombre) {
        return new PetRequest(
                nombre,
                "Perro",
                "Mestizo",
                "Mediano",
                PetStatus.ACTIVO,
                "Descripcion valida",
                "https://example.com/pet.jpg"
        );
    }

    private PetResponse response(Long id, String nombre) {
        return new PetResponse(
                id,
                nombre,
                "Perro",
                "Mestizo",
                "Mediano",
                PetStatus.ACTIVO,
                "Descripcion valida",
                "https://example.com/pet.jpg",
                LocalDateTime.of(2026, 6, 4, 9, 0)
        );
    }
}
