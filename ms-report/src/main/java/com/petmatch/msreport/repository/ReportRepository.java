package com.petmatch.msreport.repository;

import com.petmatch.msreport.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository Pattern — sección 4.2 del informe
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Reportes de un usuario específico
    List<Report> findByIdUser(Long idUser);

    // Reportes de una mascota específica
    List<Report> findByIdPet(Long idPet);

    // Reportes filtrados por tipo (1=PERDIDA, 2=ENCONTRADA, 3=EN_PELIGRO)
    List<Report> findByIdType(Long idType);

    // Reportes filtrados por estado (1=ACTIVO, 2=RESUELTO, 3=URGENTE)
    List<Report> findByIdStatus(Long idStatus);
}
