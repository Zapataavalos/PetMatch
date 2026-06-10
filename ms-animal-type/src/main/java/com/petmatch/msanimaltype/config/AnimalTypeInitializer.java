package com.petmatch.msanimaltype.config;

import com.petmatch.msanimaltype.model.AnimalType;
import com.petmatch.msanimaltype.repository.AnimalTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AnimalTypeInitializer implements CommandLineRunner {

    private final AnimalTypeRepository animalTypeRepository;

    public AnimalTypeInitializer(AnimalTypeRepository animalTypeRepository) {
        this.animalTypeRepository = animalTypeRepository;
    }

    @Override
    public void run(String... args) {
        seed("PERRO", "Caninos domesticos");
        seed("GATO", "Felinos domesticos");
    }

    private void seed(String nombre, String descripcion) {
        if (animalTypeRepository.existsByNombreIgnoreCase(nombre)) {
            return;
        }

        AnimalType animalType = new AnimalType();
        animalType.setNombre(nombre);
        animalType.setDescripcion(descripcion);
        animalTypeRepository.save(animalType);
    }
}
