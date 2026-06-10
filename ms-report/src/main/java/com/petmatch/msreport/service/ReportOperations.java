package com.petmatch.msreport.service;

import com.petmatch.msreport.dto.ReportRequest;
import com.petmatch.msreport.dto.ReportResponse;

import java.util.List;

public interface ReportOperations {

    List<ReportResponse> listarReportes();

    ReportResponse crearReporte(ReportRequest request);

    void eliminarReporte(Long id);

    ReportResponse marcarComoEncontrado(Long id);
}
