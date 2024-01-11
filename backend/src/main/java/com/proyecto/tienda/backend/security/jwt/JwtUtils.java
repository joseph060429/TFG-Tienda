package com.proyecto.tienda.backend.security.jwt;

import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import java.security.Key;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private String jwtExpirationTime;

    // Generar Token de acceso
    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(jwtExpirationTime.trim())))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    // Validar token acceso este lee el token
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Obtener email del token
    public String getEmailFromToken(String token) {

        String email = getClaim(token, Claims::getSubject);
        return (email != null) ? email : ""; // Devuelve una cadena vacía si el email es nuulo
    }

    // Obtener el id del token
    public String getUserIdFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // Obtener un solo claim del token
    public <T> T getClaim(String token, Function<Claims, T> claimsFunction) {

        Claims claims = extractAllClaims(token);
        return (claims != null) ? claimsFunction.apply(claims) : null; // Manejo si los reclamos son nulos
    }

    // Obtener todos los claims del token
    public Claims extractAllClaims(String token) {
        try {

            return Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {

            ex.printStackTrace();
            return null; // Otra acción dependiendo del flujo de tu aplicación.
        }
    }

    // Obtener firma del token
    public Key getSignatureKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}