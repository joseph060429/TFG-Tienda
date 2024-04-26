package com.proyecto.tienda.backend.controllers.Usuario;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.UsuarioServicio.UsuarioServicio;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasAnyRole('USER') || hasAnyRole('ADMIN')")
public class UsuariosControllers {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioServicio usuarioServicio;


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

}
