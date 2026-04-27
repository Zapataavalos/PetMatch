package com.petmatch.msrace.client;

import com.petmatch.msrace.dto.AnimalTypeResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-animal-type.
 * Valida que el idAnimalType exista antes de guardar una raza (MER).
 */
@FeignClient(name = "ms-animal-type", url = "${ms.animal-type.url}")
public interface AnimalTypeClient {

    @GetMapping("/api/animal-type/{id}")
    AnimalTypeResponseDTO getById(@PathVariable Long id);
}
