package com.petmatch.pais_service.Repository;

import com.petmatch.pais_service.Model.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Integer> {

    boolean existsByNombrePaisIgnoreCase(String nombrePais);

    boolean existsByNombrePaisIgnoreCaseAndIdPaisNot(String nombrePais, Integer idPais);
}