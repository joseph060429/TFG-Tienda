package com.proyecto.tienda.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.models.*;
import java.util.Optional;

@Service
public class UsuarioServicio {
    
    // @Autowired
    // private JwtUtils jwtutils;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;


    //Metodo para eliminar el usuario SIENDO USUARIO
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



  

    
}
