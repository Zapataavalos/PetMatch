package com.petmatch.region_service.Repository;

import com.petmatch.region_service.Model.PaisReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaisReferenciaRepository extends JpaRepository<PaisReferencia, Integer> {

    boolean existsByIdPaisAndActivoTrue(Integer idPais);
}