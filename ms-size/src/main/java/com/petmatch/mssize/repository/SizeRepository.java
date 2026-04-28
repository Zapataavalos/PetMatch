package com.petmatch.mssize.repository;

import com.petmatch.mssize.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository Pattern — sección 4.2 del informe
@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    // Evitar duplicados por nombre
    Optional<Size> findByNameIgnoreCase(String name);
}
