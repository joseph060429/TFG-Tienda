package com.proyecto.tienda.backend.controllers.Usuario;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.proyecto.tienda.backend.DTO.DTOUsuario.RolesDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.UtilEnum.ERol;
import com.proyecto.tienda.backend.models.RolesModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.UsuarioDetailsServiceImpl;
import com.proyecto.tienda.backend.service.UsuarioServicio.UsuarioServicio;
// import com.proyecto.tienda.backend.util.Gmailer;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasAnyRole('USER') || hasAnyRole('ADMIN')")
public class UsuariosControllers {

    @Autowired
    private UsuarioDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // CONTROLADOR PARA BORRAR UN USUARIO SIENDO USUARIO
    @DeleteMapping("/borrarUsuario")
    public String borrarUsuario(@RequestHeader("Authorization") String token) {
       return usuarioServicio.eliminarUsuarioSiendoUsuario(token, jwtUtils);
    }

    // CONTROLADOR PARA ACTUALIZAR EL USUARIO SIENDO USUARIO
    @PatchMapping("/actualizarUsuario")
    public String actualizarUsuario(
            @RequestBody @Valid UsuarioActualizacionDTO actualizarUsuarioDTO,
            @RequestHeader("Authorization") String token) {

        return usuarioServicio.actualizarUsuario(actualizarUsuarioDTO, token, jwtUtils);
    }

    // CONTROLADOR PARA REFRESCAR EL TOKEN
    @GetMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Quitamos "Bearer " del encabezado Authorization

            if (jwtUtils.isTokenValid(token)) {
                String email = jwtUtils.getEmailFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email); // Obtenemos los detalles del
                                                                                        // usuario desde Spring Security

                // Creamos un nuevo token de autenticación
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                        null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                System.out.println("Refresh token: " + token);

                // Devolvemos una ResponseEntity con el token de actualización
                return ResponseEntity.ok("Refresh token: " + token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token de autorización no encontrado");
        }
    }
}
