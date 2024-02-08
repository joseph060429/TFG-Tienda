package com.proyecto.tienda.backend.service.AdminServicio;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.models.Roles;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

public interface AdminServicio {

    List<UsuarioModelo> listarUsuarios();

    UsuarioModelo listarUnUsuario(String _id);

    String eliminarUsuarioSiendoAdmin(String email);

    ResponseEntity<?> actualizarRolUsuario(String usuarioId, Set<String> nuevosRoles);

    String actualizarUsuarioSiendoAdmin(String userId, UsuarioActualizacionDTO actualizarUsuarioDTO, String token,
            JwtUtils jwtUtils);

     Set<Roles> obtenerRolesPorNombresExistentes(Set<String> nombresRoles);

}
