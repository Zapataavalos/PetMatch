package com.petmatch.msrace.config;

import com.petmatch.msrace.model.Race;
import com.petmatch.msrace.repository.RaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RaceInitializer implements CommandLineRunner {

    private final RaceRepository raceRepository;

    public RaceInitializer(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public void run(String... args) {
        seed("MESTIZO", 1L, "PERRO");
        seed("LABRADOR", 1L, "PERRO");
        seed("SIAMES", 2L, "GATO");
    }

    private void seed(String nombre, Long animalTypeId, String animalTypeNombre) {
        if (raceRepository.existsByNombreIgnoreCaseAndAnimalTypeId(nombre, animalTypeId)) {
            return;
        }

        Race race = new Race();
        race.setNombre(nombre);
        race.setAnimalTypeId(animalTypeId);
        race.setAnimalTypeNombre(animalTypeNombre);
        raceRepository.save(race);
    }
}
