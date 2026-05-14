package com.petmatch.msreportstatus.repository;
import com.petmatch.msreportstatus.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ReportStatusRepository extends JpaRepository<ReportStatus, Long> {
    Optional<ReportStatus> findByNameIgnoreCase(String name);
}
