package com.petmatch.msreport.client;
import com.petmatch.msreport.dto.PetResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "ms-pet", url = "${ms.pet.url}")
public interface PetClient {
    @GetMapping("/api/pet/{id}")
    PetResponseDTO getById(@PathVariable Long id);
}
