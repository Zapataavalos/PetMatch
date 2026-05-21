package com.petmatch.pais_service.Model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "pais",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pais_nombre", columnNames = "nombre_pais")
        }
)
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pais")
    private Integer idPais;

    @Column(name = "nombre_pais", nullable = false, length = 80)
    private String nombrePais;

    public Pais() {
    }

    public Pais(Integer idPais, String nombrePais) {
        this.idPais = idPais;
        this.nombrePais = nombrePais;
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
}