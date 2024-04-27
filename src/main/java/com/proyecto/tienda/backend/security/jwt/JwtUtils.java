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

    @Value("${jwt.refresh.time.expiration}")
    private String jwtRefreshTimeExpiration;



    // GENERAR TOKEN DE ACCESO INCLUYENDO EL ROL DEL USUARIO EN EL TOKEN
    public String generateJwtToken(String email, String role) {
        return Jwts.builder()
                // .claim("role", role)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(jwtExpirationTime.trim()))) // Tiempo
                                                                                                                // de
                                                                                                                // expiración
                                                                                                                // por
                                                                                                                // eso
                                                                                                                // se
                                                                                                                // cierrra
                                                                                                                // la
                                                                                                                // sesion
                                                                                                                // despues
                                                                                                                // de 30
                                                                                                                // minutos
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();

    }


    public String generateJwtTokenRefresh(String email, String role) {
        return Jwts.builder()
                // .claim("role", role)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(jwtRefreshTimeExpiration.trim()))) // Tiempo
                                                                                                                // de
                                                                                                                // expiración
                                                                                                                // por
                                                                                                                // eso
                                                                                                                // se
                                                                                                                // cierrra
                                                                                                                // la
                                                                                                                // sesion
                                                                                                                // despues
                                                                                                                // de 30
                                                                                                                // minutos
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();

    }



    //



    // VALIDAR TOKEN DE ACCESO, AQUÍ SE LEE EL TOKEN
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

    // OBTIENE EL EMAIL DEL TOKEN
    public String getEmailFromToken(String token) {

        String email = getClaim(token, Claims::getSubject);
        return (email != null) ? email : ""; // Devuelve una cadena vacía si el email es nulo
    }

    // OBTIENE EL ID DEL TOKEN
    public String getUserIdFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // OBTIENE UN SOLO CLAIM DEL TOKEN
    public <T> T getClaim(String token, Function<Claims, T> claimsFunction) {

        Claims claims = extractAllClaims(token);
        return (claims != null) ? claimsFunction.apply(claims) : null; // Manejo si los reclamos son nulos
    }

    // OBTIENE TODOS LOS CLAIMS DEL TOKEN
    public Claims extractAllClaims(String token) {
        try {

            return Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {

            ex.printStackTrace();
            return null; // Otra acción dependiendo del flujo de mi aplicación.
        }
    }

    // OBTIENE LA FIRMA DEL TOKEN hola
    // public Key getSignatureKey() {

    // byte[] keyBytes = Decoders.BASE64.decode(secretKey);

    // return Keys.hmacShaKeyFor(keyBytes);
    // }

    public Key getSignatureKey() {
        if (secretKey == null) {
            throw new IllegalArgumentException("La clave secreta no puede ser nula");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}