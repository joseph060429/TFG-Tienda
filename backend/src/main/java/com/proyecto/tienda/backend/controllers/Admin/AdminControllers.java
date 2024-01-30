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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.proyecto.tienda.backend.DTO.DTOProducto.CrearProductoDTO;
// import com.proyecto.tienda.backend.controllers.request.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.models.*;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.AdminServicio.AdminServicio;
import com.proyecto.tienda.backend.service.ProductoServicio.ProductoServicio;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Con esto ya me pilla que los que pueden entrar aqui solo seran los admin
public class AdminControllers {

    @Autowired
    private AdminServicio adminServicio;


    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private JwtUtils jwtUtils;

    // Metodo para listar todos los usuarios (Este metodo solo se lo he implemenatdo
    // al Admin)
    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<UsuarioModelo>> listarUsuarios() {
        List<UsuarioModelo> usuarios = adminServicio.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Metodo para borrar usuario siendo Admin
    @DeleteMapping("/borrarUsuario")
    public ResponseEntity<String> eliminarUsuario(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String resultado = adminServicio.eliminarUsuarioSiendoAdmin(email);
        return ResponseEntity.ok(resultado);
    }

    // Metodo para actualizar el rol de un usuario
    @PatchMapping("/{id}/actualizarRol")
    public ResponseEntity<?> actualizarRolUsuario(@PathVariable("id") String usuarioId,
            @RequestBody Map<String, String> requestBody) {
        String nuevoRol = requestBody.get("nombreRol");

        Set<String> nuevosRoles = new HashSet<>();
        nuevosRoles.add(nuevoRol);

        return adminServicio.actualizarRolUsuario(usuarioId, nuevosRoles);
    }

    // Metodo para actualizar el PERFIL de un usuario SIENDO ADMIN
    @PatchMapping("/{id}/actualizarUsuario")
    public ResponseEntity<String> actualizarUsuario(@PathVariable String id,
            @RequestBody  @Valid UsuarioActualizacionDTO actualizarUsuarioDTO,
            @RequestHeader("Authorization") String token) {

        String mensaje = adminServicio.actualizarUsuarioSiendoAdmin(id, actualizarUsuarioDTO, token, jwtUtils);
        return ResponseEntity.ok(mensaje);
    }

 



}
