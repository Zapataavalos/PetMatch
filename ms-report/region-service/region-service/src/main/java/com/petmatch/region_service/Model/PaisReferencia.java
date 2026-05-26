package com.petmatch.region_service.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "pais_referencia")
public class PaisReferencia {

    @Id
    @Column(name = "id_pais")
    private Integer idPais;

    @Column(name = "nombre_pais", nullable = false, length = 80)
    private String nombrePais;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public PaisReferencia() {
    }

    public PaisReferencia(Integer idPais, String nombrePais, Boolean activo) {
        this.idPais = idPais;
        this.nombrePais = nombrePais;
        this.activo = activo;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}