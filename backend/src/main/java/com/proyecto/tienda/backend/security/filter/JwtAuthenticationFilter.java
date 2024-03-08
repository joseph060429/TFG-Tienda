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
import java.util.Map;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.User;

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
            password = usuario.getPassword().trim();

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

        // Obtenemos el token
        String token = jwtUtils.generateJwtToken(user.getUsername());

        response.addHeader("Authorization", token);

        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("Message", "Autenticacion Correcta");
        httpResponse.put("user", user);

        // Convertir el mapa en el json
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush();

        super.successfulAuthentication(request, response, chain, authResult);
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
