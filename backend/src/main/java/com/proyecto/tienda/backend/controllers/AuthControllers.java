package com.proyecto.tienda.backend.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.controllers.request.UsuarioDTO;
import com.proyecto.tienda.backend.service.AuthServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class AuthControllers {

    @Autowired
    private AuthServicio authServicio;


    @GetMapping("/hello")
    public String hello() {
        return "Hello World SIN SEGURIDAD";
    }

    @GetMapping("/helloSeguridad")
    @PreAuthorize("#userId == authentication.principal.id")
    public String helloSeguridad() {
        return "Hello World CON SEGURIDAD";
    }

    @PostMapping("/crearUsuario")
    public ResponseEntity<?> crearUsuario(@RequestBody @Valid UsuarioDTO crearUsuarioDTO) {
        return authServicio.crearNuevoUsuario(crearUsuarioDTO);
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




