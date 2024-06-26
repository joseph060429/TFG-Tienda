package com.proyecto.tienda.backend.controllers.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;

import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.UsuarioServicio.UsuarioServicio;
// import com.proyecto.tienda.backend.util.Gmailer;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasAnyRole('USER')")
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

    // Metodo para enviar contraseña de recuperación
    // @PostMapping("/enviar-correo")
    // public String enviarCorreoRecuperacion(@RequestBody CrearUsuarioDTO
    // crearUsuarioDTO) {
    // try {
    // // Accede a crearUsuarioDTO.getEmail() para obtener el correo del usuario
    // usuarioServicio.recuperarContraseña(crearUsuarioDTO.getEmail());
    // return "Correo de recuperación enviado con éxito.";
    // } catch (Exception e) {
    // return "Error al enviar el correo de recuperación: " + e.getMessage();
    // }
    // }

}
