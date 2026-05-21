package com.petmatch.ciudad_service.Repository;

import com.petmatch.ciudad_service.Model.RegionReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionReferenciaRepository extends JpaRepository<RegionReferencia, Integer> {

    boolean existsByIdRegionAndActivoTrue(Integer idRegion);
}