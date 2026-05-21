package com.petmatch.usuario_service.Repository;

import com.petmatch.usuario_service.Model.RolReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolReferenciaRepository extends JpaRepository<RolReferencia, Integer> {

    boolean existsByIdRolAndActivoTrue(Integer idRol);

    Optional<RolReferencia> findByIdRolAndActivoTrue(Integer idRol);
}