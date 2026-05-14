package com.petmatch.msreporttype.repository;
import com.petmatch.msreporttype.model.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ReportTypeRepository extends JpaRepository<ReportType, Long> {
    Optional<ReportType> findByNameIgnoreCase(String name);
}
