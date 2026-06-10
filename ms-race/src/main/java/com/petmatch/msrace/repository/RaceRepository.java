package com.petmatch.msrace.repository;

import com.petmatch.msrace.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceRepository extends JpaRepository<Race, Long> {
    boolean existsByNombreIgnoreCaseAndAnimalTypeId(String nombre, Long animalTypeId);

    boolean existsByNombreIgnoreCaseAndAnimalTypeIdAndIdNot(String nombre, Long animalTypeId, Long id);

    List<Race> findByAnimalTypeIdOrderByNombreAsc(Long animalTypeId);
}
