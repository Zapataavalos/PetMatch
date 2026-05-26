package com.petmatch.color_service.Model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "color",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_color_nombre", columnNames = "nombre_color"),
                @UniqueConstraint(name = "uk_color_hex", columnNames = "codigo_hexadecimal")
        }
)
public class Color{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_color")
    private Integer idColor;

    @Column(name = "nombre_color", nullable = false, length = 50)
    private String nombreColor;

    @Column(name = "codigo_hexadecimal", nullable = false, length = 7)
    private String codigoHexadecimal;

    public Color() {
    }

    public Color(Integer idColor, String nombreColor, String codigoHexadecimal) {
        this.idColor = idColor;
        this.nombreColor = nombreColor;
        this.codigoHexadecimal = codigoHexadecimal;
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
}