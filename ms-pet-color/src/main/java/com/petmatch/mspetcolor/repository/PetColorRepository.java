package com.petmatch.mspetcolor.repository;

import com.petmatch.mspetcolor.model.PetColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetColorRepository extends JpaRepository<PetColor, Long> {

    // Todos los colores de una mascota (MER)
    List<PetColor> findByIdPet(Long idPet);

    // Verificar si ya existe esa combinación mascota-color
    boolean existsByIdPetAndIdColor(Long idPet, Long idColor);
}
