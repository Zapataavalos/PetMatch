package com.petmatch.ubicacion_service.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "ciudad_referencia")
public class CiudadReferencia {

    @Id
    @Column(name = "id_ciudad")
    private Integer idCiudad;

    @Column(name = "nombre_ciudad", nullable = false, length = 100)
    private String nombreCiudad;

    @Column(name = "id_region", nullable = false)
    private Integer idRegion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public CiudadReferencia() {
    }

    public CiudadReferencia(Integer idCiudad, String nombreCiudad, Integer idRegion, Boolean activo) {
        this.idCiudad = idCiudad;
        this.nombreCiudad = nombreCiudad;
        this.idRegion = idRegion;
        this.activo = activo;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}