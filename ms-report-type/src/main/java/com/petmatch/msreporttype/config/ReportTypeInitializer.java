package com.petmatch.msreporttype.config;

import com.petmatch.msreporttype.model.ReportType;
import com.petmatch.msreporttype.repository.ReportTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ReportTypeInitializer implements CommandLineRunner {

    private final ReportTypeRepository reportTypeRepository;

    public ReportTypeInitializer(ReportTypeRepository reportTypeRepository) {
        this.reportTypeRepository = reportTypeRepository;
    }

    @Override
    public void run(String... args) {
        seed("PERDIDA", "Mascota perdida por su tutor");
        seed("AVISTAMIENTO", "Mascota vista en la via publica");
        seed("RESCATE", "Mascota resguardada temporalmente");
    }

    private void seed(String nombre, String descripcion) {
        if (reportTypeRepository.existsByNombreIgnoreCase(nombre)) {
            return;
        }

        ReportType reportType = new ReportType();
        reportType.setNombre(nombre);
        reportType.setDescripcion(descripcion);
        reportTypeRepository.save(reportType);
    }
}
