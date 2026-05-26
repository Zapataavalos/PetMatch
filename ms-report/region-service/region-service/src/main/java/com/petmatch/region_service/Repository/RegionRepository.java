package com.petmatch.region_service.Repository;

import com.petmatch.region_service.Model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

    List<Region> findByIdPais(Integer idPais);

    boolean existsByNombreRegionIgnoreCaseAndIdPais(String nombreRegion, Integer idPais);

    boolean existsByNombreRegionIgnoreCaseAndIdPaisAndIdRegionNot(
            String nombreRegion,
            Integer idPais,
            Integer idRegion
    );
}