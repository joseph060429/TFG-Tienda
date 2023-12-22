package com.proyecto.tienda.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.UsuarioServicio;


@RestController
@RequestMapping("/usuarios")
public class UsuariosControllers {

    // @Autowired
    // private UsuarioRepositorio usuarioRepositorio;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // Esto es una interfaz de spring security
    // @Autowired
    // private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @DeleteMapping("/borrarUsuario")
    public String deleteUser(@RequestHeader("Authorization") String token) {
        return usuarioServicio.eliminarUsuarioSiendoUsuario(token, jwtUtils);
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





 // Metodo para borrar el usuario
    // @DeleteMapping("/borrarUsuario")
    // public ResponseEntity<String> borrarUsuario(@RequestBody UsuarioDTO
    // borrarUsuarioDTO, @RequestHeader("Authorization") String authorizationHeader)
    // {

    // String email = borrarUsuarioDTO.getEmail();

    // if (jwtUtils.isTokenValid(authorizationHeader)) {

    // Optional<Usuarios> usuarioOptional = usuarioRepositorio.findByEmail(email);

    // if (usuarioOptional.isPresent()) {
    // Usuarios usuario = usuarioOptional.get();

    // String userIdFromToken = jwtUtils.getUserIdFromToken(authorizationHeader);

    // if (userIdFromToken != null && userIdFromToken.equals(usuario.get_id())) {
    // // Los IDs coinciden, puedes proceder con la eliminación del usuario
    // usuarioRepositorio.delete(usuario);
    // return ResponseEntity.ok("Usuario eliminado correctamente");
    // } else {
    // return ResponseEntity.badRequest().body("El token no tiene permisos para
    // eliminar este usuario");
    // }
    // } else {
    // return ResponseEntity.badRequest().body("No se encontró un usuario con ese
    // email");
    // }
    // } else {
    // return ResponseEntity.badRequest().body("Token inválido o expirado");
    // }
    // }

    // Actualizar el usuario siendo Usuario es el DTO
    // @PutMapping("/actualizarUsuario")
    // public ResponseEntity<String> actualizarUsuario(@RequestBody UsuarioDTO
    // actualizarUsuarioDTO) {

    // String email = actualizarUsuarioDTO.getEmail();
    // String currentPassword = actualizarUsuarioDTO.getPassword(); // Contraseña
    // actual

    // Optional<Usuarios> usuarioOptional = usuarioRepositorio.findByEmail(email);

    // if (usuarioOptional.isPresent()) {

    // Usuarios usuario = usuarioOptional.get();

    // // Verificar la contraseña actual antes de actualizar
    // if (BCrypt.checkpw(currentPassword, usuario.getPassword())) {

    // // Actualizar todos los campos excepto email y fecha de creación
    // usuario.setNombre(actualizarUsuarioDTO.getNombre());
    // usuario.setApellido(actualizarUsuarioDTO.getApellido());
    // usuario.setRoles(null);(actualizarUsuarioDTO.getRoles());
    // usuario.setDireccionEnvio(actualizarUsuarioDTO.getDireccionEnvio());

    // // Actualizar la fecha de modificación
    // usuario.setFechaModificacion();

    // usuarioRepositorio.save(usuario);
    // return ResponseEntity.ok("Usuario actualizado correctamente");
    // } else {
    // return ResponseEntity.badRequest().body("La contraseña actual es
    // incorrecta");
    // }

    // } else {
    // return ResponseEntity.badRequest().body("No se encontró un usuario con ese
    // email");
    // }
    // }
