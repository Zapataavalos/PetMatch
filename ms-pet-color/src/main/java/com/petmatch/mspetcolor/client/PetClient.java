package com.petmatch.mspetcolor.client;

import com.petmatch.mspetcolor.dto.PetResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client hacia ms-pet.
 * Valida que el idPet exista antes de asignar un color (MER).
 */
@FeignClient(name = "ms-pet", url = "${ms.pet.url}")
public interface PetClient {

    @GetMapping("/api/pet/{id}")
    PetResponseDTO getById(@PathVariable Long id);
}
