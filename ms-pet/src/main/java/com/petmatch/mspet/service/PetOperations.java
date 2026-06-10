package com.petmatch.mspet.service;

import com.petmatch.mspet.dto.PetRequest;
import com.petmatch.mspet.dto.PetResponse;

import java.util.List;

public interface PetOperations {

    List<PetResponse> listarMascotas();

    PetResponse buscarMascota(Long id);

    PetResponse crearMascota(PetRequest request);

    PetResponse actualizarMascota(Long id, PetRequest request);

    void eliminarMascota(Long id);
}
