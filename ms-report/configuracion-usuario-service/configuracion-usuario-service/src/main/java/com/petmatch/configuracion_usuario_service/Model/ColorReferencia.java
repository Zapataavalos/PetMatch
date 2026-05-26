package com.petmatch.configuracion_usuario_service.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "color_referencia")
public class ColorReferencia {

    @Id
    @Column(name = "id_color")
    private Integer idColor;

    @Column(name = "nombre_color", nullable = false, length = 50)
    private String nombreColor;

    @Column(name = "codigo_hexadecimal", nullable = false, length = 7)
    private String codigoHexadecimal;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public ColorReferencia() {
    }

    public ColorReferencia(Integer idColor, String nombreColor, String codigoHexadecimal, Boolean activo) {
        this.idColor = idColor;
        this.nombreColor = nombreColor;
        this.codigoHexadecimal = codigoHexadecimal;
        this.activo = activo;
    }

    public Integer getIdColor() {
        return idColor;
    }

    public void setIdColor(Integer idColor) {
        this.idColor = idColor;
    }

    public String getNombreColor() {
        return nombreColor;
    }

    public void setNombreColor(String nombreColor) {
        this.nombreColor = nombreColor;
    }

    public String getCodigoHexadecimal() {
        return codigoHexadecimal;
    }

    public void setCodigoHexadecimal(String codigoHexadecimal) {
        this.codigoHexadecimal = codigoHexadecimal;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}