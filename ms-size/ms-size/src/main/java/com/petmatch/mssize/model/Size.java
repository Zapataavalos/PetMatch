package com.petmatch.mssize.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * MER: TAMANO
 *  - idSize  PK
 *  - name
 *
 * Es referenciado por ms-pet (PET.idSize).
 * ms-pet valida contra este servicio via Feign Client.
 */
@Entity
@Table(name = "SIZE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSize;

    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
