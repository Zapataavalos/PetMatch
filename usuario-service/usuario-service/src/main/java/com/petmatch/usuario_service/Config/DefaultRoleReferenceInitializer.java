package com.petmatch.usuario_service.Config;

import com.petmatch.usuario_service.Model.RolReferencia;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DefaultRoleReferenceInitializer {

    private static final List<RolReferencia> DEFAULT_ROLE_REFERENCES = List.of(
            new RolReferencia(1, "ADMINISTRADOR", true),
            new RolReferencia(2, "DUENO", true),
            new RolReferencia(3, "CIUDADANO", true)
    );

    @Bean
    public CommandLineRunner seedDefaultRoleReferences(RolReferenciaRepository rolReferenciaRepository) {
        return args -> rolReferenciaRepository.saveAll(DEFAULT_ROLE_REFERENCES);
    }
}
