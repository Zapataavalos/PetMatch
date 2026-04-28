package com.petmatch.mspetcolor.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * MER: MASCOTA_COLOR
 *  - idPetColor  PK
 *  - idPet       FK → PET      (ms-pet)
 *  - idColor     FK → COLOR    (tabla local en este microservicio)
 *
 * Relación many-to-many resuelta como entidad propia según el MER.
 */
@Entity
@Table(name = "PET_COLOR",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_pet", "id_color"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPetColor;

    // FK → ms-pet
    @Column(name = "id_pet", nullable = false)
    private Long idPet;

    // FK → Color (tabla local)
    @Column(name = "id_color", nullable = false)
    private Long idColor;
}
