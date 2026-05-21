package com.petmatch.configuracion_usuario_service.Repository;

import com.petmatch.configuracion_usuario_service.Model.UsuarioReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioReferenciaRepository extends JpaRepository<UsuarioReferencia, Integer> {

    boolean existsByIdUsuarioAndActivoTrue(Integer idUsuario);
}