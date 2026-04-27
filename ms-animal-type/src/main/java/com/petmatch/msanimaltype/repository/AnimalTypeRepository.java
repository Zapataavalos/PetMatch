package com.petmatch.msanimaltype.repository;

import com.petmatch.msanimaltype.model.AnimalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository Pattern — sección 4.2 del informe
@Repository
public interface AnimalTypeRepository extends JpaRepository<AnimalType, Long> {

    // Buscar por nombre para evitar duplicados
    Optional<AnimalType> findByNameIgnoreCase(String name);
}
