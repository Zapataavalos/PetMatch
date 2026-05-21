package com.petmatch.ciudad_service.Repository;

import com.petmatch.ciudad_service.Model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {

    List<Ciudad> findByIdRegion(Integer idRegion);

    boolean existsByNombreCiudadIgnoreCaseAndIdRegion(String nombreCiudad, Integer idRegion);

    boolean existsByNombreCiudadIgnoreCaseAndIdRegionAndIdCiudadNot(
            String nombreCiudad,
            Integer idRegion,
            Integer idCiudad
    );
}