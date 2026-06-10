package com.petmatch.mssize.config;

import com.petmatch.mssize.model.PetSize;
import com.petmatch.mssize.repository.PetSizeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SizeInitializer implements CommandLineRunner {

    private final PetSizeRepository petSizeRepository;

    public SizeInitializer(PetSizeRepository petSizeRepository) {
        this.petSizeRepository = petSizeRepository;
    }

    @Override
    public void run(String... args) {
        seed("PEQUENO", "Hasta 10 kg");
        seed("MEDIANO", "Entre 10 y 25 kg");
        seed("GRANDE", "Sobre 25 kg");
    }

    private void seed(String nombre, String descripcion) {
        if (petSizeRepository.existsByNombreIgnoreCase(nombre)) {
            return;
        }

        PetSize petSize = new PetSize();
        petSize.setNombre(nombre);
        petSize.setDescripcion(descripcion);
        petSizeRepository.save(petSize);
    }
}
