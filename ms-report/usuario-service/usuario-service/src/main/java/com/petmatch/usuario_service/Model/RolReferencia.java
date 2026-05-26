package com.petmatch.usuario_service.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "rol_referencia")
public class RolReferencia {

    @Id
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false, length = 50)
    private String nombreRol;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public RolReferencia() {
    }

    public RolReferencia(Integer idRol, String nombreRol, Boolean activo) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.activo = activo;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}