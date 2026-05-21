package com.petmatch.configuracion_usuario_service.Repository;

import com.petmatch.configuracion_usuario_service.Model.ColorReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorReferenciaRepository extends JpaRepository<ColorReferencia, Integer> {

    boolean existsByIdColorAndActivoTrue(Integer idColor);
}