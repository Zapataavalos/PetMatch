package com.petmatch.region_service.Model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "region",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_region_nombre_pais",
                        columnNames = {"nombre_region", "id_pais"}
                )
        }
)
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private Integer idRegion;

    @Column(name = "nombre_region", nullable = false, length = 100)
    private String nombreRegion;

    @Column(name = "id_pais", nullable = false)
    private Integer idPais;

    public Region() {
    }

    public Region(Integer idRegion, String nombreRegion, Integer idPais) {
        this.idRegion = idRegion;
        this.nombreRegion = nombreRegion;
        this.idPais = idPais;
    }

    public Integer getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(Integer idRegion) {
        this.idRegion = idRegion;
    }

    public String getNombreRegion() {
        return nombreRegion;
    }

    public void setNombreRegion(String nombreRegion) {
        this.nombreRegion = nombreRegion;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }
}