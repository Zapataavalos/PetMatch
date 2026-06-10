package com.petmatch.mssize.repository;

import com.petmatch.mssize.model.PetSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetSizeRepository extends JpaRepository<PetSize, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
