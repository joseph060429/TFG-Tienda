package com.proyecto.tienda.backend.controllers.Admin;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.models.*;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.AdminServicio.AdminServicio;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Con esto ya me pilla que los que pueden entrar aqui solo seran los admin
public class AdminControllers {

    @Autowired
    private AdminServicio adminServicio;

    @Autowired
    private JwtUtils jwtUtils;

    // CONTROLADOR PARA LISTAR TODOS LOS USUARIOS (ESTE MÉTODO SOLO SE LO HE IMPLEMENTADO
    // AL ADMIN)
    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<UsuarioModelo>> listarUsuarios() {
        List<UsuarioModelo> usuarios = adminServicio.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // CONTROLADOR PARA LISTAR UN USUARIO POR ID
    @GetMapping("/listarUsuario")
    public ResponseEntity<?> listarUnUsuario(@RequestParam("id") String usuarioId) {

        UsuarioModelo usuario = adminServicio.listarUnUsuario(usuarioId);

        if (usuario != null) {
            return ResponseEntity.ok(usuario); // Devuelve el usuario encontrado
        } else {
            String mensaje = "Usuario no encontrado para el ID: " + usuarioId;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
        }
    }

    // CONTROLADOR PARA BORRAR UN USUARIO SIENDO ADMIN
    @DeleteMapping("/borrarUsuario")
    public ResponseEntity<String> eliminarUsuario(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String resultado = adminServicio.eliminarUsuarioSiendoAdmin(email);
        return ResponseEntity.ok(resultado);
    }

    // CONTROLADOR PARA ACTUALIZAR EL ROL DE UN USUARIO
    @PatchMapping("/actualizarRolUsuario")
    public ResponseEntity<?> actualizarRolUsuario(@RequestParam("id") String usuarioId,
            @RequestBody Map<String, String> requestBody) {
        String nuevoRol = requestBody.get("nombreRol");

        Set<String> nuevosRoles = new HashSet<>();
        nuevosRoles.add(nuevoRol);

        return adminServicio.actualizarRolUsuario(usuarioId, nuevosRoles);
    }

    // CONTROLADOR PARA ACTUALIZAR EL PERFIL DE UN USUARIO SIENDO ADMIN
    @PatchMapping("/actualizarUsuario")
    public ResponseEntity<String> actualizarUsuario(@RequestParam("id") String usuarioId,
            @RequestBody @Valid UsuarioActualizacionDTO actualizarUsuarioDTO,
            @RequestHeader("Authorization") String token) {

        String mensaje = adminServicio.actualizarUsuarioSiendoAdmin(usuarioId, actualizarUsuarioDTO, token, jwtUtils);
        return ResponseEntity.ok(mensaje);
    }

}
