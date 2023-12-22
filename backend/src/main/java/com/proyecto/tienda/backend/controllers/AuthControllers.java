package com.proyecto.tienda.backend.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.controllers.request.UsuarioDTO;
import com.proyecto.tienda.backend.models.ERol;
import com.proyecto.tienda.backend.models.Roles;
import com.proyecto.tienda.backend.models.Usuarios;
import com.proyecto.tienda.backend.repositorios.RolesRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class AuthControllers {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolesRepositorio rolesRepositorio;

    // UsuarioControllers

    @GetMapping("/hello")
    public String hello() {
        return "Hello World SIN SEGURIDAD";
    }

    @GetMapping("/helloSeguridad")
    @PreAuthorize("#userId == authentication.principal.id")
    public String helloSeguridad() {
        return "Hello World CON SEGURIDAD";
    }

    // @PostMapping("/crearUsuario")
    // public ResponseEntity<?> crearUsuario(@RequestBody @Valid UsuarioDTO crearUsuarioDTO) {

    //     if (usuarioRepositorio.existsByEmail(crearUsuarioDTO.getEmail())) {
    //         return ResponseEntity.badRequest().body("El email ya existe");
    //     }

    //     Set<Roles> roles = new HashSet<>();

    //     // Verifico si no se encuentran roles y asigno el rol por defecto "user"
    //     if (crearUsuarioDTO.getRoles() == null || crearUsuarioDTO.getRoles().isEmpty()) {
    //         Roles defaultRole = Roles.builder().name(ERol.USER).build();//Cambiar esto por si me cargo el ADMIN cd
    //         defaultRole.setId(UUID.randomUUID().toString());
    //         rolesRepositorio.save(defaultRole); // Guardo el rol por defecto en la base de datos para el usuario
    //         roles.add(defaultRole);
    //     } else {
    //         // Asigno los roles proporcionados en el DTO
    //         roles = crearUsuarioDTO.getRoles().stream()
    //                 .map(rol -> {
    //                     Roles nuevoRol = Roles.builder()
    //                             .name(ERol.valueOf(rol))
    //                             .build();
    //                     nuevoRol.setId(UUID.randomUUID().toString());
    //                     nuevoRol = rolesRepositorio.save(nuevoRol);
    //                     return nuevoRol;
    //                 })
    //                 .collect(Collectors.toSet());
    //     }

    //     Usuarios usuario = Usuarios.builder()
    //             .nombre(crearUsuarioDTO.getNombre())
    //             .apellido(crearUsuarioDTO.getApellido())
    //             .email(crearUsuarioDTO.getEmail())
    //             .password(passwordEncoder.encode(crearUsuarioDTO.getPassword()))
    //             .direccionEnvio("")
    //             .fechaModificacion("")
    //             .roles(roles)
    //             .build();

    //     // Convierto el Id de mi usuario de mongo en un UUID
    //     if (usuario.get_id() == null) {
    //         usuario.set_id(UUID.randomUUID().toString());
    //     }

    //     // Guardo la fecha de creacion antes de insertar el usuario en la base de datos
    //     UsuarioDTO usuarioDTO = new UsuarioDTO();
    //     usuarioDTO.setFechaCreacion();
    //     usuario.setFechaCreacion(usuarioDTO.getFechaCreacion());

    //     // Guardo el usuario
    //     usuarioRepositorio.save(usuario);

    //     return ResponseEntity.ok(usuario);
    // }

    @PostMapping("/crearUsuario")
public ResponseEntity<?> crearUsuario(@RequestBody @Valid UsuarioDTO crearUsuarioDTO) {
    if (usuarioRepositorio.existsByEmail(crearUsuarioDTO.getEmail())) {
        return ResponseEntity.badRequest().body("El email ya existe");
    }

    Set<Roles> roles = new HashSet<>();

    // Verifico si no se encuentran roles y asigno el rol por defecto "USER"
    if (crearUsuarioDTO.getRoles() == null || crearUsuarioDTO.getRoles().isEmpty()) {
        Roles defaultRole = new Roles();
        defaultRole.setName(ERol.USER); // Establecer el nombre del rol como USER
        defaultRole.setId(UUID.randomUUID().toString());
        rolesRepositorio.save(defaultRole); // Guardar el rol por defecto en la base de datos para el usuario
        roles.add(defaultRole);
    } else {
        // Asignar los roles proporcionados en el DTO
        roles = crearUsuarioDTO.getRoles().stream()
                .map(rol -> {
                    Roles nuevoRol = new Roles();
                    nuevoRol.setName(ERol.valueOf(rol)); // Establecer el nombre del rol desde el enum ERol
                    nuevoRol.setId(UUID.randomUUID().toString());
                    return rolesRepositorio.save(nuevoRol);
                })
                .collect(Collectors.toSet());
    }

    // Crear el usuario
    Usuarios usuario = Usuarios.builder()
            .nombre(crearUsuarioDTO.getNombre())
            .apellido(crearUsuarioDTO.getApellido())
            .email(crearUsuarioDTO.getEmail())
            .password(passwordEncoder.encode(crearUsuarioDTO.getPassword()))
            .direccionEnvio("")
            .fechaModificacion("")
            .roles(roles)
            .build();

    // Convierte el ID de usuario de mongo en un UUID
    if (usuario.get_id() == null) {
        usuario.set_id(UUID.randomUUID().toString());
    }

    // Guardar la fecha de creación antes de insertar el usuario en la base de datos
    UsuarioDTO usuarioDTO = new UsuarioDTO();
    usuarioDTO.setFechaCreacion();
    usuario.setFechaCreacion(usuarioDTO.getFechaCreacion());

    // Guardar el usuario
    usuarioRepositorio.save(usuario);

    return ResponseEntity.ok(usuario);
}


}

// Este metodo es para crear un nuevo rol por si me cargo por casualidad el
// ADMIN xd

// Set<Roles> roles = crearUsuarioDTO.getRoles().stream()
// .map(rol -> {
// Roles nuevoRol = Roles.builder()
// .name(ERol.valueOf(rol))
// .build();

// //Asigno un UUID a mi ID de roles
// nuevoRol.setId(UUID.randomUUID().toString());

// //Guardo el rol con el nuevo ID
// nuevoRol = rolesRepositorio.save(nuevoRol);
// return nuevoRol;
// })
// .collect(Collectors.toSet());




