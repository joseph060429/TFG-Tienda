package com.proyecto.tienda.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.controllers.request.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.models.*;
import java.util.Optional;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Metodo para eliminar el usuario SIENDO USUARIO
    public String eliminarUsuarioSiendoUsuario(String token, JwtUtils jwtUtils) {
        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

        Optional<Usuarios> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        if (usuarioOptional.isPresent()) {
            Usuarios usuario = usuarioOptional.get();
            if (usuario.getEmail().equals(emailFromToken)) {
                usuarioRepositorio.deleteBy_id(usuario.get_id());
                return "Usuario eliminado correctamente";
            } else {
                return "No estás autorizado para eliminar este usuario";
            }
        } else {
            return "Usuario no encontrado";
        }
    }

    // Método para actualizar un usuario
    public String actualizarUsuario(@RequestBody UsuarioActualizacionDTO actualizarUsuarioDTO, String token, JwtUtils jwtUtils) {
   
    String jwtToken = token.replace("Bearer ", "");
    String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);
  
    Optional<Usuarios> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

    if (usuarioOptional.isPresent()) {
        Usuarios usuario = usuarioOptional.get();

        // Validar y actualizar los campos que sean diferentes de null
        if (actualizarUsuarioDTO.getNombre() != null && !actualizarUsuarioDTO.getNombre().isEmpty()) {
            usuario.setNombre(actualizarUsuarioDTO.getNombre());
        }

        if (actualizarUsuarioDTO.getApellido() != null && !actualizarUsuarioDTO.getApellido().isEmpty()) {
            usuario.setApellido(actualizarUsuarioDTO.getApellido());
        }

        if (actualizarUsuarioDTO.getEmail() != null && !actualizarUsuarioDTO.getEmail().isEmpty()) {
            // Validar que el nuevo email no exista
            Optional<Usuarios> existeEmail = usuarioRepositorio.findByEmail(actualizarUsuarioDTO.getEmail());
            if (existeEmail.isPresent()) {
                return "El email ya esta en uso";
            }
            usuario.setEmail(actualizarUsuarioDTO.getEmail());
        }

        if (actualizarUsuarioDTO.getPassword() != null && !actualizarUsuarioDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(actualizarUsuarioDTO.getPassword()));
        }

        // Actualizar la fecha de modificación
        actualizarUsuarioDTO.setFechaModificacion();
        usuario.setFechaModificacion(actualizarUsuarioDTO.getFechaModificacion());

        // Guardar los cambios en la base de datos
        usuarioRepositorio.save(usuario);

        return "Usuario actualizado correctamente";
    } else {
        return "No se pudo actualizar el usuario debido a un problema desconocido";
    }
}

}
