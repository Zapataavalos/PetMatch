package com.petmatch.msrace.repository;

import com.petmatch.msrace.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {

    // Buscar todas las razas de un tipo de animal (relación MER)
    List<Race> findByIdAnimalType(Long idAnimalType);
}
