package com.petmatch.msreport.config;

import com.petmatch.msreport.model.Report;
import com.petmatch.msreport.model.ReportStatus;
import com.petmatch.msreport.repository.ReportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DemoReportInitializer implements CommandLineRunner {

    private final ReportRepository reportRepository;

    public DemoReportInitializer(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public void run(String... args) {
        if (reportRepository.count() > 0) {
            return;
        }

        create(
                "REP-001",
                "Max",
                "Golden retriever con collar azul.",
                "Santiago Centro",
                ReportStatus.PERDIDO,
                "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=600&auto=format&fit=crop",
                -33.4489,
                -70.6693,
                LocalDateTime.now().minusHours(2)
        );
        create(
                "REP-002",
                "Luna",
                "Gata negra resguardada por una vecina.",
                "Providencia",
                ReportStatus.EN_REFUGIO,
                "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=600&auto=format&fit=crop",
                -33.4263,
                -70.6170,
                LocalDateTime.now().minusHours(5)
        );
        create(
                "REP-003",
                "Desconocido",
                "Perro visto cerca de una avenida con mucho transito.",
                "Las Condes",
                ReportStatus.EN_PELIGRO,
                "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop",
                -33.4089,
                -70.5675,
                LocalDateTime.now().minusMinutes(10)
        );
    }

    private void create(
            String codigo,
            String nombre,
            String descripcion,
            String ubicacion,
            ReportStatus estado,
            String imagenUrl,
            Double latitud,
            Double longitud,
            LocalDateTime createdAt
    ) {
        Report report = new Report();
        report.setCodigo(codigo);
        report.setNombre(nombre);
        report.setDescripcion(descripcion);
        report.setUbicacion(ubicacion);
        report.setEstado(estado);
        report.setImagenUrl(imagenUrl);
        report.setLatitud(latitud);
        report.setLongitud(longitud);
        report.setCreatedAt(createdAt);
        reportRepository.save(report);
    }
}
