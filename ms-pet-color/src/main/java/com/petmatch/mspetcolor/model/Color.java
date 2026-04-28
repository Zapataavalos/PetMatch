package com.petmatch.mspetcolor.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * MER: COLOR
 *  - idColor  PK
 *  - name
 *
 * Tabla local de este microservicio.
 * Usada como catálogo de colores disponibles.
 */
@Entity
@Table(name = "COLOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idColor;

    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
