package com.petmatch.mspet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PET")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPet;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Long idUser;

    @Column(nullable = false)
    private Long idRace;

    @Column(nullable = false)
    private Long idSize;
}
