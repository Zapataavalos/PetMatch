package com.petmatch.msrace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "races",
        uniqueConstraints = @UniqueConstraint(name = "uk_race_nombre_animal", columnNames = {"nombre", "animal_type_id"})
)
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "animal_type_id", nullable = false)
    private Long animalTypeId;

    @Column(nullable = false, length = 80)
    private String animalTypeNombre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getAnimalTypeId() {
        return animalTypeId;
    }

    public void setAnimalTypeId(Long animalTypeId) {
        this.animalTypeId = animalTypeId;
    }

    public String getAnimalTypeNombre() {
        return animalTypeNombre;
    }

    public void setAnimalTypeNombre(String animalTypeNombre) {
        this.animalTypeNombre = animalTypeNombre;
    }
}
