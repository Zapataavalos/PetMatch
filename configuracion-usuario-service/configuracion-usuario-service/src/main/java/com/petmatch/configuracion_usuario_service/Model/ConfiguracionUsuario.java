package com.petmatch.configuracion_usuario_service.Model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "configuracion_usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_config_usuario", columnNames = "id_usuario")
        }
)
public class ConfiguracionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion_usuario")
    private Integer idConfiguracionUsuario;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "id_color", nullable = false)
    private Integer idColor;

    @Column(name = "notificaciones_activas", nullable = false)
    private Boolean notificacionesActivas;

    @Column(name = "modo_oscuro", nullable = false)
    private Boolean modoOscuro;

    @Column(name = "idioma", nullable = false, length = 5)
    private String idioma;

    public ConfiguracionUsuario() {
    }

    public ConfiguracionUsuario(Integer idConfiguracionUsuario, Integer idUsuario, Integer idColor,
                                Boolean notificacionesActivas, Boolean modoOscuro, String idioma) {
        this.idConfiguracionUsuario = idConfiguracionUsuario;
        this.idUsuario = idUsuario;
        this.idColor = idColor;
        this.notificacionesActivas = notificacionesActivas;
        this.modoOscuro = modoOscuro;
        this.idioma = idioma;
    }

    public Integer getIdConfiguracionUsuario() {
        return idConfiguracionUsuario;
    }

    public void setIdConfiguracionUsuario(Integer idConfiguracionUsuario) {
        this.idConfiguracionUsuario = idConfiguracionUsuario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdColor() {
        return idColor;
    }

    public void setIdColor(Integer idColor) {
        this.idColor = idColor;
    }

    public Boolean getNotificacionesActivas() {
        return notificacionesActivas;
    }

    public void setNotificacionesActivas(Boolean notificacionesActivas) {
        this.notificacionesActivas = notificacionesActivas;
    }

    public Boolean getModoOscuro() {
        return modoOscuro;
    }

    public void setModoOscuro(Boolean modoOscuro) {
        this.modoOscuro = modoOscuro;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
}