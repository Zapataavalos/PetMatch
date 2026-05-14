package com.petmatch.msreport.client;

import com.petmatch.msreport.dto.ReportStatusResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client → ms-report-status (puerto 8087).
 * Valida el estado y obtiene su nombre para enriquecer la respuesta.
 */
@FeignClient(name = "ms-report-status", url = "${ms.report-status.url}")
public interface ReportStatusClient {

    @GetMapping("/api/report-status/{id}")
    ReportStatusResponseDTO getById(@PathVariable Long id);
}
