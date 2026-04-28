package com.petmatch.msrace.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * MER: RAZA
 *  - idRaza      PK
 *  - nombre
 *  - idTipoAnimal  FK → TIPO_ANIMAL (ms-animal-type)
 *
 * En microservicios la FK es solo un Long; la integridad
 * referencial se valida llamando a ms-animal-type vía Feign.
 */
@Entity
@Table(name = "RACE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRace;

    @Column(nullable = false, length = 100)
    private String name;

    // FK → ms-animal-type (TIPO_ANIMAL)
    @Column(nullable = false)
    private Long idAnimalType;
}
