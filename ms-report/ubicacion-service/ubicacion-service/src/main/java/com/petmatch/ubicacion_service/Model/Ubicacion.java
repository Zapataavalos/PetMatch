package com.petmatch.ubicacion_service.Model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "ubicacion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ubicacion_direccion_numero_ciudad",
                        columnNames = {"direccion", "numero", "id_ciudad"}
                )
        }
)
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Integer idUbicacion;

    @Column(name = "direccion", nullable = false, length = 150)
    private String direccion;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "referencia", length = 150)
    private String referencia;

    @Column(name = "codigo_postal", length = 15)
    private String codigoPostal;

    @Column(name = "latitud", nullable = false)
    private Double latitud;

    @Column(name = "longitud", nullable = false)
    private Double longitud;

    @Column(name = "id_ciudad", nullable = false)
    private Integer idCiudad;

    public Ubicacion() {
    }

    public Ubicacion(
            Integer idUbicacion,
            String direccion,
            String numero,
            String referencia,
            String codigoPostal,
            Double latitud,
            Double longitud,
            Integer idCiudad
    ) {
        this.idUbicacion = idUbicacion;
        this.direccion = direccion;
        this.numero = numero;
        this.referencia = referencia;
        this.codigoPostal = codigoPostal;
        this.latitud = latitud;
        this.longitud = longitud;
        this.idCiudad = idCiudad;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Integer getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(Integer idCiudad) {
        this.idCiudad = idCiudad;
    }
}