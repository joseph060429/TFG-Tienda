package com.proyecto.tienda.backend.service.AdminServicio;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.models.RolesModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

public interface AdminServicio {

    // METODO PARA LISTAR TODOS LOS USUARIOS
    List<UsuarioModelo> listarUsuarios();

    // METODO PARA LISTAR UN USUARIO POR ID
    UsuarioModelo listarUnUsuario(String _id);

    // METODO PARA BORRAR UN USUARIO POR EMAIL
    String eliminarUsuarioSiendoAdmin(String email);

    // METODO PARA ACTUALIZAR EL ROL DE UN USUARIO
    ResponseEntity<?> actualizarRolUsuario(String usuarioId, Set<String> nuevosRoles);

    // METODO PARA ACTUALIZAR EL PERFIL DE UN USUARIO SIENDO ADMIN
    String actualizarUsuarioSiendoAdmin(String userId, UsuarioActualizacionDTO actualizarUsuarioDTO, String token,
            JwtUtils jwtUtils);
    
    // METODO PARA OBTENER LOS NOMBRES DE LOS ROLES EXISTENTES
     Set<RolesModelo> obtenerRolesPorNombresExistentes(Set<String> nombresRoles);

}
