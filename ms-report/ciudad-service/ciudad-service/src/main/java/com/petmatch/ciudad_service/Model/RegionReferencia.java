package com.petmatch.ciudad_service.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "region_referencia")
public class RegionReferencia {

    @Id
    @Column(name = "id_region")
    private Integer idRegion;

    @Column(name = "nombre_region", nullable = false, length = 100)
    private String nombreRegion;

    @Column(name = "id_pais", nullable = false)
    private Integer idPais;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public RegionReferencia() {
    }

    public RegionReferencia(Integer idRegion, String nombreRegion, Integer idPais, Boolean activo) {
        this.idRegion = idRegion;
        this.nombreRegion = nombreRegion;
        this.idPais = idPais;
        this.activo = activo;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}