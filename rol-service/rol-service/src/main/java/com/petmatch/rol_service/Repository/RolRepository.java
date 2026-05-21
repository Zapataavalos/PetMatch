package com.petmatch.rol_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.petmatch.rol_service.Model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    boolean existsByNombreRolIgnoreCase(String nombreRol);

    boolean existsByNombreRolIgnoreCaseAndIdRolNot(String nombreRol, Integer idRol);
}