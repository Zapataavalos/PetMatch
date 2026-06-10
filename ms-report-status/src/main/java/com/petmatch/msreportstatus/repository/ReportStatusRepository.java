package com.petmatch.msreportstatus.repository;

import com.petmatch.msreportstatus.model.ReportStatusCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportStatusRepository extends JpaRepository<ReportStatusCatalog, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
