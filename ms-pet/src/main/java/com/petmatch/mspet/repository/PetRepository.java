package com.petmatch.mspet.repository;

import com.petmatch.mspet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByIdUser(Long idUser);
    List<Pet> findByIdRace(Long idRace);
}
