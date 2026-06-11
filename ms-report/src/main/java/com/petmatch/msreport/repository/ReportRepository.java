package com.petmatch.msreport.repository;

import com.petmatch.msreport.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByOrderByCreatedAtDesc();

    Optional<Report> findByCodigo(String codigo);
}
