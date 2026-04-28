package com.petmatch.msreport.client;
import com.petmatch.msreport.dto.ReportTypeResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "ms-report-type", url = "${ms.report-type.url}")
public interface ReportTypeClient {
    @GetMapping("/api/report-type/{id}")
    ReportTypeResponseDTO getById(@PathVariable Long id);
}
