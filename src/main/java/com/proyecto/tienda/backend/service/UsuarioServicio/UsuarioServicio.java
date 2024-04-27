package com.proyecto.tienda.backend.service.UsuarioServicio;

import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

public interface UsuarioServicio{

    // METODO PARA ELIMINAR  USUARIO
    String eliminarUsuarioSiendoUsuario(String token, JwtUtils jwtUtils);

    // METODO PARA ACTUALIZAR USUARIO
    String actualizarUsuario(UsuarioActualizacionDTO actualizarUsuarioDTO, String token, JwtUtils jwtUtils);

}
