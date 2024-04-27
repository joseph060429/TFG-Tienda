package com.proyecto.tienda.backend.security.filter;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.User;
import java.time.format.DateTimeFormatter;

//CLASE PARA CUANDO EL USUARIO INTENTE AUTENTICARSE
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtUtils jwtUtils;

    @Autowired
    UsuarioModelo usuarios;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // PROCESO DE AUTENTICACION
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        UsuarioModelo usuario = null;
        String email = "";
        String password = "";

        try {

            usuario = new ObjectMapper().readValue(request.getInputStream(), UsuarioModelo.class);
            email = usuario.getEmail().trim();
            // No le pongo el .trim a la password porque la contraseña tiene que ser
            // exactamente igual
            password = usuario.getPassword();

        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    // CUANDO LA AUTENTICACION ES CORRECTA
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        // Este user es el de Spring Security para obtener los detalles del usuario
        User user = (User) authResult.getPrincipal();

        // Obtén el rol del usuario
        String userRole = user.getAuthorities().iterator().next().getAuthority();

        // Obtenemos el token con el email y el rol del usuario
        String token = jwtUtils.generateJwtToken(user.getUsername(), userRole);

        String refreshToken = jwtUtils.generateJwtTokenRefresh(user.getUsername(), userRole);

        String userEmail = user.getUsername();

        // Obtén la fecha y hora actual
        LocalDateTime now = LocalDateTime.now();

        // Calcula la fecha de expiración (30 minutos desde ahora)
        LocalDateTime expirationTime = now.plusMinutes(30);

        // Definir el formato deseado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formatea la fecha de expiración como una cadena
        String expirationTimeString = expirationTime.format(formatter);


        response.addHeader("Authorization", token);

        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("Message", "Autenticación Correcta");
        httpResponse.put("refreshToken", refreshToken);
        httpResponse.put("roles", userRole);
        httpResponse.put("email", userEmail);
        httpResponse.put("tiempoExpiracion", expirationTimeString);

        // Convertir el mapa en el json
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.getWriter().flush();

        response.setHeader("refreshToken", refreshToken);

        // super.successfulAuthentication(request, response, chain, authResult);
    }

    // CUANDO LAS CREDENCIALES SON INVALIDAS
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", "Credenciales inválidas");

        ObjectMapper objectMapper = new ObjectMapper();
        String errorDetailsJson = objectMapper.writeValueAsString(errorDetails);

        response.getWriter().write(errorDetailsJson);
        response.getWriter().flush();
    }

}
