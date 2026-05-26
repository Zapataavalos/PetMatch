package com.petmatch.usuario_service.Security;

import com.petmatch.usuario_service.Model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private Long jwtExpirationMs;

    public String generarToken(Usuario usuario, String nombreRol) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtExpirationMs);

        String rolNormalizado = normalizarRol(nombreRol);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("idUsuario", usuario.getIdUsuario())
                .claim("idRol", usuario.getIdRol())
                .claim("rol", rolNormalizado)
                .claim("role", rolNormalizado)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(obtenerClaveFirma())
                .compact();
    }

    public String obtenerEmailDesdeToken(String token) {
        return obtenerClaims(token).getSubject();
    }

    public String obtenerRolDesdeToken(String token) {
        Claims claims = obtenerClaims(token);

        String rol = claims.get("rol", String.class);

        if (rol == null || rol.isBlank()) {
            rol = claims.get("role", String.class);
        }

        return rol;
    }

    public boolean tokenValido(String token) {
        try {
            obtenerClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClaveFirma())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obtenerClaveFirma() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String normalizarRol(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            return "CIUDADANO";
        }

        String rol = Normalizer
                .normalize(nombreRol.trim().toUpperCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return switch (rol) {
            case "ADMINISTRADOR" -> "ADMIN";
            case "USUARIO" -> "CIUDADANO";
            case "DUENO" -> "DUENO";
            default -> rol;
        };
    }
}
