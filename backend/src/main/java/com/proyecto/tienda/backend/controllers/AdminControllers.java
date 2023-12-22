package com.proyecto.tienda.backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.service.AdminServicio;
import com.proyecto.tienda.backend.models.*;;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Con esto ya me pilla que los que pueden entrar aqui solo seran los admin
public class AdminControllers {

    @Autowired
    private AdminServicio adminServicio;

    // Metodo para listar todos los usuarios (Este metodo solo se lo he implemenatdo
    // al Admin)
    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<Usuarios>> listarUsuarios() {
        List<Usuarios> usuarios = adminServicio.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Metodo para borrar usuario siendo Admin
    @DeleteMapping("/borrarUsuario")
    public ResponseEntity<String> eliminarUsuario(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String resultado = adminServicio.eliminarUsuarioSiendoAdmin(email);
        return ResponseEntity.ok(resultado);
    }



    
    // REVISAR EL METODO ACTUALIZAR Y EL SERVICIO ACTUALIZAR
    // @PatchMapping("/{id}/editarRol")
    // public ResponseEntity<String> editarRolUsuario(@PathVariable String id, @RequestBody List<ERol> nuevosRoles) {
    //     String mensaje = adminServicio.editarRolUsuario(id, nuevosRoles);
    //     if (mensaje.equals("No existe el usuario en la base de datos")) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     return ResponseEntity.ok(mensaje);
    // }

}
