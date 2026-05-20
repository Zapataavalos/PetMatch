package com.petmatch.rol_service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "rol",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_rol_nombre", columnNames = "nombre_rol")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rol{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false, length = 50)
    private String nombreRol;
}