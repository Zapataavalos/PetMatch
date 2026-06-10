package com.petmatch.mspetcolor.repository;

import com.petmatch.mspetcolor.model.PetColor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetColorRepository extends JpaRepository<PetColor, Long> {
    boolean existsByPetIdAndColorId(Long petId, Integer colorId);

    boolean existsByPetIdAndColorIdAndIdNot(Long petId, Integer colorId, Long id);

    List<PetColor> findByPetIdOrderByColorNombreAsc(Long petId);
}
