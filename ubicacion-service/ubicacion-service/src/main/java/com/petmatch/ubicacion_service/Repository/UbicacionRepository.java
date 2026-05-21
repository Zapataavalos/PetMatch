package com.petmatch.ubicacion_service.Repository;

import com.petmatch.ubicacion_service.Model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    List<Ubicacion> findByIdCiudad(Integer idCiudad);

    boolean existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudad(
            String direccion,
            String numero,
            Integer idCiudad
    );

    boolean existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudadAndIdUbicacionNot(
            String direccion,
            String numero,
            Integer idCiudad,
            Integer idUbicacion
    );
}