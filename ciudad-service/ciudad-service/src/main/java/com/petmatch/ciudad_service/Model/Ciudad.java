package com.petmatch.ciudad_service.Model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "ciudad",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ciudad_nombre_region",
                        columnNames = {"nombre_ciudad", "id_region"}
                )
        }
)
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciudad")
    private Integer idCiudad;

    @Column(name = "nombre_ciudad", nullable = false, length = 100)
    private String nombreCiudad;

    @Column(name = "id_region", nullable = false)
    private Integer idRegion;

    public Ciudad() {
    }

    public Ciudad(Integer idCiudad, String nombreCiudad, Integer idRegion) {
        this.idCiudad = idCiudad;
        this.nombreCiudad = nombreCiudad;
        this.idRegion = idRegion;
    }

    public Integer getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(Integer idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }

    public Integer getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(Integer idRegion) {
        this.idRegion = idRegion;
    }
}