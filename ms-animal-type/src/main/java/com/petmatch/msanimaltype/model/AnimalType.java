package com.petmatch.msanimaltype.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * MER: TIPO_ANIMAL
 *  - idAnimalType  PK
 *  - name
 *
 * Es referenciado por ms-race (RAZA.idAnimalType)
 * ms-race valida contra este servicio via Feign Client.
 */
@Entity
@Table(name = "ANIMAL_TYPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAnimalType;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
