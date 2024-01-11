package com.proyecto.tienda.backend.controllers;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.proyecto.tienda.backend.controllers.request.UsuarioActualizacionDTO;

import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.UsuarioServicio;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasAnyRole('USER')") 
public class UsuariosControllers {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioServicio usuarioServicio;

    //Metodo para borrar el usuario siendo usuario
    @DeleteMapping("/borrarUsuario")
    public String borrarUsuario(@RequestHeader("Authorization") String token) {
        return usuarioServicio.eliminarUsuarioSiendoUsuario(token, jwtUtils);
    }

    //Metodo para actualizar el usuario siendo usuario
    @PatchMapping("/actualizarUsuario")
    public String actualizarUsuario(
            @RequestBody @Valid UsuarioActualizacionDTO actualizarUsuarioDTO,
            @RequestHeader("Authorization") String token) {
    
        return usuarioServicio.actualizarUsuario(actualizarUsuarioDTO, token, jwtUtils);
    }
}

// public ResponseEntity<String> actualizarUsuario(@RequestBody UsuarioDTO
// actualizarUsuarioDTO) {

// String email = actualizarUsuarioDTO.getEmail();

// Optional<Usuarios> usuarioOptional = usuarioRepositorio.findByEmail(email);

// if (usuarioOptional.isPresent()) {
// Usuarios usuario = usuarioOptional.get();

// // Actualizo los campos que sean necesarios
// usuario.setNombre(actualizarUsuarioDTO.getNombre());
// usuario.setApellido(actualizarUsuarioDTO.getApellido());
// usuario.setEmail(actualizarUsuarioDTO.getEmail());
// usuario.setPassword(passwordEncoder.encode(actualizarUsuarioDTO.getPassword()));
// // Encripto la nueva contraseña

// //Guardo el usuario
// usuarioRepositorio.save(usuario);

// return ResponseEntity.ok("Usuario actualizado correctamente");
// } else {
// return ResponseEntity.notFound().build();
// }

// }

// Metodo para borrar el usuario

// @DeleteMapping("/borrarUsuario")
// public String deleteUser(@RequestHeader("Authorization") String token) {
// String jwtToken = token.replace("Bearer ", "");
// String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);
// Optional<Usuarios> usuarioOptional =
// usuarioRepositorio.findByEmail(emailFromToken);

// if (usuarioOptional.isPresent()) {
// Usuarios usuario = usuarioOptional.get();

// if (usuario.getEmail().equals(emailFromToken)) {
// usuarioRepositorio.deleteBy_id(usuario.get_id());
// return "Usuario eliminado correctamente";
// } else {
// return "No estás autorizado para eliminar este usuario";
// }
// } else {
// return "Usuario no encontrado";
// }
// }





 