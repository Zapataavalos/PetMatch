package com.petmatch.ubicacion_service.Repository;

import com.petmatch.ubicacion_service.Model.CiudadReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadReferenciaRepository extends JpaRepository<CiudadReferencia, Integer> {

    boolean existsByIdCiudadAndActivoTrue(Integer idCiudad);
}