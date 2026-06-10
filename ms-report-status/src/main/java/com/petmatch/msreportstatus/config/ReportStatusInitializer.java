package com.petmatch.msreportstatus.config;

import com.petmatch.msreportstatus.model.ReportStatusCatalog;
import com.petmatch.msreportstatus.repository.ReportStatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ReportStatusInitializer implements CommandLineRunner {

    private final ReportStatusRepository reportStatusRepository;

    public ReportStatusInitializer(ReportStatusRepository reportStatusRepository) {
        this.reportStatusRepository = reportStatusRepository;
    }

    @Override
    public void run(String... args) {
        seed("PERDIDO", "Reporte abierto por mascota perdida");
        seed("EN_REFUGIO", "Mascota resguardada temporalmente");
        seed("EN_PELIGRO", "Mascota en zona o condicion de riesgo");
        seed("ENCONTRADO", "Mascota encontrada o reunida");
    }

    private void seed(String nombre, String descripcion) {
        if (reportStatusRepository.existsByNombreIgnoreCase(nombre)) {
            return;
        }

        ReportStatusCatalog status = new ReportStatusCatalog();
        status.setNombre(nombre);
        status.setDescripcion(descripcion);
        reportStatusRepository.save(status);
    }
}
