package com.petmatch.msreport.repository;
import com.petmatch.msreport.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByIdUser(Long idUser);
    List<Report> findByIdPet(Long idPet);
    List<Report> findByIdType(Long idType);
    List<Report> findByIdStatus(Long idStatus);
}
