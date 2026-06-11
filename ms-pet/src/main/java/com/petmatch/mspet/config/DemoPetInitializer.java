package com.petmatch.mspet.config;

import com.petmatch.mspet.messaging.EventPublisher;
import com.petmatch.mspet.model.PetStatus;
import com.petmatch.mspet.repository.PetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DemoPetInitializer {

    private static final List<DemoPet> DEMO_PETS = List.of(
            new DemoPet(
                    1L,
                    "Bruno",
                    "Perro",
                    "Mestizo",
                    "Mediano",
                    PetStatus.ACTIVO,
                    "Jugueton y sociable. Registrado como mascota demo.",
                    "https://images.unsplash.com/photo-1587300003388-59208cc962cb?q=80&w=600&auto=format&fit=crop",
                    LocalDateTime.now().minusDays(3)
            ),
            new DemoPet(
                    2L,
                    "Mishi",
                    "Gato",
                    "Europeo",
                    "Pequeno",
                    PetStatus.REPORTADO_PERDIDO,
                    "Gata gris con collar verde.",
                    "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=600&auto=format&fit=crop",
                    LocalDateTime.now().minusDays(2)
            ),
            new DemoPet(
                    3L,
                    "Rocky",
                    "Perro",
                    "Labrador",
                    "Grande",
                    PetStatus.EN_REFUGIO,
                    "Resguardado temporalmente en refugio colaborador.",
                    "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=600&auto=format&fit=crop",
                    LocalDateTime.now().minusDays(1)
            )
    );

    @Bean
    public CommandLineRunner seedDemoPets(
            JdbcTemplate jdbcTemplate,
            PetRepository petRepository,
            EventPublisher eventPublisher
    ) {
        return args -> {
            for (DemoPet pet : DEMO_PETS) {
                jdbcTemplate.update(
                        """
                                insert into pets (
                                    id,
                                    nombre,
                                    tipo,
                                    raza,
                                    tamano,
                                    estado,
                                    descripcion,
                                    imagen_url,
                                    created_at
                                )
                                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                on duplicate key update
                                    nombre = values(nombre),
                                    tipo = values(tipo),
                                    raza = values(raza),
                                    tamano = values(tamano),
                                    estado = values(estado),
                                    descripcion = values(descripcion),
                                    imagen_url = values(imagen_url),
                                    created_at = values(created_at)
                                """,
                        pet.id(),
                        pet.nombre(),
                        pet.tipo(),
                        pet.raza(),
                        pet.tamano(),
                        pet.estado().name(),
                        pet.descripcion(),
                        pet.imagenUrl(),
                        pet.createdAt()
                );

                petRepository.findById(pet.id())
                        .ifPresent(savedPet -> eventPublisher.publish("UPSERTED", "PET", savedPet));
            }
        };
    }

    private record DemoPet(
            Long id,
            String nombre,
            String tipo,
            String raza,
            String tamano,
            PetStatus estado,
            String descripcion,
            String imagenUrl,
            LocalDateTime createdAt
    ) {
    }
}
