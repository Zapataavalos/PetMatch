package com.petmatch.configuracion_usuario_service.Repository;

import com.petmatch.configuracion_usuario_service.Model.ConfiguracionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionUsuarioRepository extends JpaRepository<ConfiguracionUsuario, Integer> {

    Optional<ConfiguracionUsuario> findByIdUsuario(Integer idUsuario);

    boolean existsByIdUsuario(Integer idUsuario);

    boolean existsByIdUsuarioAndIdConfiguracionUsuarioNot(Integer idUsuario, Integer idConfiguracionUsuario);
}